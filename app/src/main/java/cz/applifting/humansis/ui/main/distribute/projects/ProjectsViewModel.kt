package cz.applifting.humansis.ui.main.distribute.projects

import android.content.Context
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.model.db.ProjectLocal
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.ui.main.BaseListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
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

    val projectsLD: MutableLiveData<List<ProjectLocal>> = MutableLiveData()

    init {
        launch {
            distributionsRepository
                .getAllDistributions()
                .flatMapMerge { distributions ->
                    projectsRepository
                        .getProjectsOffline()
                        .map {
                            Pair(distributions, it)
                        }
                }
                .map { (distributions, projects) ->
                    projects.filter { project ->
                        distributions.any { it.projectId == project.id && !it.completed }
                    }
                }
                .collect {
                    projectsLD.value = it
                    showRetrieving(false, it.isNotEmpty())
                }
        }
    }
}