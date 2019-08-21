package cz.applifting.humansis.ui.main.distribution.distributions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import cz.applifting.humansis.R
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.main.MainActivity
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
        (activity as MainActivity).actionBar?.title = args.projectName

        val viewManager = LinearLayoutManager(context)
        val viewAdapter = DistributionsAdapter {
            //val action = ProjectsFragmentDirections.chooseProject(it.id)
            //this.findNavController().navigate(action)
        }

        rv_distributions.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        viewModel.distributionsLD.observe(this, Observer {
            viewAdapter.updateDistributions(it)
        })

        viewModel.distributionsViewStateLD.observe(this, Observer {
            srl_reload.isRefreshing = it.refreshing
        })

        srl_reload.setOnRefreshListener { viewModel.loadDistributions(args.projectId) }

        viewModel.loadDistributions(args.projectId)
    }
}