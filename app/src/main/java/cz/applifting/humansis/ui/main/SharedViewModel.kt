package cz.applifting.humansis.ui.main

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.getDate
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.ui.BaseViewModel
import cz.applifting.humansis.workers.SYNC_WORKER
import cz.applifting.humansis.workers.SyncWorker
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
    private val sp: SharedPreferences,
    context: Context
) : BaseViewModel() {

    val snackbarLD = MediatorLiveData<String>()
    val forceOfflineReloadLD = MutableLiveData<Boolean>()
    val lastDownloadLD = MutableLiveData<Date>()
    val pendingChangesLD = MediatorLiveData<Boolean>()

    val syncWorkerIsLoadingLD: MediatorLiveData<Boolean> = MediatorLiveData()
    private val workInfosLD: LiveData<List<WorkInfo>>


    private val workManager = WorkManager.getInstance(context)

    init {
        lastDownloadLD.value = sp.getDate(LAST_DOWNLOAD_KEY)
        workInfosLD = workManager.getWorkInfosForUniqueWorkLiveData(SYNC_WORKER)
        syncWorkerIsLoadingLD.addSource(
            workInfosLD
        ) {
            if (it.isNullOrEmpty()) {
                return@addSource
            }
            syncWorkerIsLoadingLD.value = !it.first().state.isFinished
        }

        // TODO check if this is a correct usage of mediator liveData
        pendingChangesLD.addSource(workInfosLD) { refreshPendingChanges() }
        
        snackbarLD.addSource(workInfosLD) {
            if (it.isNullOrEmpty()) {
                return@addSource
            }

            if (it.first().state == WorkInfo.State.FAILED) {
                snackbarLD.value = context.getString(R.string.error_message)
            }
        }
    }

    fun synchronize() {
        launch {
            workManager.beginUniqueWork(SYNC_WORKER, ExistingWorkPolicy.KEEP, OneTimeWorkRequest.from(SyncWorker::class.java)).enqueue()
            forceOfflineReloadLD.value = true

        }
    }

    fun tryFirstDownload() {
        if (sp.getDate(LAST_DOWNLOAD_KEY) == null) {
            synchronize()
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
