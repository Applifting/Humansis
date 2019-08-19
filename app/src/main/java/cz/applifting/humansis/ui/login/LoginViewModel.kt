package cz.applifting.humansis.ui.login

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.misc.generateXWSSEHeader
import cz.applifting.humansis.misc.saltPassword
import cz.applifting.humansis.model.LoginReqRes
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 17, August, 2019
 */
class LoginViewModel @Inject constructor(val mService: HumansisService) : BaseViewModel() {

    val viewState = MutableLiveData<LoginViewState>()

    init {
        viewState.value = LoginViewState()
    }

    fun login(username: String, password: String): Boolean {
        viewState.value = LoginViewState(
            btnLoginVisibility = View.GONE,
            etPasswordIsEnabled = false,
            etUsernameIsEnabled = false,
            pbLoadingVisible = View.VISIBLE
        )

        launch {
            try {
                val saltResponse = mService.getSalt(username)
                val hashedPassword = saltPassword(saltResponse.salt, password)
                mService.postLogin(LoginReqRes(true, username, null, null, hashedPassword, null, username, null))

                val header = generateXWSSEHeader(username, hashedPassword)
                mService.getProjects(header)
            } catch (e: Exception) {
                Log.d("asdf", e.toString())
            }

            viewState.value = LoginViewState()
        }

        return true
    }
}