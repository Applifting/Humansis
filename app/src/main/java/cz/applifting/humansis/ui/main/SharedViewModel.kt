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
import cz.applifting.humansis.extensions.getDate
import cz.applifting.humansis.misc.Logger
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.synchronization.*
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 10, September, 2019
 */
const val LAST_DOWNLOAD_KEY = "lastDownloadKey"
const val LAST_SYNC_FAILED_KEY = "lastSyncFailedKey"

class SharedViewModel @Inject constructor(
    private val projectsRepository: ProjectsRepository,
    private val distributionsRepository: DistributionsRepository,
    private val beneficieriesRepository: BeneficieriesRepository,
    private val logger: Logger,
    private val sp: SharedPreferences,
    private val context: Context
) : BaseViewModel() {

    val toastLD = MediatorLiveData<String>()
    val forceOfflineReloadLD = MutableLiveData<Boolean>()
    val lastDownloadLD = MutableLiveData<Date>()
    val lastSyncFailedLD = MutableLiveData<Date>()
    val pendingChangesLD = MediatorLiveData<Boolean>()
    val networkStatus = MutableLiveData<Boolean>()

    val syncWorkerIsLoadingLD: MediatorLiveData<Boolean> = MediatorLiveData()
    private val workInfos1LD: LiveData<List<WorkInfo>>
    private val workInfos2LD: LiveData<List<WorkInfo>>
    private val workInfos3LD: LiveData<List<WorkInfo>>

    private val workManager = WorkManager.getInstance(context)

    init {
        syncWorkerIsLoadingLD.value = false

        lastDownloadLD.value = sp.getDate(LAST_DOWNLOAD_KEY)
        lastSyncFailedLD.value = sp.getDate(LAST_SYNC_FAILED_KEY)

        workInfos1LD = workManager.getWorkInfosForUniqueWorkLiveData(MANUAL_SYNC_WORKER)
        workInfos2LD = workManager.getWorkInfosForUniqueWorkLiveData(WHEN_ON_WIFI_SYNC_WORKER)
        workInfos3LD = workManager.getWorkInfosForUniqueWorkLiveData(ON_START_SYNC_WORKER)

        syncWorkerIsLoadingLD.addSource(workInfos1LD) {
            syncWorkerIsLoadingLD.value = isLoading(it)
        }

        syncWorkerIsLoadingLD.addSource(workInfos2LD) {
            syncWorkerIsLoadingLD.value = isLoading(it)
        }

        syncWorkerIsLoadingLD.addSource(workInfos3LD) {
            syncWorkerIsLoadingLD.value = isLoading(it)
        }


        // TODO toast is shown every time sync fails and user opens the app. I can not think of a simpler solution than saving the toast to persistent memory
        toastLD.addSource(workInfos1LD) {
            lastDownloadLD.value = sp.getDate(LAST_DOWNLOAD_KEY)
            lastSyncFailedLD.value = sp.getDate(LAST_SYNC_FAILED_KEY)

            if (it.isNullOrEmpty()) {
                toastLD.value = null
                return@addSource
            }

            if (it.first().state == WorkInfo.State.FAILED) {
                val errors = it.first().outputData.getStringArray(ERROR_MESSAGE_KEY)
                // show only first error in toast
                val error = errors?.first()

                toastLD.value = error
            }
        }


        launch {
            beneficieriesRepository
                .getAllBeneficieriesOffline()
                .collect {
                    val edited = it.find { it.edited }
                    pendingChangesLD.value = edited != null
                }
        }
    }

    fun synchronize() {
        launch {
            workManager.beginUniqueWork(MANUAL_SYNC_WORKER, ExistingWorkPolicy.KEEP, OneTimeWorkRequest.from(SyncWorker::class.java)).enqueue()
            forceOfflineReloadLD.value = true
        }
    }

    fun tryFirstDownload() {
        launch {
            if (sp.getDate(LAST_DOWNLOAD_KEY) == null || projectsRepository.getProjectsOfflineSuspend().isEmpty()) {
                synchronize()
            }
        }
    }

    fun showToast(text: String?) {
        toastLD.value = text
    }

    private fun isLoading(workInfos: List<WorkInfo>): Boolean {
        if (workInfos.isNullOrEmpty()) {
            return false
        }
        launch { logger.logToFile(context, "Worker state: ${workInfos.first().state}") }
        return workInfos.first().state == WorkInfo.State.RUNNING
    }
}