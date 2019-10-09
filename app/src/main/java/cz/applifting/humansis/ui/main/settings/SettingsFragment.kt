package cz.applifting.humansis.ui.main.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.showSoftKeyboard
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.HumansisActivity
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 9. 10. 2019
 */

class SettingsFragment : BaseFragment() {

    private val viewModel: SettingsViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as HumansisActivity).supportActionBar?.title = getString(R.string.action_settings)
        (activity as HumansisActivity).supportActionBar?.subtitle = ""

        viewModel.countryLD.observe(viewLifecycleOwner, Observer {
            et_country.isEnabled = false
            et_country.setText(it)
        })

        viewModel.savedLD.observe(viewLifecycleOwner, Observer<Boolean> {
            val message = if (it) {
                sharedViewModel.synchronize()
                getString(R.string.settings_country_update_success)
            } else {
                getString(R.string.settings_country_update_error)
            }

            viewModel.loadCountrySettings()

            view?.let { view ->
                Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
            }

        })

        btn_edit.setOnClickListener {
            et_country.isEnabled = !et_country.isEnabled
            if (et_country.isEnabled) {
                et_country.requestFocus()
                et_country.setSelection(et_country.text.length)
                et_country.showSoftKeyboard()
                btn_edit.text = getString(R.string.settings_save)
            } else {
                btn_edit.text = getString(R.string.settings_edit)
                viewModel.updateCountrySettings(et_country.text.toString())
            }
        }
    }
}