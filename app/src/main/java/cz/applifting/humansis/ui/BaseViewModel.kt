package cz.applifting.humansis.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 17, August, 2019
 */
open class BaseViewModel: ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext = viewModelScope.coroutineContext
}