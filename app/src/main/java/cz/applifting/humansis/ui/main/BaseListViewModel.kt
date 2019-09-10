package cz.applifting.humansis.ui.main

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.ui.BaseViewModel
import cz.applifting.humansis.ui.components.listComponent.ListComponentState

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 10, September, 2019
 */
abstract class BaseListViewModel: BaseViewModel() {

    open val listStateLD: MutableLiveData<ListComponentState> = MutableLiveData()

    fun showRefreshing() {
        listStateLD.value = ListComponentState(isRefreshing = true, isRetrieving = false, text = "Downloading...")
    }
}