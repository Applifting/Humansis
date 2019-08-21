package cz.applifting.humansis.di

import android.app.Application
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.main.MainActivity
import cz.applifting.humansis.ui.login.LoginActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton



/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */

@Singleton
@Component(modules = [AppModule::class, ViewModelModule::class])
interface AppComponent {
    fun inject(baseFragment: BaseFragment)

    fun inject(loginActivity: LoginActivity)

    fun inject(mainActivity: MainActivity)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(applicationContext: Application): Builder

        @BindsInstance
        fun baseUrl(baseUrl: String): Builder

        fun build(): AppComponent
    }
}