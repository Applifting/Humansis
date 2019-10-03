package cz.applifting.humansis.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.format(): String {
    val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
    return formatter.format(this)
}