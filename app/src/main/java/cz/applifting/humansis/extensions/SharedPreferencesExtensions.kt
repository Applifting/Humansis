package cz.applifting.humansis.extensions

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 11, September, 2019
 */

suspend fun SharedPreferences.Editor.suspendCommit(): Boolean {
    return withContext(Dispatchers.IO) {
        this@suspendCommit.commit()
    }
}

fun SharedPreferences.setDate(key: String, value: Date) {
    this.edit().putLong(key, value.time).apply()
}

fun SharedPreferences.getDate(key: String): Date? {
    val time = this.getLong(key, 0)

    if (time == 0L) {
        return null
    }
    return Date(time)
}