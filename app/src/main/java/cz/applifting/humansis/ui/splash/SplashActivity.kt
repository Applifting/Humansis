package cz.applifting.humansis.ui.splash

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import cz.applifting.humansis.R
import cz.applifting.humansis.ui.App
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 11, September, 2019
 */
class SplashActivity: AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: SplashViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        (application as App).appComponent.inject(this)

        if (viewModel.shouldLogin()) {
          // ActivityNavigator(this).navigate(SplashActivityDirections.actionSplashActivityToNavGraph())
        }
    }
}