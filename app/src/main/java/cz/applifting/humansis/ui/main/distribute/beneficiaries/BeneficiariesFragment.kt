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
import cz.applifting.humansis.extensions.hideSoftKeyboard
import cz.applifting.humansis.extensions.visible
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.HumansisActivity
import kotlinx.android.synthetic.main.component_search_beneficiary.*
import kotlinx.android.synthetic.main.fragment_beneficiaries.*

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
        cmp_search_beneficiary.onSort {
            viewModel.changeSort()
            lc_beneficiaries.scrollToTop()
        }

        viewModel.listStateLD.observe(viewLifecycleOwner, Observer(lc_beneficiaries::setState))

        viewModel.currentSort.observe(viewLifecycleOwner, Observer<BeneficiariesViewModel.Sort> {
            viewModel.setSortedBeneficieries(viewModel.searchResultsLD.value)
            cmp_search_beneficiary.changeSortIcon(it)
        })

        viewModel.init(args.distributionId)

        sharedViewModel.syncState.observe(viewLifecycleOwner, Observer {
            viewModel.showRefreshing(it.isLoading)
        })

        findNavController().addOnDestinationChangedListener { _, _, _ -> et_search?.hideSoftKeyboard() }
    }

    private fun showControls(show: Boolean) {
        cmp_reached_beneficiaries.visible(show)
        cmp_search_beneficiary.visible(show)
    }

    private fun showBeneficiaryDialog(beneficiaryLocal: BeneficiaryLocal) {
        (findNavController().currentDestination?.id == R.id.beneficiariesFragment).let { safeToNavigate ->
            if (safeToNavigate) {
                val action = BeneficiariesFragmentDirections.actionBeneficiariesFragmentToBeneficiaryFragmentDialog(
                    beneficiaryId = beneficiaryLocal.id,
                    distributionName = args.distributionName,
                    projectName = args.projectName,
                    isQRVoucher = args.isQRVoucherDistribution
                )
                findNavController().navigate(action)
            }
        }
    }
}