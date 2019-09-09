package cz.applifting.humansis.ui.main.distribute.beneficiaries

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.simpleDrawable
import cz.applifting.humansis.extensions.tintedDrawable
import cz.applifting.humansis.model.db.BeneficiaryLocal
import kotlinx.android.synthetic.main.item_beneficiary.view.*


/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 5. 9. 2019
 */

class BeneficiariesAdapter(
    private val context: Context,
    val onItemClick: (beneficiary: BeneficiaryLocal) -> Unit
) : RecyclerView.Adapter<BeneficiariesAdapter.BeneficiaryViewHolder>() {

    private var beneficiaries = listOf<BeneficiaryLocal>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeneficiaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_beneficiary,
            parent,
            false
        )
        return BeneficiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: BeneficiaryViewHolder, position: Int) {
        val distributionBeneficiary = beneficiaries[position]
        holder.bind(distributionBeneficiary)
    }

    override fun getItemCount(): Int = beneficiaries.size

    internal fun update(newBeneficiaries: List<BeneficiaryLocal>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                newBeneficiaries[newItemPosition].id == beneficiaries[oldItemPosition].id

            override fun getOldListSize(): Int = beneficiaries.size
            override fun getNewListSize(): Int = newBeneficiaries.size
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                beneficiaries[oldItemPosition] == newBeneficiaries[newItemPosition]
        })
        beneficiaries = newBeneficiaries
        diffResult.dispatchUpdatesTo(this)
    }

    inner class BeneficiaryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val tvId = view.tv_id
        val tvName = view.tv_name
        val ivDistributionState = view.iv_distribution_state
        val llVulnerabilitiesHolder = view.ll_vulnerabilities_holder

        fun bind(beneficiaryLocal: BeneficiaryLocal) {

            tvId.text = context.getString(R.string.beneficiary_id, beneficiaryLocal.id)
            tvName.text = context.getString(
                R.string.beneficiary_name,
                beneficiaryLocal.givenName,
                beneficiaryLocal.familyName
            )

            val color = if (beneficiaryLocal.distributed) R.color.distributed else R.color.notDistributed
            ivDistributionState.tintedDrawable(R.drawable.ic_distribution_state, color)

            beneficiaryLocal.vulnerabilities.forEach {
                llVulnerabilitiesHolder.removeAllViews()

                getVulnerabilityDrawable(it)?.let { drawable ->
                    val vulnerabilityImage = ImageView(context)
                    vulnerabilityImage.simpleDrawable(drawable)
                    llVulnerabilitiesHolder.addView(vulnerabilityImage)
                }
            }

            view.setOnClickListener { onItemClick(beneficiaryLocal) }
        }

        private fun getVulnerabilityDrawable(vulnerability: String): Int? {
            return when (vulnerability) {
                "disabled" -> R.drawable.ic_vulnerability_disabled
                "lactating" -> R.drawable.ic_vulnerability_lactating
                "pregnant" -> R.drawable.ic_vulnerability_pregnant
                else -> null
            }
        }
    }

}