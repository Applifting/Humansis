package cz.applifting.humansis.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cz.applifting.humansis.BuildConfig
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

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        (application as App).appComponent.inject(this)

        btn_login.setOnClickListener {
            viewModel.login(et_username.text.toString(), et_password.text.toString())
        }

        viewModel.viewState.observe(this, Observer{ viewState ->
            et_username.isEnabled = viewState.etUsernameIsEnabled
            et_password.isEnabled = viewState.etPasswordIsEnabled
            btn_login.visibility = viewState.btnLoginVisibility
            pb_loading.visibility = viewState.pbLoadingVisible
        })

        if (BuildConfig.DEBUG) {
            et_username.setText("demo@humansis.org")
            et_password.setText("Testing123")
        }
    }
}