package cz.applifting.humansis.ui.components.listComponent

import android.content.Context
import android.graphics.Rect
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
class ListComponent(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    fun init(viewAdapter: RecyclerView.Adapter<*>) {
        View.inflate(context, R.layout.component_list, this)

        val viewManager = LinearLayoutManager(context)

        rv_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            addItemDecoration(VerticalMarginItemDecoration())
        }

        swrl_swipe_to_refresh.isEnabled = false
    }

    fun setState(state: ListComponentState) {
        // Always scroll top
        rv_list.scrollToPosition(0)

        pb_loading.visible(state.isRetrieving)
        swrl_swipe_to_refresh.isRefreshing = state.isRefreshing

        if (state.text != null && !state.isRetrieving && !state.isRetrieving) {
            tv_info.text = state.text
            tv_info.visible(true)
        } else {
            tv_info.visible(false)
        }
    }

    fun setOnRefreshListener(listener: () -> Unit) {
        swrl_swipe_to_refresh.isEnabled = true
        swrl_swipe_to_refresh.setOnRefreshListener(listener)
    }

    fun scrollToTop() {
        rv_list.layoutManager?.scrollToPosition(0)
    }

    inner class VerticalMarginItemDecoration() : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

            val margin = context.resources.getDimension(R.dimen.list_items_vertical_margin).toInt()

            with(outRect) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    top = margin
                }
                left = margin
                right = margin
                bottom = margin
            }
        }
    }

}