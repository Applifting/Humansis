package cz.applifting.humansis.ui.main

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.model.db.User
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 21, August, 2019
 */
class MainActivityViewModel @Inject constructor(
    private val humansisDB: HumansisDB
) : BaseViewModel() {

    val userLD = MutableLiveData<User>()

    fun getUser() {
        launch(Dispatchers.IO) {
            val user = humansisDB.userDao().getUser()
            userLD.postValue(user)
        }
    }

    fun logout() {
        launch(Dispatchers.IO) {
            // TODO clear all other tables
            humansisDB.userDao().deleteAll()
            userLD.postValue(null)
        }
    }
}