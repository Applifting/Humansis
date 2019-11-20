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
    private val projectsRepository: ProjectsRepository, val distributionsRepository: DistributionsRepository, context: Context) : BaseListViewModel(context) {

    val projectsLD: MutableLiveData<List<ProjectModel>> = MutableLiveData()

    init {
        launch {
            showRefreshing()
            projectsRepository
                .getProjectsOffline()
                .map { newProjects ->
                    newProjects?.map {
                        //todo find better solution to count uncompleted distributions
                        val uncompleteDistributions = distributionsRepository.getUncompletedDistributions(it.id)
                        val projectModel = ProjectModel(it.id, it.name, it.numberOfHouseholds, uncompleteDistributions.isEmpty())
                        projectModel
                    }
                }
                .collect {
                    projectsLD.value = it
                    finishLoading(it)
                }
        }
    }
}