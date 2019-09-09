package cz.applifting.humansis.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.visible
import kotlinx.android.synthetic.main.component_list.view.*

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 09, September, 2019
 */
class ListComponent(context: Context, attrs: AttributeSet): ConstraintLayout(context, attrs) {

    fun init(viewAdapter: RecyclerView.Adapter<*>) {
        View.inflate(context, R.layout.component_list, this)

        val viewManager = LinearLayoutManager(context)

        rv_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    fun setState(state: ListComponentState) {
        pb_loading.visible(state.isRetrieving)
        swrl_swipe_to_refresh.isRefreshing = state.isRefreshing
    }

    fun setOnRefreshListener(listener: () -> Unit) {
        swrl_swipe_to_refresh.setOnRefreshListener(listener)
    }

}