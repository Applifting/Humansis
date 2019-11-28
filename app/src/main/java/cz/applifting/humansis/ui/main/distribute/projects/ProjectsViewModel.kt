package cz.applifting.humansis.ui.main.distribute.projects

import android.content.Context
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.model.ui.ProjectModel
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.ui.main.BaseListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class ProjectsViewModel @Inject constructor(
    private val projectsRepository: ProjectsRepository,
    val distributionsRepository: DistributionsRepository,
    context: Context
) : BaseListViewModel(context) {

    val projectsLD: MutableLiveData<List<ProjectModel>> = MutableLiveData()

    init {
        launch {
            showRefreshing(true)

            projectsRepository
                .getProjectsOffline()
                .map { newProjects ->
                    newProjects.map {
                        //todo find better solution to count uncompleted distributions
                        val uncompleteDistributions = distributionsRepository.getUncompletedDistributionsSuspend(it.id)
                        val projectModel = ProjectModel(it.id, it.name, it.numberOfHouseholds, uncompleteDistributions.isEmpty())
                        projectModel
                    }.filter { !it.completed }
                }
                .collect {
                    projectsLD.value = it
                    showRetrieving(false, it.isNotEmpty())
                }
        }

        launch {
            // Refresh colors of projects to indicate whether they have active distributions
            distributionsRepository
                .getAllDistributions()
                .collect { newDistributions ->
                    if (projectsLD.value != null) {
                        projectsLD.value = projectsLD.value?.map { project ->
                            val someProjectDistribution = newDistributions.find { it.projectId == project.id }
                            val projectModel = project.copy(completed = someProjectDistribution == null)
                            projectModel
                        }
                    }
                }
        }
    }
}