package cz.applifting.humansis.ui.splash

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.db.DbProvider
import cz.applifting.humansis.managers.LoginManager
import cz.applifting.humansis.model.db.User
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 11, September, 2019
 */

class SplashViewModel @Inject constructor(private val dbProvider: DbProvider, private val loginManager: LoginManager): BaseViewModel() {

    val userLD = MutableLiveData<User>()

    fun initDB(): Boolean {
        return loginManager.tryInitDB()
    }

    fun getUser() {
        launch {
            userLD.value = loginManager.retrieveUser()
        }
    }

}