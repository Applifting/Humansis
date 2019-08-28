package cz.applifting.humansis.ui.main.distribute.distribution

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import cz.applifting.humansis.R
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.main.MainActivity
import cz.applifting.humansis.ui.main.distribute.distributions.DistributionsFragmentArgs

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 28, August, 2019
 */
class DistributionFragment : BaseFragment() {

    val args: DistributionsFragmentArgs by navArgs()

    private val viewModel: DistributionViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_distribution, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).actionBar?.title = args.projectName

//        val viewManager = LinearLayoutManager(context)
//        val viewAdapter = DistributionsAdapter(requireContext()) {
//            val action = ProjectsFragmentDirections.chooseProject(it.id)
//            this.findNavController().navigate(action)
//        }
//
//        rv_distributions.apply {
//            setHasFixedSize(true)
//            layoutManager = viewManager
//            adapter = viewAdapter
//        }
//
//        viewModel.distributionsLD.observe(viewLifecycleOwner, Observer {
//            viewAdapter.updateDistributions(it)
//        })
//
//        viewModel.distributionsViewStateLD.observe(viewLifecycleOwner, Observer {
//            srl_reload.isRefreshing = it.refreshing
//        })
//
//        srl_reload.setOnRefreshListener { viewModel.loadDistributions(args.projectId) }
//
//        viewModel.loadDistributions(args.projectId)
    }
}