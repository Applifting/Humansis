package cz.applifting.humansis.ui.main.distribute.projects

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.extensions.getDate
import cz.applifting.humansis.model.db.ProjectLocal
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.ui.main.BaseListViewModel
import cz.applifting.humansis.ui.main.LAST_DOWNLOAD_KEY
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
    private val sp: SharedPreferences,
    context: Context
) : BaseListViewModel(context) {

    val projectsLD: MutableLiveData<List<ProjectLocal>> = MutableLiveData()

    init {
        loadProjects()
    }

    fun loadProjects() {
        launch {

            if (sp.getDate(LAST_DOWNLOAD_KEY) == null) {
                return@launch
            }

            projectsRepository
                .getProjectsOffline()
                .flatMapMerge { projects ->
                    distributionsRepository
                        .getAllDistributions()
                        .map {
                            Pair(it, projects)
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