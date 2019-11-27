package cz.applifting.humansis.ui

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.work.*
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.getDate
import cz.applifting.humansis.extensions.isWifiConnected
import cz.applifting.humansis.synchronization.ON_START_SYNC_WORKER
import cz.applifting.humansis.synchronization.SyncWorker
import cz.applifting.humansis.synchronization.WHEN_ON_WIFI_SYNC_WORKER
import cz.applifting.humansis.ui.main.LAST_DOWNLOAD_KEY
import java.util.*
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 11, September, 2019
 */
class HumansisActivity : AppCompatActivity() {

    @Inject
    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_humansis)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }

        val navController = findNavController(R.id.nav_host_fragment_base)
        (application as App).appComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()

        val workManager = WorkManager.getInstance(this)

        // Try to upload changes as soon as user is online
        if (!isWifiConnected()) {

            val whenOnWifiConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()
            val syncWhenWifiRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(whenOnWifiConstraints)
                .build()

            workManager.enqueueUniqueWork(WHEN_ON_WIFI_SYNC_WORKER, ExistingWorkPolicy.KEEP, syncWhenWifiRequest)
        }


        val lastDownloadDate = sp.getDate(LAST_DOWNLOAD_KEY)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, -1)
        val dateHourAgo = calendar.time


        if (lastDownloadDate != null && lastDownloadDate.before(dateHourAgo)) {
            workManager.beginUniqueWork(ON_START_SYNC_WORKER, ExistingWorkPolicy.REPLACE, OneTimeWorkRequest.from(SyncWorker::class.java)).enqueue()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        return false
    }
}