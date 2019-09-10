package cz.applifting.humansis.ui.main.distribute.distributions

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
    private val onItemClick: (distribution: DistributionLocal) -> Unit
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

    override fun onBindViewHolder(holder: DistributionViewHolder, position: Int) {
        val distribution = distributions[position]
        holder.bind(distribution)
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

    inner class DistributionViewHolder(val layout: ConstraintLayout) : RecyclerView.ViewHolder(layout) {
        val tvName = layout.tv_name
        val tvDate = layout.tv_date
        val tvBeneficieriesCnt = layout.tv_beneficieries_cnt
        val ivCash = layout.iv_cash
        val ivFood = layout.iv_food
        val ivLoan = layout.iv_loan
        val ivTarget = layout.iv_target
        val context = layout.context

        fun bind(distribution: DistributionLocal) {
            // Set text fields
            tvName.text = distribution.name
            tvDate.text = context.getString(R.string.date_of_distribution, distribution.dateOfDistribution)
            tvBeneficieriesCnt.text = context.getString(R.string.beneficiaries, distribution.numberOfBeneficiaries)

            // Set commodities
            ivCash.visibility = View.GONE
            ivFood.visibility = View.GONE
            ivLoan.visibility = View.GONE

            for (commodity in distribution.commodities) {
                when (commodity) {
                    CommodityType.CASH.name -> ivCash.visibility = View.VISIBLE
                    CommodityType.FOOD.name -> ivFood.visibility = View.VISIBLE
                    CommodityType.LOAN.name -> ivLoan.visibility = View.VISIBLE
    //              CommodityType.RTE_KIT -> TODO()
    ////            CommodityType.PAPER_VOUCHER -> TODO()
    ////            null -> TODO()
                    else -> ivLoan.visibility = View.VISIBLE
                }
            }

            // Set target
            val targetImage =
                if (distribution.target == Target.INDIVIDUAL) {
                    context.getDrawable(R.drawable.ic_person_black_24dp)
                } else {
                    context.getDrawable(R.drawable.ic_home_black_24dp)
                }
            ivTarget.setImageDrawable(targetImage)
            layout.setOnClickListener { onItemClick(distributions[position]) }

        }
    }
}