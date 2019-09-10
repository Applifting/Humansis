package cz.applifting.humansis.extensions

import android.app.Activity
import android.widget.Toast

fun String.shortToast(activity: Activity) {
    Toast.makeText(activity, this, Toast.LENGTH_SHORT).show()
}