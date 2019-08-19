package cz.applifting.humansis.ui.login

import android.view.View

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 19, August, 2019
 */
data class LoginViewState(
    val btnLoginVisibility: Int = View.VISIBLE,
    val etPasswordIsEnabled: Boolean = true,
    val etUsernameIsEnabled: Boolean = true,
    val pbLoadingVisible: Int = View.GONE
)