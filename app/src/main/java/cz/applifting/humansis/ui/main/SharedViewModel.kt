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
class SharedViewModel @Inject constructor(
    private val projectsRepository: ProjectsRepository,
    private val distributionsRepository: DistributionsRepository,
    private val beneficieriesRepository: BeneficieriesRepository,
    private val pendingChangesRepository: PendingChangesRepository,
    private val sp: SharedPreferences
) : BaseViewModel() {

    private val lastDownloadKey = "lastDownloadKey"

    val downloadingLD = MutableLiveData<Boolean>()
    val uploadDialogLD = MutableLiveData<Boolean>()
    val snackbarLD = MutableLiveData<String>()
    val forceOfflineReloadLD = MutableLiveData<Boolean>()
    val lastDownloadLD = MutableLiveData<Date>()
    val pendingChangesLD = MutableLiveData<Boolean>()

    val loadingLD = MediatorLiveData<Boolean>()

    init {
        downloadingLD.value = false
        lastDownloadLD.value = sp.getDate(lastDownloadKey)

        loadingLD.addSource(downloadingLD) { loadingLD.value = it }
        loadingLD.addSource(uploadDialogLD) { loadingLD.value = it }

        initPendingChanges()
    }

    fun tryDownloadingAll() {
        // TODO when should we download all?
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
                sp.setDate(lastDownloadKey, Date())

            } catch (e: Throwable) {

            } finally {
                downloadingLD.value = false
            }
        }

        val lastDownloadAt = Date()
        lastDownloadLD.value = lastDownloadAt
        sp.setDate(lastDownloadKey, lastDownloadAt)
    }

    fun uploadChanges() {
        launch {
            uploadDialogLD.value = true
            val pendingChanges = pendingChangesRepository.getPendingChanges()
            pendingChanges?.forEach { change ->
                beneficieriesRepository.distribute(change.beneficiaryId)
                change.id?.let {
                    pendingChangesRepository.deletePendingChange(it)
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

    private fun initPendingChanges() {
        launch {
            pendingChangesLD.value = pendingChangesRepository.hasPendingChanges()
        }
    }
}
