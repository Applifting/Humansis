package cz.applifting.humansis.ui

import android.app.Application
import androidx.work.*
import cz.applifting.humansis.BuildConfig
import cz.applifting.humansis.di.AppComponent
import cz.applifting.humansis.di.DaggerAppComponent
import cz.applifting.humansis.workers.PERIODIC_SYNC_WORKER
import cz.applifting.humansis.workers.SyncWorker
import java.util.concurrent.TimeUnit.HOURS

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class App : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .context(this)
            .baseUrl(BuildConfig.API_BASE_URL)
            .build()


        // Periodically sync
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val saveRequest =
            PeriodicWorkRequestBuilder<SyncWorker>(1, HOURS)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(PERIODIC_SYNC_WORKER, ExistingPeriodicWorkPolicy.KEEP, saveRequest)

    }
}