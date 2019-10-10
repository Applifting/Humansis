package cz.applifting.humansis.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import cz.applifting.humansis.BuildConfig
import cz.applifting.humansis.R
import cz.applifting.humansis.R.id.action_open_status_dialog
import cz.applifting.humansis.R.id.snackbar_text
import cz.applifting.humansis.extensions.isNetworkConnected
import cz.applifting.humansis.extensions.simpleDrawable
import cz.applifting.humansis.misc.HumansisError
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.HumansisActivity
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.menu_status_button.view.*
import kotlinx.android.synthetic.main.nav_header_main.*




/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class MainFragment : BaseFragment() {

    private val viewModel: MainViewModel by viewModels { viewModelFactory }
    private lateinit var mainNavController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(cz.applifting.humansis.R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val appBarConfiguration = AppBarConfiguration(
            setOf(cz.applifting.humansis.R.id.projectsFragment, cz.applifting.humansis.R.id.settingsFragment),
            drawer_layout
        )

        val fragmentContainer = view?.findViewById<View>(cz.applifting.humansis.R.id.nav_host_fragment) ?: throw HumansisError("Cannot find nav host in main")
        mainNavController = Navigation.findNavController(fragmentContainer)

        (activity as HumansisActivity).setSupportActionBar(tb_toolbar)

        tb_toolbar.setupWithNavController(mainNavController, appBarConfiguration)
        nav_view.setupWithNavController(mainNavController)

        // Define Observers
        viewModel.userLD.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                findNavController().navigate(R.id.logout)
                return@Observer
            }

            tv_username?.text = it.username
        })

        sharedViewModel.snackbarLD.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                showSnackbar(it)
                sharedViewModel.showSnackbar(null)
            }
        })

        sharedViewModel.syncWorkerIsLoadingLD.observe(viewLifecycleOwner, Observer {

        })

        val tvAppVersion = nav_view.getHeaderView(0).findViewById<TextView>(R.id.tv_app_version)
        tvAppVersion.text = BuildConfig.VERSION_NAME

        btn_logout.setOnClickListener {
            AlertDialog.Builder(context!!)
                .setMessage(getString(cz.applifting.humansis.R.string.logout_alert_text))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    viewModel.logout()
                }
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }

        sharedViewModel.refreshPendingChanges()
        sharedViewModel.tryFirstDownload()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        val networkFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        activity?.registerReceiver(networkReceiver, networkFilter)
    }

    override fun onPause() {
        super.onPause()
        activity?.unregisterReceiver(networkReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(cz.applifting.humansis.R.menu.menu_status, menu)
        // A fix for action with custom layout
        // https://stackoverflow.com/a/35265797
        val item = menu.findItem(action_open_status_dialog)
        item.actionView.setOnClickListener { onOptionsItemSelected(item) }

        val ivStatus = item.actionView.findViewById<ImageView>(cz.applifting.humansis.R.id.iv_status)
        ivStatus.simpleDrawable(if (context?.isNetworkConnected() == true) cz.applifting.humansis.R.drawable.ic_online else cz.applifting.humansis.R.drawable.ic_offline)

        sharedViewModel.pendingChangesLD.observe(viewLifecycleOwner, Observer {
            item.actionView.iv_pending_changes.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            action_open_status_dialog -> {
                mainNavController.navigate(cz.applifting.humansis.R.id.uploadDialog)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context?.let {
                activity?.invalidateOptionsMenu()
            }
        }
    }

    private fun showSnackbar(text: String) {
        val snackbar = Snackbar.make(view!!, text, Snackbar.LENGTH_SHORT)
        val layout = snackbar.view as Snackbar.SnackbarLayout
        val textView = layout.findViewById(snackbar_text) as TextView
        textView.maxLines = 5  // show multiple line
        snackbar.show()
    }
}
