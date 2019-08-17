package cz.applifting.humansis.ui.login

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import cz.applifting.humansis.R
import cz.applifting.humansis.ui.App
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 15, August, 2019
 */
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LoginViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        (application as App).appComponent.inject(this)

        btn_login.setOnClickListener {
            viewModel.login(et_username.text.toString(), et_username.text.toString())
        }
    }

    private fun enableWidgets(shouldEnable: Boolean) {
        btn_login.isEnabled = shouldEnable
        et_username.isEnabled = shouldEnable
        et_password.isEnabled = shouldEnable
    }
}