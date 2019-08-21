package cz.applifting.humansis.ui.main.distribution.projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cz.applifting.humansis.R
import cz.applifting.humansis.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_projects.*

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class ProjectsFragment : BaseFragment() {

    private val viewModel: ProjectsViewModel by viewModels {
        this.viewModelFactory
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewManager = LinearLayoutManager(context)
        val viewAdapter = ProjectsAdapter {
            val action = ProjectsFragmentDirections.chooseProject(it.id)
            this.findNavController().navigate(action)
        }

        rv_projects.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        viewModel.projectsLD.observe(this, Observer {
            viewAdapter.updateProjects(it)
        })

        viewModel.loadProjects()
    }
}