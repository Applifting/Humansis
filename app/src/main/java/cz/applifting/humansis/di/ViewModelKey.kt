package cz.applifting.humansis.di

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 15, August, 2019
 */
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)