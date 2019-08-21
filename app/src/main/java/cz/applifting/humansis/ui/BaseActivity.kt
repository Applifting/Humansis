package cz.applifting.humansis.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 21, August, 2019
 */
abstract class BaseActivity: AppCompatActivity(), CoroutineScope {
    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mJob = Job()
    }

    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
    }
}