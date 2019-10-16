package cz.applifting.humansis.api

import android.content.Context
import cz.applifting.humansis.R
import retrofit2.HttpException

fun parseError(e: HttpException, context: Context): String {

    return when (e.response()?.errorBody()?.string()) {
        "This username doesn't exist" -> context.getString(R.string.error_invalid_username)
        "Wrong password" -> context.getString(R.string.error_invalid_password)
        "No internet connection" -> context.getString(R.string.error_no_internet_connection)
        "Service unavailable" -> context.getString(R.string.error_service_unavailable)
        else -> context.getString(R.string.error_unknown)
    }
}