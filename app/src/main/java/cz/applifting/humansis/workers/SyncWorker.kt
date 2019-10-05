package cz.applifting.humansis.workers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cz.applifting.humansis.extensions.setDate
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.ui.App
import cz.applifting.humansis.ui.main.LAST_DOWNLOAD_KEY
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.*
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 05, October, 2019
 */
const val SYNC_WORKER = "sync-worker"
const val PERIODIC_SYNC_WORKER = "periodic-sync-worker"

class SyncWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    @Inject
    lateinit var projectsRepository: ProjectsRepository
    @Inject
    lateinit var distributionsRepository: DistributionsRepository
    @Inject
    lateinit var beneficieriesRepository: BeneficieriesRepository
    @Inject
    lateinit var sp: SharedPreferences

    init {
        (appContext as App).appComponent.inject(this)
    }

    override suspend fun doWork(): Result {
        return coroutineScope {
            val unsuccessfullyUploaded = mutableListOf<Int>()
            val errors = mutableListOf<Throwable>()

            Log.d("asdf", "uploadig...")

            // Upload all changes
            getAllBeneficiaries()
                .forEach {
                    if (it.edited && it.distributed) {
                        try {
                            beneficieriesRepository.distribute(it.id)
                        } catch (e: Throwable) {
                            errors.add(e)
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
                errors.add(e)
            }

            if (errors.isEmpty()) {
                Result.success()
            } else {
                Result.failure()
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
