package cz.applifting.humansis.ui.main.distribute.beneficiary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import cz.applifting.humansis.R
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_beneficiary.*

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 9. 9. 2019
 */

class BeneficiaryFragment : BaseFragment() {

    val args: BeneficiaryFragmentArgs by navArgs()

    private val viewModel: BeneficiaryViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beneficiary, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).supportActionBar?.title = getString(R.string.assign_booklet)
        (activity as MainActivity).supportActionBar?.subtitle = args.beneficiaryName


        tv_status.setValue(getString(if (args.distributionStatus) R.string.distributed else R.string.not_distributed))
        tv_status.setStatus(args.distributionStatus)
        tv_beneficiary.setValue(args.beneficiaryName)
        tv_distribution.setValue(args.distributionName)
        tv_project.setValue(args.projectName)

    }
}