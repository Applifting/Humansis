package cz.applifting.humansis.ui.login

import android.view.View
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.managers.LoginManager
import cz.applifting.humansis.misc.HumansisError
import cz.applifting.humansis.misc.hashAndSaltPassword
import cz.applifting.humansis.model.api.LoginReqRes
import cz.applifting.humansis.model.db.User
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 17, August, 2019
 */
class LoginViewModel @Inject constructor(
    private val service: HumansisService,
    private val loginManager: LoginManager
) : BaseViewModel() {

    val viewStateLD = MutableLiveData<LoginViewState>()
    val loginLD = MutableLiveData<User>()

    init {
        viewStateLD.value = LoginViewState()
    }

    fun login(username: String, password: String) {
        launch {
            viewStateLD.value = LoginViewState(
                btnLoginVisibility = View.GONE,
                etPasswordIsEnabled = false,
                etUsernameIsEnabled = false,
                pbLoadingVisible = View.VISIBLE
            )

            try {
                val saltResponse = service.getSalt(username)
                val hashedPassword = hashAndSaltPassword(saltResponse.salt, password)
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

                val user = loginManager.login(userResponse, password.toByteArray())
                loginLD.value = user
            } catch (e: HumansisError) {
                viewStateLD.value = LoginViewState(errorMessage = e.message)
            }
        }
    }
}