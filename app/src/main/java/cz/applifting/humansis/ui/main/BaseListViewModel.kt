package cz.applifting.humansis.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.ui.BaseViewModel
import cz.applifting.humansis.ui.components.listComponent.ListComponentState

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 10, September, 2019
 */
abstract class BaseListViewModel(val context: Context): BaseViewModel() {

    val listStateLD: LiveData<ListComponentState>
        get() = _listStateLD

    private val _listStateLD = MutableLiveData<ListComponentState>()

    init {
        _listStateLD.value = ListComponentState()
    }

    fun showRefreshing(show: Boolean) {
        _listStateLD.value = _listStateLD.value?.copy(isRefreshing = show)
    }

    fun showRetrieving(show: Boolean) {
        _listStateLD.value = _listStateLD.value?.copy(isRetrieving = show)
    }
}