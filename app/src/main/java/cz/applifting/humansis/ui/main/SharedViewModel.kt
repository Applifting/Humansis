package cz.applifting.humansis.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.extensions.setDate
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.repositories.PendingChangesRepository
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.ui.BaseViewModel
import cz.applifting.humansis.ui.main.distribute.upload.LAST_DATA_UPDATE
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 10, September, 2019
 */
class SharedViewModel @Inject constructor(
    private val projectsRepository: ProjectsRepository,
    private val distributionsRepository: DistributionsRepository,
    private val beneficieriesRepository: BeneficieriesRepository,
    private val pendingChangesRepository: PendingChangesRepository,
    private val sp: SharedPreferences
) : BaseViewModel() {

    val downloadingLD = MutableLiveData<Boolean>()
    val snackbarLD = MutableLiveData<String>()
    val forceOfflineReload = MutableLiveData<Boolean>()
    val hasPendingChangesLD = MutableLiveData<Boolean>()

    fun tryDownloadingAll() {
        // TODO when should we download all?
        launch {
            try {
                downloadingLD.value = true
                projectsRepository
                    .getProjectsOnline()
                    .orEmpty()
                    .map { async { distributionsRepository.getDistributionsOnline(it.id) } }
                    .flatMap { it.await() ?: listOf() }
                    .map { async { beneficieriesRepository.getBeneficieriesOnline(it.id) } }
                    .map { it.await() }

                sp.setDate(LAST_DATA_UPDATE, Date())

            } catch (e: Throwable) {

            } finally {
                downloadingLD.value = false
            }
        }
    }

    fun showSnackbar(text: String?) {
        snackbarLD.value = text
    }

    fun forceOfflineReload(force: Boolean) {
        forceOfflineReload.value = force
    }

    fun checkPendingChanges() {
        launch {
            val hasChanges = pendingChangesRepository.hasPendingChanges()
            hasPendingChangesLD.value = hasChanges
        }
    }
}