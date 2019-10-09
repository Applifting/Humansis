package cz.applifting.humansis.synchronization

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import cz.applifting.humansis.extensions.setDate
import cz.applifting.humansis.managers.LoginManager
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.ui.App
import cz.applifting.humansis.ui.main.LAST_DOWNLOAD_KEY
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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


    init {
        (appContext as App).appComponent.inject(this)
    }

    override suspend fun doWork(): Result {
        return coroutineScope {
            val unsuccessfullyUploaded = mutableListOf<Int>()
            val errors = mutableListOf<String?>()

            Log.d("asdf", "uploadig...")

            val reason = Data.Builder()

            if (!loginManager.tryInitDB()) {
                reason.putStringArray(
                    ERROR_MESSAGE_KEY,
                    arrayOf("Could not read DB.")
                )
                return@coroutineScope Result.failure(reason.build())
            }

            // Upload all changes
            getAllBeneficiaries()
                .forEach {
                    if (it.edited && it.distributed) {
                        try {
                            beneficieriesRepository.distribute(it.id)
                        } catch (e: HttpException) {
                            errors.add("${it.id}: ${e.response()?.errorBody()?.string()}")
                            unsuccessfullyUploaded.add(it.id)
                        }

                    }
                }

            // Download updated data
            try {
                projectsRepository
                    .getProjectsOnline().orEmpty()
                    .map { async { distributionsRepository.getDistributionsOnline(it.id) } }
                    .flatMap { it.await() ?: listOf() }
                    .map { async { beneficieriesRepository.getBeneficieriesOnline(it.id, unsuccessfullyUploaded) } }
                    .map { it.await() }

                val lastDownloadAt = Date()
                sp.setDate(LAST_DOWNLOAD_KEY, lastDownloadAt)

            } catch (e: Throwable) {
                errors.add(e.message)
            }

            if (errors.isEmpty()) {
                Result.success()
            } else {
                Result.failure(reason.putStringArray(ERROR_MESSAGE_KEY, errors.toTypedArray()).build())
            }
        }
    }

    private suspend fun getAllBeneficiaries(): List<BeneficiaryLocal> {
        return projectsRepository
            .getProjectsOffline()
            .flatMap { distributionsRepository.getDistributionsOffline(it.id) }
            .flatMap { beneficieriesRepository.getBeneficieriesOffline(it.id) }
    }
}
