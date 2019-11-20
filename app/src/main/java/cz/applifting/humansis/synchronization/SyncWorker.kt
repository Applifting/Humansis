package cz.applifting.humansis.synchronization

import android.content.Context
import android.content.SharedPreferences
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import cz.applifting.humansis.extensions.setDate
import cz.applifting.humansis.managers.LoginManager
import cz.applifting.humansis.misc.Logger
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.model.db.DistributionLocal
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.ui.App
import cz.applifting.humansis.ui.main.LAST_DOWNLOAD_KEY
import cz.applifting.humansis.ui.main.LAST_SYNC_FAILED_KEY
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 05, October, 2019
 */
const val MANUAL_SYNC_WORKER = "manual-sync-worker"
const val PERIODIC_SYNC_WORKER = "periodic-sync-worker"
const val WHEN_ON_WIFI_SYNC_WORKER = "when-on-wifi-sync-worker"

const val ERROR_MESSAGE_KEY = "error-message-key"

class SyncWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    @Inject
    lateinit var projectsRepository: ProjectsRepository
    @Inject
    lateinit var distributionsRepository: DistributionsRepository
    @Inject
    lateinit var beneficieriesRepository: BeneficieriesRepository
    @Inject
    lateinit var sp: SharedPreferences
    @Inject
    lateinit var loginManager: LoginManager
    @Inject
    lateinit var logger: Logger


    init {
        (appContext as App).appComponent.inject(this)
    }

    override suspend fun doWork(): Result {
        return supervisorScope {
            val unsuccessfullyUploaded = mutableListOf<Int>()
            val errors = mutableListOf<String?>()
            val reason = Data.Builder()

            logger.logToFile(applicationContext, "Started Sync")

            if (!loginManager.tryInitDB()) {
                reason.putStringArray(
                    ERROR_MESSAGE_KEY,
                    arrayOf("Could not read DB.")
                )
                logger.logToFile(applicationContext, "Failed to read db")
                return@supervisorScope Result.failure(reason.build())
            }

            // Upload all changes
            getAllBeneficiaries()
                .forEach {
                    if (it.edited && it.distributed) {
                        try {
                            beneficieriesRepository.distribute(it.id)
                        } catch (e: HttpException) {
                            val errBody = "${e.response()?.errorBody()?.string()}"
                            errors.add("${it.id}: $errBody")
                            logger.logToFile(applicationContext, "Failed uploading: ${it.id}: $errBody")
                            unsuccessfullyUploaded.add(it.id)
                        }

                    }
                }
            // Download updated data
            try {
                projectsRepository
                    .getProjectsOnline().orEmpty()
                    .map {
                        async { distributionsRepository.getDistributionsOnline(it.id) }
                    }
                    .flatMap {
                        try {
                            it.await() ?: listOf()
                        } catch (e: HttpException) {
                            logger.logToFile(applicationContext, "Failed downloading distribution ${e.code()} ${e.message()} ${e.response().toString()}")
                            listOf<DistributionLocal>()
                        }
                    }
                    .map { async { beneficieriesRepository.getBeneficieriesOnline(it.id, unsuccessfullyUploaded) } }
                    .map {
                        try {
                            it.await()
                        } catch (e: HttpException) {
                            logger.logToFile(applicationContext, "Failed downloading beneficiaries  ${e.code()} ${e.message()} ${e.response().toString()}")
                        }
                    }

                val lastDownloadAt = Date()
                sp.setDate(LAST_DOWNLOAD_KEY, lastDownloadAt)
                sp.setDate(LAST_SYNC_FAILED_KEY, null)

            } catch (e: HttpException) {
                errors.add(e.message)
                logger.logToFile(applicationContext, "Failed downloading: ${e.message}}")
            }

            if (errors.isEmpty()) {
                logger.logToFile(applicationContext, "Sync finished successfully")
                Result.success()
            } else {
                logger.logToFile(applicationContext, "Sync finished with failure")
                sp.setDate(LAST_SYNC_FAILED_KEY, Date())
                Result.failure(reason.putStringArray(ERROR_MESSAGE_KEY, errors.toTypedArray()).build())
            }
        }
    }

    private suspend fun getAllBeneficiaries(): List<BeneficiaryLocal> {
        return projectsRepository
            .getProjectsOfflineSuspend()
            .flatMap { distributionsRepository.getDistributionsOfflineSuspend(it.id) }
            .flatMap { beneficieriesRepository.getBeneficieriesOfflineSuspend(it.id) }
    }
}
