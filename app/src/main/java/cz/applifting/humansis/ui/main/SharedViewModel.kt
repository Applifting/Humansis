package cz.applifting.humansis.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.extensions.getDate
import cz.applifting.humansis.extensions.setDate
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.repositories.DistributionsRepository
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
        launch {
            uploadDialogLD.value = true

            getAllBeneficiaries()
                .forEach {
                    if (it.edited && it.distributed) {
                        try {
                            beneficieriesRepository.distribute(it.id)
                        } catch (e: Throwable) {
                            snackbarLD.value = "Error: ${e.message}"
                        }

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

    fun refreshPendingChanges() {
        launch {
            for (beneficiary in getAllBeneficiaries()) {
                if (beneficiary.edited) {
                    pendingChangesLD.value = true
                    return@launch
                }
            }

            pendingChangesLD.value = false
        }
    }

    private suspend fun getAllBeneficiaries(): List<BeneficiaryLocal> {
       return projectsRepository
            .getProjectsOffline()
            .flatMap { distributionsRepository.getDistributionsOffline(it.id) }
            .flatMap { beneficieriesRepository.getBeneficieriesOffline(it.id) }
    }
}
