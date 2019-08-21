package cz.applifting.humansis.ui.main

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.managers.AuthManager
import cz.applifting.humansis.model.db.User
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 21, August, 2019
 */
class MainActivityViewModel @Inject constructor(
    private val authManager: AuthManager
) : BaseViewModel() {

    val userLD = MutableLiveData<User>()

    fun getUser() {
        launch {
            userLD.value = authManager.retrieveUser()
        }
    }

    fun logout() {
        launch(Dispatchers.IO) {
            authManager.logout()
            userLD.postValue(null)
        }
    }
}