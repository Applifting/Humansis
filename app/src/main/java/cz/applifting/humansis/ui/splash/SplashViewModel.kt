package cz.applifting.humansis.ui.splash

import cz.applifting.humansis.db.DbProvider
import cz.applifting.humansis.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 11, September, 2019
 */

class SplashViewModel @Inject constructor(private val dbProvider: DbProvider): BaseViewModel() {

    fun shouldLogin(): Boolean {
        return dbProvider.isInitialized()
    }

}