package cz.applifting.humansis.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import cz.applifting.humansis.R

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 11, September, 2019
 */
class HumansisActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_humansis)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }

        val navController = findNavController(R.id.nav_host_fragment_base)
        (application as App).appComponent.inject(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        return false
    }
}