package cz.applifting.humansis.ui.main

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.managers.LoginManager
import cz.applifting.humansis.model.db.User
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 21, August, 2019
 */
class MainViewModel @Inject constructor(
    private val loginManager: LoginManager
) : BaseViewModel() {

    val userLD = MutableLiveData<User>()

    fun logout() {
        launch(Dispatchers.IO) {
            loginManager.logout()
            userLD.postValue(null)
        }
    }
}