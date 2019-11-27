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
import cz.applifting.humansis.synchronization.ERROR_MESSAGE_KEY
import cz.applifting.humansis.synchronization.MANUAL_SYNC_WORKER
import cz.applifting.humansis.synchronization.SyncWorker
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
    context: Context
) : BaseViewModel() {

    val toastLD = MediatorLiveData<String>()
    val forceOfflineReloadLD = MutableLiveData<Boolean>()
    val lastDownloadLD = MutableLiveData<Date>()
    val lastSyncFailedLD = MutableLiveData<Date>()
    val pendingChangesLD = MediatorLiveData<Boolean>()
    val networkStatus = MutableLiveData<Boolean>()

    val syncWorkerIsLoadingLD: MediatorLiveData<Boolean> = MediatorLiveData()
    private val workInfosLD: LiveData<List<WorkInfo>>

    private val workManager = WorkManager.getInstance(context)

    init {
        syncWorkerIsLoadingLD.value = false

        lastDownloadLD.value = sp.getDate(LAST_DOWNLOAD_KEY)
        lastSyncFailedLD.value = sp.getDate(LAST_SYNC_FAILED_KEY)

        workInfosLD = workManager.getWorkInfosForUniqueWorkLiveData(MANUAL_SYNC_WORKER)
        syncWorkerIsLoadingLD.addSource(
            workInfosLD
        ) {
            if (it.isNullOrEmpty()) {
                return@addSource
            }
            launch { logger.logToFile(context, "Worker state: ${it.first().state}") }
            syncWorkerIsLoadingLD.value = !it.first().state.isFinished
        }

        toastLD.addSource(workInfosLD) {
            lastDownloadLD.value = sp.getDate(LAST_DOWNLOAD_KEY)
            lastSyncFailedLD.value = sp.getDate(LAST_SYNC_FAILED_KEY)

            if (it.isNullOrEmpty()) {
                toastLD.value = null
                return@addSource
            }

            if (it.first().state == WorkInfo.State.FAILED) {
                val errors = it.first().outputData.getStringArray(ERROR_MESSAGE_KEY)
                val error = errors?.joinToString("\n")

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
        if (sp.getDate(LAST_DOWNLOAD_KEY) == null) {
            synchronize()
        }
    }

    fun showToast(text: String?) {
        toastLD.value = text
    }
}