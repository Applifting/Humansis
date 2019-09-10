package cz.applifting.humansis.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 10, September, 2019
 */
class SharedViewModel @Inject constructor(
    private val projectsRepository: ProjectsRepository,
    private val distributionsRepository: DistributionsRepository,
    private val beneficieriesRepository: BeneficieriesRepository
) : BaseViewModel() {

    val downloadingLD = MutableLiveData<Boolean>()

    fun tryDownloadingAll() {
        // TODO when should we download all?
        launch {
            try {
                Log.d("asdf", "downloading all")
                downloadingLD.value = true
                projectsRepository
                    .getProjectsOnline()
                    .orEmpty()
                    .flatMap {
                        distributionsRepository.getDistributionsOnline(it.id) ?: listOf()
                    }
                    .flatMap {
                        beneficieriesRepository.getBeneficieriesOnline(it.id) ?: listOf()
                    }
            } catch (e: Throwable) {

            } finally {
                downloadingLD.value = false
            }
        }
    }
}