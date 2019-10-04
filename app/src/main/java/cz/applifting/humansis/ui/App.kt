package cz.applifting.humansis.ui

import android.app.Application
import cz.applifting.humansis.BuildConfig
import cz.applifting.humansis.di.AppComponent
import cz.applifting.humansis.di.DaggerAppComponent

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class App: Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .context(this)
            .baseUrl(BuildConfig.API_BASE_URL)
            .build()
    }

    fun addUserDependecies() {

    }
}