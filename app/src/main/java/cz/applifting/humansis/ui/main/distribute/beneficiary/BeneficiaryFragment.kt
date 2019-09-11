package cz.applifting.humansis.ui.main.distribute.beneficiary

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.Nullable
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.shortToast
import cz.applifting.humansis.extensions.visible
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.HumansisActivity
import kotlinx.android.synthetic.main.fragment_beneficiary.*
import kotlinx.android.synthetic.main.menu_confirm_button.view.*


/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 9. 9. 2019
 */

class BeneficiaryFragment : BaseFragment() {

    val args: BeneficiaryFragmentArgs by navArgs()

    private val viewModel: BeneficiaryViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
//        (activity as MainFragment).supportActionBar?.title = getString(R.string.assign_booklet)
//        (activity as MainFragment).supportActionBar?.subtitle = args.beneficiaryName

        viewModel.distributedLD.observe(viewLifecycleOwner, Observer {
            tv_status.setValue(getString(if (it) R.string.distributed else R.string.not_distributed))
            tv_status.setStatus(it)
            tv_beneficiary.setValue(args.beneficiaryName)
            tv_distribution.setValue(args.distributionName)
            tv_project.setValue(args.projectName)
//            (activity as MainFragment).invalidateOptionsMenu()
        })

        viewModel.refreshingLD.observe(viewLifecycleOwner, Observer {
            pb_loading.visible(it)
            tv_status.visible(!it)
            tv_beneficiary.visible(!it)
            tv_distribution.visible(!it)
            tv_project.visible(!it)
        })

        viewModel.loadBeneficiary(args.beneficiaryId)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_beneficiary, menu)
        val confimAction = menu.findItem(R.id.action_confirm_distribution)
        confimAction?.isVisible = if (args.distributionStatus) !args.distributionStatus else !(viewModel.distributedLD.value ?: false)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val confirmAction = menu.findItem(R.id.action_confirm_distribution)
        val rootView = confirmAction.actionView as FrameLayout
        val confirmBtn = rootView.btn_confirm_distribution
        confirmBtn?.setOnClickListener {
            viewModel.confirm()
            getString(R.string.distribution_confirmation_message, args.beneficiaryName).shortToast(activity as HumansisActivity)
        }
        return super.onPrepareOptionsMenu(menu)
    }
}