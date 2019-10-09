package cz.applifting.humansis.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.visible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.hideSoftKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.showSoftKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}
