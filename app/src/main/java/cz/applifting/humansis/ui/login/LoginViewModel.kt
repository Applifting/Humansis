package cz.applifting.humansis.ui.login

import android.view.View
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.managers.AuthManager
import cz.applifting.humansis.misc.saltPassword
import cz.applifting.humansis.model.api.LoginReqRes
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 17, August, 2019
 */
class LoginViewModel @Inject constructor(
    private val service: HumansisService,
    private val authManager: AuthManager
) : BaseViewModel() {

    val viewState = MutableLiveData<LoginViewState>()

    init {
        viewState.value = LoginViewState()
    }

    fun login(username: String, password: String) {
        launch {
            viewState.value = LoginViewState(
                btnLoginVisibility = View.GONE,
                etPasswordIsEnabled = false,
                etUsernameIsEnabled = false,
                pbLoadingVisible = View.VISIBLE
            )

            try {
                val saltResponse = service.getSalt(username)
                val hashedPassword = saltPassword(saltResponse.salt, password)
                val userResponse = service.postLogin(
                    LoginReqRes(
                        true,
                        username,
                        null,
                        null,
                        hashedPassword,
                        null,
                        username,
                        null
                    )
                )

                authManager.login(userResponse)

                viewState.value = LoginViewState(finishLoginActivity = true)
            } catch (e: Exception) {
                viewState.value = LoginViewState(errorMessage = e.message)
            }
        }
    }
}