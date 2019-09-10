package cz.applifting.humansis.ui.main.distribute.beneficiary

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import androidx.annotation.Nullable
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.shortToast
import cz.applifting.humansis.extensions.visible
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.main.MainActivity
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
        (activity as MainActivity).supportActionBar?.title = getString(R.string.assign_booklet)
        (activity as MainActivity).supportActionBar?.subtitle = args.beneficiaryName

        viewModel.beneficiaryViewStateLD.observe(viewLifecycleOwner, Observer {
            pb_loading.visible(it.refreshing)
            tv_status.visible(!it.refreshing)
            tv_beneficiary.visible(!it.refreshing)
            tv_distribution.visible(!it.refreshing)
            tv_project.visible(!it.refreshing)
            tv_status.setValue(getString(if (it.distributed) R.string.distributed else R.string.not_distributed))
            tv_status.setStatus(it.distributed)
            tv_beneficiary.setValue(args.beneficiaryName)
            tv_distribution.setValue(args.distributionName)
            tv_project.setValue(args.projectName)
            (activity as MainActivity).invalidateOptionsMenu()
        })

        viewModel.loadBeneficiary(args.beneficiaryId)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_beneficiary, menu)
        val confimAction = menu.findItem(R.id.action_confirm_distribution)
        confimAction?.isVisible = if (args.distributionStatus) !args.distributionStatus else !viewModel.distributed
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val confirmAction = menu.findItem(R.id.action_confirm_distribution)
        val rootView = confirmAction.actionView as FrameLayout
        val confirmBtn = rootView.btn_confirm_distribution
        confirmBtn?.setOnClickListener {
            viewModel.confirm()
            getString(R.string.distribution_confirmation_message, args.beneficiaryName).shortToast(activity as MainActivity)
        }
        return super.onPrepareOptionsMenu(menu)
    }
}