package cz.applifting.humansis.ui.main.distribute.beneficiaries

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.simpleDrawable
import cz.applifting.humansis.extensions.tintedDrawable
import cz.applifting.humansis.extensions.visible
import cz.applifting.humansis.model.db.BeneficiaryLocal
import kotlinx.android.synthetic.main.item_beneficiary.view.*


/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 5. 9. 2019
 */

class BeneficiariesAdapter(
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
        val tvHumansisId = view.tv_humansis_id
        val tvName = view.tv_name
        val ivDistributionState = view.iv_distribution_state
        val llVulnerabilitiesHolder = view.ll_vulnerabilities_holder
        val ivOffline = view.iv_offline
        val context = view.context

        fun bind(beneficiaryLocal: BeneficiaryLocal) {

            tvId.text = view.context.getString(R.string.beneficiary_id, beneficiaryLocal.beneficiaryId)
            tvHumansisId.text = view.context.getString(R.string.humansis_id, beneficiaryLocal.id)
            tvName.text = view.context.getString(
                R.string.beneficiary_name,
                beneficiaryLocal.givenName,
                beneficiaryLocal.familyName
            )

            val color = if (beneficiaryLocal.distributed) R.color.orange else R.color.light_blue
            ivDistributionState.tintedDrawable(R.drawable.ic_circle, color)
            llVulnerabilitiesHolder.removeAllViews()

            beneficiaryLocal.vulnerabilities.forEach {
                getVulnerabilityDrawable(it)?.let { drawableRes ->
                    val vulnerabilityImage = ImageView(view.context)
                    vulnerabilityImage.simpleDrawable(drawableRes)
                    llVulnerabilitiesHolder.addView(vulnerabilityImage)
                }
            }

            ivOffline.visible(beneficiaryLocal.edited)

            view.setOnClickListener { onItemClick(beneficiaryLocal) }
        }

        private fun getVulnerabilityDrawable(vulnerability: String): Int? {
            // values from https://api-demo.humansis.org/api/wsse/vulnerability_criteria
            return when (vulnerability) {
                "disabled" -> R.drawable.ic_vulnerability_disabled
                "soloParent" -> R.drawable.ic_vulnerability_solo_parent
                "lactating" -> R.drawable.ic_vulnerability_lactating
                "pregnant" -> R.drawable.ic_vulnerability_pregnant
                "nutritionalIssues" -> R.drawable.ic_vulnerability_nutritional_issues
                else -> null
            }
        }
    }

}