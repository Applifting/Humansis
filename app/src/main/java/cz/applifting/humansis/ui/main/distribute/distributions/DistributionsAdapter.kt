package cz.applifting.humansis.ui.main.distribute.distributions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cz.applifting.humansis.R
import cz.applifting.humansis.model.CommodityType
import cz.applifting.humansis.model.Target
import cz.applifting.humansis.model.db.DistributionLocal
import kotlinx.android.synthetic.main.item_distribution.view.*
import kotlinx.android.synthetic.main.item_project.view.tv_name

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 21, August, 2019
 */
class DistributionsAdapter(
    private val context: Context,
    val onItemClick: (distribution: DistributionLocal) -> Unit
) : RecyclerView.Adapter<DistributionsAdapter.DistributionViewHolder>() {

    private val distributions: MutableList<DistributionLocal> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistributionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_distribution,
            parent,
            false
        ) as ConstraintLayout
        return DistributionViewHolder(view)
    }

    override fun getItemCount(): Int = distributions.size


    // TODO fix kotlin binding
    override fun onBindViewHolder(holder: DistributionViewHolder, position: Int) {
        val distribution = distributions[position]

        // Set text fields
        holder.layout.tv_name.text = distribution.name
                holder.layout.tv_date.text = context.getString(R.string.date_of_distribution, distribution.dateOfDistribution)
                holder.layout.tv_beneficieries_cnt.text = context.getString(R.string.beneficiaries, distribution.numberOfBeneficiaries)

        // Set commodities
        holder.layout.iv_cash.visibility = View.GONE
        holder.layout.iv_food.visibility = View.GONE
        holder.layout.iv_loan.visibility = View.GONE

        for (commodity in distribution.commodities) {
            when (commodity) {
                CommodityType.CASH.name -> holder.layout.iv_cash.visibility = View.VISIBLE
                CommodityType.FOOD.name -> holder.layout.iv_food.visibility = View.VISIBLE
                CommodityType.LOAN.name -> holder.layout.iv_loan.visibility = View.VISIBLE
//              CommodityType.RTE_KIT -> TODO()
////            CommodityType.PAPER_VOUCHER -> TODO()
////            null -> TODO()
                else -> holder.layout.iv_loan.visibility = View.VISIBLE
            }
        }

        // Set target
        val targetImage =
            if (distribution.target == Target.INDIVIDUAL) {
                context.getDrawable(R.drawable.ic_person_black_24dp)
            } else {
                context.getDrawable(R.drawable.ic_home_black_24dp)
            }
        holder.layout.iv_target.setImageDrawable(targetImage)
        holder.layout.setOnClickListener { onItemClick(distributions[position]) }
    }

    fun updateDistributions(newDistributions: List<DistributionLocal>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                newDistributions[newItemPosition].id == distributions[oldItemPosition].id

            override fun getOldListSize(): Int = distributions.size
            override fun getNewListSize(): Int = newDistributions.size
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                false
        })

        this.distributions.clear()
        this.distributions.addAll(newDistributions)
        diffResult.dispatchUpdatesTo(this)
    }

    class DistributionViewHolder(val layout: ConstraintLayout) : RecyclerView.ViewHolder(layout)
}