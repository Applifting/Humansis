package cz.applifting.humansis.ui.main.distribute.projects

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cz.applifting.humansis.R
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.HumansisActivity
import cz.applifting.humansis.ui.components.UploadStatusDialogFragment
import kotlinx.android.synthetic.main.fragment_projects.*
import kotlinx.android.synthetic.main.menu_status_button.view.*


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class ProjectsFragment : BaseFragment() {

    private val viewModel: ProjectsViewModel by viewModels { this.viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as HumansisActivity).supportActionBar?.title = getString(R.string.app_name)
        (activity as HumansisActivity).supportActionBar?.subtitle = getString(R.string.projects)

        val adapter = ProjectsAdapter {
            val action = ProjectsFragmentDirections.chooseProject(it.id, it.name)
            this.findNavController().navigate(action)
        }

        lc_projects.init(adapter)
        lc_projects.setOnRefreshListener { viewModel.loadProjects(true) }

        viewModel.projectsLD.observe(viewLifecycleOwner, Observer {
            adapter.updateProjects(it)
        })

        viewModel.listStateLD.observe(viewLifecycleOwner, Observer(lc_projects::setState))

        sharedViewModel.downloadingLD.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.showRefreshing()
            } else {
                viewModel.loadProjects()
            }
        })

        if (sharedViewModel.downloadingLD.value == false) {
            viewModel.loadProjects()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_status, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val confirmAction = menu.findItem(R.id.action_open_status_dialog)
        val rootView = confirmAction.actionView as FrameLayout
        val btnStatus = rootView.btn_status
        btnStatus?.setOnClickListener {
            showUploadStatusDialog()
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_open_status_dialog -> {
                showUploadStatusDialog()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showUploadStatusDialog() {

        val dialogTag = "statusDialog"
        fragmentManager?.apply {
            val transaction = beginTransaction()
            findFragmentByTag(dialogTag)?.apply {
                transaction.remove(this)
            }
            transaction.addToBackStack(null)
            val dialogFragment = UploadStatusDialogFragment()
            dialogFragment.show(transaction, dialogTag)
        }
    }
}