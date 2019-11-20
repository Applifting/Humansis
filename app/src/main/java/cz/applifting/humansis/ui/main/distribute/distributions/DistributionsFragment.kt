package cz.applifting.humansis.ui.main.distribute.distributions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cz.applifting.humansis.R
import cz.applifting.humansis.model.CommodityType
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.HumansisActivity
import kotlinx.android.synthetic.main.fragment_distributions.*

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class DistributionsFragment : BaseFragment() {

    val args: DistributionsFragmentArgs by navArgs()

    private val viewModel: DistributionsViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_distributions, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as HumansisActivity).supportActionBar?.title = args.projectName
        (activity as HumansisActivity).supportActionBar?.subtitle = getString(R.string.distributions)

        val viewAdapter = DistributionsAdapter {
            val action = DistributionsFragmentDirections.actionDistributionsFragmentToBeneficiariesFragment(
                it.id,
                it.name,
                args.projectName,
                it.commodities.any { commodity -> commodity.type == CommodityType.QR_VOUCHER.toString() })
            this.findNavController().navigate(action)
        }

        lc_distributions.init(viewAdapter)

        viewModel.distributionsLD.observe(viewLifecycleOwner, Observer {
            viewAdapter.updateDistributions(it)
        })

        viewModel.listStateLD.observe(viewLifecycleOwner, Observer(lc_distributions::setState))

        sharedViewModel.syncWorkerIsLoadingLD.observe(viewLifecycleOwner, Observer {
            if (it) viewModel.showRefreshing()
        })


        viewModel.init(args.projectId)
    }

}