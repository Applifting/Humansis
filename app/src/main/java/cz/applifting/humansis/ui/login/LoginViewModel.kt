package cz.applifting.humansis.ui.login

import android.view.View
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.misc.HumansisError
import cz.applifting.humansis.misc.saltPassword
import cz.applifting.humansis.model.api.LoginReqRes
import cz.applifting.humansis.model.db.User
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 17, August, 2019
 */
class LoginViewModel @Inject constructor(
    val mService: HumansisService,
    val mHumansisDB: HumansisDB
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
                val saltResponse = mService.getSalt(username)
                val hashedPassword = saltPassword(saltResponse.salt, password)
                val userResponse = mService.postLogin(
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

                val id = userResponse.id?.toInt() ?: throw HumansisError()
                val user = User(id, userResponse.username, userResponse.email, hashedPassword)
                mHumansisDB.userDao().insert(user)

                viewState.value = LoginViewState(finishLoginActivity = true)
            } catch (e: Exception) {
                viewState.value = LoginViewState(errorMessage = e.message)
            }
        }
    }
}