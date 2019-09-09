package cz.applifting.humansis.ui.main.distribute.projects

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.model.db.ProjectLocal
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.ui.BaseViewModel
import cz.applifting.humansis.ui.components.ListComponentState
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class ProjectsViewModel @Inject constructor(val projectsRepository: ProjectsRepository) : BaseViewModel() {

    val projectsLD: MutableLiveData<List<ProjectLocal>> = MutableLiveData()
    val listStateLD: MutableLiveData<ListComponentState> = MutableLiveData()

    fun loadProjects(download: Boolean = false) {
        listStateLD.value = ListComponentState(isRefreshing = download, isRetrieving = !download)

        launch {
            val projects = if (download) projectsRepository.getProjectsOnline() else projectsRepository.getProjectsOffline()
            projectsLD.value = projects
            listStateLD.value = ListComponentState()
        }
    }
}