package cz.applifting.humansis.ui.main.distribute.beneficiary

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.shortToast
import cz.applifting.humansis.extensions.visible
import cz.applifting.humansis.ui.App
import cz.applifting.humansis.ui.HumansisActivity
import kotlinx.android.synthetic.main.fragment_beneficiary.*
import kotlinx.android.synthetic.main.fragment_beneficiary.view.*
import kotlinx.android.synthetic.main.menu_confirm_button.view.*
import javax.inject.Inject


/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 9. 9. 2019
 */

class BeneficiaryFragmentDialog : DialogFragment() {

    val args: BeneficiaryFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: BeneficiaryViewModel by viewModels { viewModelFactory }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_beneficiary, container, false)
        val toolbar = view.toolbar
        toolbar.inflateMenu(R.menu.menu_beneficiary)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_confirm_distribution -> {
                    viewModel.confirm()
                    getString(R.string.distribution_confirmation_message, args.beneficiaryName).shortToast(activity as HumansisActivity)
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        (activity?.application as App).appComponent.inject(this)

        viewModel.distributedLD.observe(viewLifecycleOwner, Observer {
            tv_status.setValue(getString(if (it) R.string.distributed else R.string.not_distributed))
            tv_status.setStatus(it)
            tv_beneficiary.setValue(args.beneficiaryName)
            tv_distribution.setValue(args.distributionName)
            tv_project.setValue(args.projectName)

            args.bookletId.let {
                tv_booklet.setValue(args.bookletId!!)
                tv_booklet.setAction(getString(R.string.rescan_qr), View.OnClickListener {
                    findNavController().navigateUp()
                })
            }

            (activity as HumansisActivity).invalidateOptionsMenu()
        })

        viewModel.refreshingLD.observe(viewLifecycleOwner, Observer {
            pb_loading.visible(it)
            tv_status.visible(!it)
            tv_beneficiary.visible(!it)
            tv_distribution.visible(!it)
            tv_project.visible(!it)
            tv_booklet.visible(!it && args.bookletId != null)
        })

        viewModel.loadBeneficiary(args.beneficiaryId)

        return dialog
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_beneficiary, menu)
        val confimAction = menu.findItem(R.id.action_confirm_distribution)
        confimAction?.isVisible = if (args.distributionStatus) !args.distributionStatus else !(viewModel.distributedLD.value ?: false)
        confimAction.actionView.setOnClickListener { onOptionsItemSelected(confimAction) }
        super.onCreateOptionsMenu(menu, inflater)
    }
}