package cz.applifting.humansis.extensions

import android.content.Context
import android.net.ConnectivityManager
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController


fun Context.isNetworkConnected(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

fun Context.isWifiConnected(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    return networkInfo.isConnected
}

fun Fragment.tryNavController(): NavController? =
    try {
        findNavController()
    } catch (e: IllegalStateException) {
        // when fragment got close etc.
        e.printStackTrace()
        null
    }

fun Fragment.tryNavigateFrom(@IdRes destinationId: Int, block: NavController.() -> Unit) {
    tryNavController()?.apply {
        if (currentDestination?.id == destinationId) {
            block()
        }
    }
}

fun Fragment.tryNavigate(@IdRes destinationId: Int, directions: NavDirections) {
    tryNavigateFrom(destinationId) {
        navigate(directions)
    }
}
