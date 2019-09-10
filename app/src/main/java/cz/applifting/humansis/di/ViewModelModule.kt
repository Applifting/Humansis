package cz.applifting.humansis.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.applifting.humansis.misc.ViewModelFactory
import cz.applifting.humansis.ui.login.LoginViewModel
import cz.applifting.humansis.ui.main.MainActivityViewModel
import cz.applifting.humansis.ui.main.SharedViewModel
import cz.applifting.humansis.ui.main.distribute.beneficiaries.BeneficiariesViewModel
import cz.applifting.humansis.ui.main.distribute.beneficiary.BeneficiaryViewModel
import cz.applifting.humansis.ui.main.distribute.distributions.DistributionsViewModel
import cz.applifting.humansis.ui.main.distribute.projects.ProjectsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ProjectsViewModel::class)
    internal abstract fun bindProjectsViewModel(viewModel: ProjectsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DistributionsViewModel::class)
    internal abstract fun bindDistributionsViewModel(viewModel: DistributionsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun bindLoginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun bindMainActivityViewModel(viewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BeneficiariesViewModel::class)
    internal abstract fun bindBeneficiariesViewModel(viewModel: BeneficiariesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BeneficiaryViewModel::class)
    internal abstract fun bindBeneficiaryViewModel(viewModel: BeneficiaryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SharedViewModel::class)
    internal abstract fun bindSharedViewModel(viewModel: SharedViewModel): ViewModel

    //Add more ViewModels here
}
