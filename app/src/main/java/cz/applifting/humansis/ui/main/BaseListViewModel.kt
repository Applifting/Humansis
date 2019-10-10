package cz.applifting.humansis.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.R
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

    fun showRefreshing() {
        _listStateLD.value = ListComponentState(isRefreshing = true, isRetrieving = false, text = context.getString(R.string.downloading))
    }

    fun showRetrieving() {
        _listStateLD.value = ListComponentState(isRefreshing = false, isRetrieving = true)
    }

    fun finishLoading(list: List<Any>?) {
        val text = if (list?.isNotEmpty() == true) null else context.getString(R.string.no_data_message)
        _listStateLD.value = ListComponentState(text = text)
    }
}