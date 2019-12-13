package cz.applifting.humansis.synchronization

import android.content.Context
import android.content.SharedPreferences
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.setDate
import cz.applifting.humansis.managers.LoginManager
import cz.applifting.humansis.misc.Logger
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.model.db.DistributionLocal
import cz.applifting.humansis.model.db.ProjectLocal
import cz.applifting.humansis.model.db.SyncError
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.repositories.ErrorsRepository
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
const val SYNC_WORKER = "sync-worker"

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
    @Inject
    lateinit var errorsRepository: ErrorsRepository


    init {
        (appContext as App).appComponent.inject(this)
    }

    override suspend fun doWork(): Result {
        return supervisorScope {
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

            errorsRepository.clearAll()

            val syncErorrs = arrayListOf<SyncError>()

            // Upload all changes
            getAllBeneficiaries()
                .forEach {
                    if (it.edited && it.distributed) {
                        try {
                            beneficieriesRepository.distribute(it.id)
                        } catch (e: HttpException) {
                            val errBody = "${e.response()?.errorBody()?.string()}"
                            logger.logToFile(applicationContext, "Failed uploading: ${it.id}: $errBody")

                            // Mark conflicts in DB
                            val distributionName = distributionsRepository.getNameById(it.distributionId)
                            val projectName = projectsRepository.getNameByDistributionId(it.distributionId)
                            val beneficiaryName = "${it.givenName} ${it.familyName}"

                            val syncError = SyncError(
                                it.id,
                                "$projectName → $distributionName → $beneficiaryName",
                                "Humansis ID: ${it.beneficiaryId} \nNational ID: ${it.nationalId}",
                                e.code(),
                                "${e.code()}: $errBody"
                            )

                            syncErorrs.add(syncError)
                        }
                    }
                }

            // Download updated data
            if (syncErorrs.isEmpty()) {

                val projects = try {
                    projectsRepository.getProjectsOnline()
                } catch (e: Exception) {
                    syncErorrs.add(getDownloadError(e, applicationContext.getString(R.string.projects)))
                    emptyList<ProjectLocal>()
                }

                val distributions = try {
                    projects.orEmpty().map {
                        async { distributionsRepository.getDistributionsOnline(it.id) }
                    }.flatMap {
                        it.await().orEmpty().toList()
                    }
                } catch (e: Exception) {
                    syncErorrs.add(getDownloadError(e, applicationContext.getString(R.string.distribution)))
                    emptyList<ProjectLocal>()
                    listOf<DistributionLocal>()
                }

                try {
                    distributions.map {
                        async { beneficieriesRepository.getBeneficieriesOnline(it.id) }
                    }.map {
                        it.await()
                    }
                } catch (e: Exception) {
                    syncErorrs.add(getDownloadError(e, applicationContext.getString(R.string.beneficiary)))
                    emptyList<ProjectLocal>()
                }

                val lastDownloadAt = Date()
                sp.setDate(LAST_DOWNLOAD_KEY, lastDownloadAt)
                sp.setDate(LAST_SYNC_FAILED_KEY, null)
            }

            if (syncErorrs.isEmpty()) {
                logger.logToFile(applicationContext, "Sync finished successfully")
                Result.success()
            } else {
                errorsRepository.insertAll(syncErorrs)

                // Erase password to trigger re-authentication
                if (syncErorrs.find { it.code == 403 } != null) {
                    loginManager.markInvalidPassword()
                }

                logger.logToFile(applicationContext, "Sync finished with failure")
                sp.setDate(LAST_SYNC_FAILED_KEY, Date())
                Result.failure(reason.putStringArray(ERROR_MESSAGE_KEY, convertErrors(syncErorrs)).build())
            }
        }
    }

    private fun convertErrors(errors: List<SyncError>): Array<String> {
        return errors.map {
            it.errorMessage
        }.toTypedArray()
    }

    private suspend fun getAllBeneficiaries(): List<BeneficiaryLocal> {
        return projectsRepository
            .getProjectsOfflineSuspend()
            .flatMap { distributionsRepository.getDistributionsOfflineSuspend(it.id) }
            .flatMap { beneficieriesRepository.getBeneficieriesOfflineSuspend(it.id) }
    }

    private fun getErrorMessageByCode(code: Int): String {
        return applicationContext.getString(
            when (code) {
                400 -> R.string.error_bad_request
                403 -> R.string.error_user_not_allowed
                404 -> R.string.error_resource_not_found
                409 -> R.string.error_data_conflict
                410 -> R.string.error_server_api_changed
                429 -> R.string.error_too_many_requests
                in 500..599 -> R.string.error_server_failure
                else -> R.string.error_other
            }
        )
    }

    private suspend fun getDownloadError(e: Exception, resourceName: String): SyncError {
        logger.logToFile(applicationContext, "Failed downloading ${resourceName}: ${e.message}}")

        return when {
            e is HttpException -> {
                SyncError(
                    location = applicationContext.getString(R.string.download_error).format(resourceName.toLowerCase(Locale.ROOT)),
                    params = applicationContext.getString(R.string.error_server),
                    errorMessage = getErrorMessageByCode(e.code()),
                    code = e.code()
                )
            }

            else -> {
                SyncError(
                    location = applicationContext.getString(R.string.download_error).format(resourceName.toLowerCase(Locale.ROOT)),
                    params = applicationContext.getString(R.string.unknwon_error),
                    errorMessage = e.message ?: "",
                    code = 0
                )
            }
        }
    }
}
