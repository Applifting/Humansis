package cz.applifting.humansis.ui.main.distribution.projects

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

    fun loadProjects() {
        launch {
            try {
                val projects = service.getProjects()
                projectsLD.value = projects
            } catch (e: Exception) { }
        }
    }
}