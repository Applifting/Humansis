package cz.applifting.humansis.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.ui.BaseViewModel
import cz.applifting.humansis.ui.components.listComponent.ListComponentState

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 10, September, 2019
 */
abstract class BaseListViewModel: BaseViewModel() {

    val listStateLD: LiveData<ListComponentState>
        get() = _listStateLD

    private val _listStateLD = MutableLiveData<ListComponentState>()

    init {
        _listStateLD.value = ListComponentState()
    }

    fun showRefreshing() {
        _listStateLD.value = ListComponentState(isRefreshing = true, isRetrieving = false, text = "Downloading...")
    }

    fun showRetrieving() {
        _listStateLD.value = ListComponentState(isRefreshing = false, isRetrieving = true)
    }

    fun finishLoading(list: List<Any>?) {
        val text = if (list?.isNotEmpty() == true) null else "Nothing here, try to reload."
        _listStateLD.value = ListComponentState(text = text)
    }
}