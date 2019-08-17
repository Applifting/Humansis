package cz.applifting.humansis.ui.distribution.distributions

import android.util.Log
import androidx.lifecycle.ViewModel
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class DistributionsViewModel @Inject constructor() : ViewModel() {

    init {
        Log.d("asdf", "distribution viewmodel create")
    }

    fun hello() {
        Log.d("asdf", "Hello from distributions")
    }

}