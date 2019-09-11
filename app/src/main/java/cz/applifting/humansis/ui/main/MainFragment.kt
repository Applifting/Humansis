package cz.applifting.humansis.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import cz.applifting.humansis.R
import cz.applifting.humansis.ui.App
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.HumansisActivity
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class MainFragment : BaseFragment() {

    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.projectsFragment),
            drawer_layout
        )

        val navController = (activity as HumansisActivity).navController

        tb_toolbar.setupWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        (application as App).appComponent.inject(this)

        // Define Observers
        viewModel.userLD.observe(this, Observer {
            if (it == null) {
                navController.navigate(R.id.loginActivity)
            } else {
                tv_username.text = it.username
                tv_email.text = it.email
            }
        })

        btn_logout.setOnClickListener {
            viewModel.logout()
        }

        sharedViewModel.tryDownloadingAll()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUser()
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
