package cz.applifting.humansis.di

import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.MainActivity
import cz.applifting.humansis.ui.distribution.distributions.DistributionsFragment
import cz.applifting.humansis.ui.distribution.projects.ProjectsFragment
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */

@Singleton
@Component(modules = [AppModule::class, ViewModelModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)

    fun inject(baseFragment: BaseFragment)

}