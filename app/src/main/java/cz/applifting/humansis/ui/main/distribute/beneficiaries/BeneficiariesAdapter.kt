package cz.applifting.humansis.ui.main.distribute.beneficiaries

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.tintedDrawable
import cz.applifting.humansis.model.db.BeneficiaryLocal
import kotlinx.android.synthetic.main.item_beneficiary.view.*


/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 5. 9. 2019
 */

class BeneficiariesAdapter(val onItemClick: (beneficiary: BeneficiaryLocal) -> Unit) :
    RecyclerView.Adapter<BeneficiariesAdapter.BeneficiaryViewHolder>() {

    private var beneficiaries = mutableListOf<BeneficiaryLocal>()

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
        holder.view.setOnClickListener { onItemClick(distributionBeneficiary) }

    }

    override fun getItemCount(): Int = beneficiaries.size

    internal fun update(newDistributionBeneficiaryLocals: List<BeneficiaryLocal>) {
        beneficiaries = newDistributionBeneficiaryLocals.toMutableList()
        notifyDataSetChanged()
    }

    class BeneficiaryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(beneficiaryLocal: BeneficiaryLocal) {
            view.tv_id.text = view.context.getString(R.string.beneficiary_id, beneficiaryLocal.id)
            view.tv_name.text = view.context.getString(
                R.string.beneficiary_name,
                beneficiaryLocal.givenName,
                beneficiaryLocal.familyName
            )

            val color =
                if (beneficiaryLocal.distributed) R.color.distributed else R.color.notDistributed
            view.iv_distribution_state.tintedDrawable(R.drawable.ic_distribution_state, color)
        }
    }

}