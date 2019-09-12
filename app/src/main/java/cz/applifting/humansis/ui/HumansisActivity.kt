package cz.applifting.humansis.ui

import android.os.Bundle
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

        val navController = findNavController(R.id.nav_host_fragment_base)

        (application as App).appComponent.inject(this)
    }
}