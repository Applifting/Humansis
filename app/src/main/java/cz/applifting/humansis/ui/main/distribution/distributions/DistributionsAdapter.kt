package cz.applifting.humansis.ui.main.distribution.distributions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cz.applifting.humansis.R
import cz.applifting.humansis.model.api.Distribution
import kotlinx.android.synthetic.main.item_distribution.view.*
import kotlinx.android.synthetic.main.item_project.view.tv_name

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 21, August, 2019
 */
class DistributionsAdapter(val onItemClick: (distribution: Distribution) -> Unit) : RecyclerView.Adapter<DistributionsAdapter.DistributionViewHolder>(){

    private val distributions: MutableList<Distribution> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistributionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_distribution, parent, false) as ConstraintLayout
        return DistributionViewHolder(view)
    }

    override fun getItemCount(): Int = distributions.size

    override fun onBindViewHolder(holder: DistributionViewHolder, position: Int) {
        holder.layout.tv_name.text = distributions[position].name
        holder.layout.tv_date.text = distributions[position].dateDistribution
        holder.layout.setOnClickListener { onItemClick(distributions[position]) }
    }

    fun updateDistributions(newDistributions: List<Distribution>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = newDistributions[newItemPosition].id == distributions[oldItemPosition].id
            override fun getOldListSize(): Int = distributions.size
            override fun getNewListSize(): Int = newDistributions.size
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = false
        })

        this.distributions.clear()
        this.distributions.addAll(newDistributions)
        diffResult.dispatchUpdatesTo(this)
    }

    class DistributionViewHolder(val layout: ConstraintLayout): RecyclerView.ViewHolder(layout)
}