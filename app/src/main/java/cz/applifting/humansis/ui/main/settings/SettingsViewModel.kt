package cz.applifting.humansis.ui.main.settings

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.extensions.suspendCommit
import cz.applifting.humansis.managers.SP_COUNTRY
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 9. 10. 2019
 */

class SettingsViewModel @Inject constructor(
    private val sp: SharedPreferences,
    private val projectsRepository: ProjectsRepository
) : BaseViewModel() {

    val countryLD: MutableLiveData<String> = MutableLiveData()
    val savedLD: MutableLiveData<Boolean> = MutableLiveData()

    init {
        loadCountrySettings()
    }

    fun loadCountrySettings() {
        countryLD.value = sp.getString(SP_COUNTRY, null)
    }

    fun updateCountrySettings(country: String) {
        val oldCountry = sp.getString(SP_COUNTRY, null)
        if (oldCountry == country) {
            return
        }

        launch {
            with(sp.edit()) {
                putString(SP_COUNTRY, country.toUpperCase(Locale.getDefault()))
                suspendCommit()
            }

            // Delete all projects to not show old data when connection breaks during switch
            projectsRepository.deleteAll()
            savedLD.value = true
        }
    }
}