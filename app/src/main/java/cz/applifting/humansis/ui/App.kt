package cz.applifting.humansis.ui

import android.app.Application
import androidx.work.*
import cz.applifting.humansis.BuildConfig
import cz.applifting.humansis.di.AppComponent
import cz.applifting.humansis.di.DaggerAppComponent
import cz.applifting.humansis.extensions.isNetworkConnected
import cz.applifting.humansis.synchronization.PERIODIC_SYNC_WORKER
import cz.applifting.humansis.synchronization.SyncWorker
import cz.applifting.humansis.synchronization.WHEN_ON_WIFI_SYNC_WORKER
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
        val periodicConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val periodicWorkRequest =
            PeriodicWorkRequestBuilder<SyncWorker>(1, HOURS)
                .setConstraints(periodicConstraints)
                .build()


        val workManager = WorkManager.getInstance(this)
        workManager.enqueueUniquePeriodicWork(PERIODIC_SYNC_WORKER, ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest)

        // Try to upload changes as soon as user is online
        if (!isNetworkConnected()) {
            val whenOnWifiConstraints= Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()
            val syncWhenWifiRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(whenOnWifiConstraints)
                .build()

            workManager.enqueueUniqueWork(WHEN_ON_WIFI_SYNC_WORKER, ExistingWorkPolicy.KEEP, syncWhenWifiRequest)
        }
    }
}