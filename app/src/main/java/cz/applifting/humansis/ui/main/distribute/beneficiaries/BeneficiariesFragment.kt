package cz.applifting.humansis.ui.main.distribute.beneficiaries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.visible
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.HumansisActivity
import kotlinx.android.synthetic.main.fragment_beneficiaries.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 5. 9. 2019
 */

class BeneficiariesFragment : BaseFragment() {

    val args: BeneficiariesFragmentArgs by navArgs()

    private val viewModel: BeneficiariesViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beneficiaries, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as HumansisActivity).supportActionBar?.title = args.distributionName
        (activity as HumansisActivity).supportActionBar?.subtitle = getString(R.string.beneficiaries_title)

        val viewAdapter = BeneficiariesAdapter { beneficiary ->
            showBeneficiaryDialog(beneficiary)
        }

        lc_beneficiaries.init(viewAdapter)

        viewModel.searchResultsLD.observe(viewLifecycleOwner, Observer {
            viewAdapter.update(it)
        })

        viewModel.beneficiariesViewStateLD.observe(viewLifecycleOwner, Observer {
            lc_beneficiaries.setState(it)
            showControls(!it.isRetrieving)
        })

        viewModel.statsLD.observe(viewLifecycleOwner, Observer {
            val (reachedBeneficiaries, totalBeneficiaries) = it
            cmp_reached_beneficiaries.setStats(reachedBeneficiaries, totalBeneficiaries)
        })

        cmp_search_beneficiary.onTextChanged(viewModel::search)
        cmp_search_beneficiary.onSort { viewModel.sortBeneficiaries() }

        viewModel.listStateLD.observe(viewLifecycleOwner, Observer(lc_beneficiaries::setState))

        sharedViewModel.forceOfflineReloadLD.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.loadBeneficiaries(args.distributionId)
                sharedViewModel.forceOfflineReload(false)
            }
        })

        sharedViewModel.syncWorkerIsLoadingLD.observe(viewLifecycleOwner, Observer {
            when {
                it -> viewModel.showRefreshing()

                else -> launch {
                    // Load after animation finishes to avoid drop in frame rate
                    delay(context?.resources?.getInteger(R.integer.animationTime)?.toLong() ?: 0)
                    viewModel.loadBeneficiaries(args.distributionId)
                }
            }
        })
    }

    private fun showControls(show: Boolean) {
        cmp_reached_beneficiaries.visible(show)
        cmp_search_beneficiary.visible(show)
    }


    private fun showBeneficiaryDialog(beneficiaryLocal: BeneficiaryLocal) {
        val action = BeneficiariesFragmentDirections.actionBeneficiariesFragmentToBeneficiaryFragmentDialog(
            beneficiaryLocal.id,
            args.distributionName,
            args.projectName,
            args.isQRVoucherDistribution
        )

        this.findNavController().navigate(action)
    }
}