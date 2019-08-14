package cz.applifting.humansis.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.applifting.humansis.ui.distribution.projects.ProjectsViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */

@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Singleton
internal class ViewModelFactory @Inject constructor(
    private val viewModels: Map<Class<out ViewModel>,
            @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        //return viewModels[modelClass]?.get() as T
        val creator = viewModels[modelClass] ?: viewModels.entries.firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("unknown model class $modelClass")
        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}


@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ProjectsViewModel::class)
    internal abstract fun bindProjectsViewModel(viewModel: ProjectsViewModel): ViewModel

    //Add more ViewModels here
}
