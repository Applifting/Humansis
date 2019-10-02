package cz.applifting.humansis.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import cz.applifting.humansis.R
import cz.applifting.humansis.R.id.action_open_status_dialog
import cz.applifting.humansis.extensions.isNetworkConnected
import cz.applifting.humansis.extensions.simpleDrawable
import cz.applifting.humansis.extensions.visible
import cz.applifting.humansis.misc.HumansisError
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.HumansisActivity
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */

const val PENDING_CHANGES_ACTION = "PENDING_CHANGES_ACTION"

class MainFragment : BaseFragment() {

    private val viewModel: MainViewModel by viewModels { viewModelFactory }
    private lateinit var mainNavController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.projectsFragment),
            drawer_layout
        )

        val fragmentContainer = view?.findViewById<View>(R.id.nav_host_fragment) ?: throw HumansisError("Cannot find nav host in main")
        mainNavController = Navigation.findNavController(fragmentContainer)

        (activity as HumansisActivity).setSupportActionBar(tb_toolbar)

        tb_toolbar.setupWithNavController(mainNavController, appBarConfiguration)
        nav_view.setupWithNavController(mainNavController)


        // Define Observers
        viewModel.userLD.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                activity?.finishAffinity()
                return@Observer
            }

            tv_username.text = it.username
            tv_email.text = it.email
        })

        sharedViewModel.snackbarLD.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Snackbar.make(view!!, it, Snackbar.LENGTH_SHORT).show()
                sharedViewModel.showSnackbar(null)
            }
        })

        btn_logout.setOnClickListener {
            viewModel.logout()
        }

        sharedViewModel.hasPendingChangesLD.observe(viewLifecycleOwner, Observer {
            activity?.invalidateOptionsMenu()
        })

        sharedViewModel.checkPendingChanges()

        sharedViewModel.tryDownloadingAll()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        context?.let {
            val networkFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            it.registerReceiver(mainReceiver, networkFilter)
            val pendingChangesFilter = IntentFilter(PENDING_CHANGES_ACTION)
            LocalBroadcastManager.getInstance(it).registerReceiver(mainReceiver, pendingChangesFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        activity?.unregisterReceiver(mainReceiver)
        context?.let {
            val localBroadcastManager = LocalBroadcastManager.getInstance(it)
            localBroadcastManager.unregisterReceiver(mainReceiver)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_status, menu)
        // A fix for action with custom layout
        // https://stackoverflow.com/a/35265797
        val item = menu.findItem(action_open_status_dialog)
        item.actionView.setOnClickListener { onOptionsItemSelected(item) }
        val ivStatus = item.actionView.findViewById<ImageView>(R.id.iv_status)
        ivStatus.simpleDrawable(if (context?.isNetworkConnected() == true) R.drawable.ic_online else R.drawable.ic_offline)
        val ivPendingChanges = item.actionView.findViewById<ImageView>(R.id.iv_pending_changes)
        ivPendingChanges.visible(sharedViewModel.hasPendingChangesLD.value ?: false)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            action_open_status_dialog -> {
                mainNavController.navigate(R.id.uploadDialog)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private val mainReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context?.let {
                sharedViewModel.checkPendingChanges()
                activity?.invalidateOptionsMenu()
            }
        }
    }
}
