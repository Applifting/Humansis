package cz.applifting.humansis.extensions

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 11, September, 2019
 */

suspend fun SharedPreferences.Editor.suspendCommit(): Boolean {
    return withContext(Dispatchers.IO) {
        this@suspendCommit.commit()
    }
}