package cz.applifting.humansis.ui.components.listComponent

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 09, September, 2019
 */
data class ListComponentState(val isRetrieving: Boolean = false, val isRefreshing: Boolean = false, val text: String? = null)