package cz.applifting.humansis.ui.main.settings

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import cz.applifting.humansis.R
import cz.applifting.humansis.misc.Logger
import cz.applifting.humansis.ui.App
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.HumansisActivity
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 9. 10. 2019
 */

class SettingsFragment : BaseFragment() {

    @Inject
    lateinit var logger: Logger

    private val viewModel: SettingsViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).appComponent.inject(this)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as HumansisActivity).supportActionBar?.title = getString(cz.applifting.humansis.R.string.action_settings)
        (activity as HumansisActivity).supportActionBar?.subtitle = ""

        val navController = findNavController()

        val countries = resources.getStringArray(R.array.countries)

        val adapter = ArrayAdapter.createFromResource(context!!, R.array.countries, R.layout.item_country)
        adapter.setDropDownViewResource(R.layout.item_country_dropdown)
        spinner_country.adapter = adapter

        spinner_country.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val country = parent?.getItemAtPosition(position) as String
                viewModel.updateCountrySettings(country)
            }

        }

        btn_show_dev_logs.setOnClickListener {
            launch {
                val logs = logger.readLogs(context!!)
                val pInfo = context!!.packageManager.getPackageInfo(context!!.packageName, 0)
                logs.addFirst(pInfo.versionName)
                logs.addFirst(getAndroidVersion())

                val sb = StringBuilder()
                for (log in logs) {
                    sb.append(log).append('\n')
                }

                val action = SettingsFragmentDirections.actionSettingsFragmentToLogsDialog(sb.toString())
                navController.navigate(action)
            }
        }

        viewModel.countryLD.observe(viewLifecycleOwner, Observer<String> {
            spinner_country.setSelection(countries.indexOf(it))
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
    }


    private fun getAndroidVersion(): String {
        val release = Build.VERSION.RELEASE
        val sdkVersion = Build.VERSION.SDK_INT
        return "Android SDK: $sdkVersion ($release)"
    }
}