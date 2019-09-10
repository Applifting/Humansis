package cz.applifting.humansis.ui.main.distribute.projects

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.model.db.ProjectLocal
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.ui.main.BaseListViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class ProjectsViewModel @Inject constructor(val projectsRepository: ProjectsRepository) : BaseListViewModel() {

    val projectsLD: MutableLiveData<List<ProjectLocal>> = MutableLiveData()

    fun loadProjects(download: Boolean = false) {
        launch {
            if (download) {
                showRefreshing()
            } else {
                showRetrieving()
            }

            val projects = if (download) projectsRepository.getProjectsOnline() else projectsRepository.getProjectsOffline()
            projectsLD.value = projects

            finishLoading(projects)
        }
    }

}