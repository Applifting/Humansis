package cz.applifting.humansis.ui.main.distribute.projects

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.model.api.Project
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class ProjectsViewModel @Inject constructor(val service: HumansisService) : BaseViewModel() {

    val projectsLD: MutableLiveData<List<Project>> = MutableLiveData()
    val viewStateLD: MutableLiveData<ProjectsViewState> = MutableLiveData()

    fun loadProjects() {
        viewStateLD.value = ProjectsViewState(true)

        launch {
            try {
                val projects = service.getProjects()
                projectsLD.value = projects
                viewStateLD.value = ProjectsViewState(false)
            } catch (e: Exception) { }
        }
    }
}