package cz.applifting.humansis.extensions

import android.content.Context
import android.net.ConnectivityManager


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
