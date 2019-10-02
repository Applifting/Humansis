package cz.applifting.humansis.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.ui.BaseViewModel
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
    private val sp: SharedPreferences
) : BaseViewModel() {

    private val lastDownloadKey = "lastDownloadKey"

    val downloadingLD = MutableLiveData<Boolean>()
    val snackbarLD = MutableLiveData<String>()
    val forceOfflineReloadLD = MutableLiveData<Boolean>()
    val lastDownloadLD = MutableLiveData<String>()
    val pendingChangesLD = MutableLiveData<Boolean>()

    init {
        downloadingLD.value = false
        lastDownloadLD.value = sp.getString(lastDownloadKey, null)

        initPendingChanges()
    }

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

                pendingChangesLD.value = false
            } catch (e: Throwable) {

            } finally {
                downloadingLD.value = false
            }
        }

        val lastDownloadAt = Date().toString()
        sp.edit().putString(lastDownloadKey, lastDownloadAt).apply()
    }


    fun showSnackbar(text: String?) {
        snackbarLD.value = text
    }

    fun forceOfflineReload(force: Boolean) {
        forceOfflineReloadLD.value = force
    }

    fun markPendingChanges() {
        pendingChangesLD.value = true
    }

    private fun initPendingChanges() {
        launch {
            pendingChangesLD.value = projectsRepository
                .getProjectsOffline()
                .flatMap { distributionsRepository.getDistributionsOffline(it.id) }
                .flatMap { beneficieriesRepository.getBeneficieriesOffline(it.id) }
                .fold(false, { acc, beneficiary -> acc || beneficiary.edited })
        }
    }
}
