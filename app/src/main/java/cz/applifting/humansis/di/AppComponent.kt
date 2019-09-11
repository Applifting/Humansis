package cz.applifting.humansis.di

import android.content.Context
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.login.LoginActivity
import cz.applifting.humansis.ui.main.MainFragment
import cz.applifting.humansis.ui.splash.SplashActivity
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

    fun inject(mainActivity: MainFragment)

    fun inject(splashActivity: SplashActivity)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        @BindsInstance
        fun baseUrl(baseUrl: String): Builder

        fun build(): AppComponent
    }
}