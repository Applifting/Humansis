package cz.applifting.humansis.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.extensions.getDate
import cz.applifting.humansis.extensions.setDate
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.repositories.PendingChangesRepository
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 10, September, 2019
 */
const val LAST_DOWNLOAD_KEY = "lastDownloadKey"

class SharedViewModel @Inject constructor(
    private val projectsRepository: ProjectsRepository,
    private val distributionsRepository: DistributionsRepository,
    private val beneficieriesRepository: BeneficieriesRepository,
    private val pendingChangesRepository: PendingChangesRepository,
    private val sp: SharedPreferences
) : BaseViewModel() {

    val downloadingLD = MutableLiveData<Boolean>()
    val uploadDialogLD = MutableLiveData<Boolean>()
    val snackbarLD = MutableLiveData<String>()
    val forceOfflineReloadLD = MutableLiveData<Boolean>()
    val lastDownloadLD = MutableLiveData<Date>()
    val pendingChangesLD = MutableLiveData<Boolean>()

    val loadingLD = MediatorLiveData<Boolean>()

    init {
        lastDownloadLD.value = sp.getDate(LAST_DOWNLOAD_KEY)
        loadingLD.addSource(downloadingLD) { loadingLD.value = it }
        loadingLD.addSource(uploadDialogLD) { loadingLD.value = it }
    }

    fun tryDownloadingAll() {
        launch {
            try {
                downloadingLD.value = true
                projectsRepository
                    .getProjectsOnline()
                    .orEmpty()
                    .map { async { distributionsRepository.getDistributionsOnline(it.id) } }
                    .flatMap { it.await() ?: listOf() }
                    .map { async { beneficieriesRepository.getBeneficieriesOnline(it.id) } }
                    .map { it.await() }

                pendingChangesLD.value = false

                val lastDownloadAt = Date()
                lastDownloadLD.value = lastDownloadAt
                sp.setDate(LAST_DOWNLOAD_KEY, lastDownloadAt)

            } catch (e: Throwable) {
                snackbarLD.value = "Error: ${e.message}"
            } finally {
                downloadingLD.value = false
                forceOfflineReloadLD.value = true
            }
        }
    }

    fun tryFirstDownload() {
        if (sp.getDate(LAST_DOWNLOAD_KEY) == null) {
            tryDownloadingAll()
        } else {
            downloadingLD.value = false
        }
    }

    fun uploadChanges() {
        /*
        TODO We should reconsider the way changes are saved. Approach with a separate table/repository allows us to store history and might enable us to use some kind
        back button functionality. This is in my opinion a little over-engineered and we need to store 'edited' flag in beneficiary table anyway.
        */

        launch {
            uploadDialogLD.value = true
            val pendingChanges = pendingChangesRepository.getPendingChanges()
            pendingChanges?.forEach { change ->
                try {
                    beneficieriesRepository.distribute(change.beneficiaryId)
                    change.id?.let {
                        pendingChangesRepository.deletePendingChange(it)
                    }
                } catch (e: Throwable) {
                    snackbarLD.value = "Error: ${e.message}"
                }

            }

            uploadDialogLD.value = false
            tryDownloadingAll()
        }
    }

    fun showSnackbar(text: String?) {
        snackbarLD.value = text
    }

    fun forceOfflineReload(force: Boolean) {
        forceOfflineReloadLD.value = force
    }

    fun markPendingChanges() {
        pendingChangesLD.value = true
    }

    fun initPendingChanges() {
        launch {
            pendingChangesLD.value = pendingChangesRepository.hasPendingChanges()
        }
    }
}
