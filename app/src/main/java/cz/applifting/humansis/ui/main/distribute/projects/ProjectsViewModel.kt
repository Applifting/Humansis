package cz.applifting.humansis.ui.main.distribute.projects

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.model.ui.ProjectModel
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.ui.main.BaseListViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class ProjectsViewModel @Inject constructor(val projectsRepository: ProjectsRepository, val distributionsRepository: DistributionsRepository, context: Context) : BaseListViewModel(context) {

    val projectsLD: MutableLiveData<List<ProjectModel>> = MutableLiveData()

    fun loadProjects(download: Boolean = false) {
        launch {
            if (download) {
                showRefreshing()
            } else {
                showRetrieving()
            }

            val projects = if (download) projectsRepository.getProjectsOnline() else projectsRepository.getProjectsOffline()

            Log.d("projectsx", projects.toString())
            val projectsModel = projects?.map {
                //todo find better solution to count uncompleted distributions
                val uncompleteDistributions = distributionsRepository.getUncompletedDistributions(it.id)
                val projectModel = ProjectModel(it.id, it.name, it.numberOfHouseholds, uncompleteDistributions.isEmpty())
                projectModel
            }

            projectsLD.value = projectsModel

            finishLoading(projects)
        }
    }

}