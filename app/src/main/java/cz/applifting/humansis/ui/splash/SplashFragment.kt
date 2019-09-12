package cz.applifting.humansis.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cz.applifting.humansis.R
import cz.applifting.humansis.ui.BaseFragment

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 11, September, 2019
 */
class SplashFragment: BaseFragment() {

    private val viewModel: SplashViewModel by viewModels { viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (viewModel.shouldLogin()) {
            goToLoginScreen()
        } else {
            viewModel.getUser()
        }

        viewModel.userLD.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                goToLoginScreen()
                return@Observer
            }

            val action = SplashFragmentDirections.actionSplashFragmentToMainFragment(
                it.username,
                it.email
            )
            this.findNavController().navigate(action)
        })
    }

    private fun goToLoginScreen() {
        val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
        this.findNavController().navigate(action)
    }
}