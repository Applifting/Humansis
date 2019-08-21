package cz.applifting.humansis.ui.main.distribution.projects

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.model.api.Project
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class ProjectsViewModel @Inject constructor(val service: HumansisService) : ViewModel() {

    private val _projects = MutableLiveData(
        listOf(
            Project(1,"Project 1")
        )
    )

    val projects: LiveData<List<Project>>
        get() = _projects

    init {
        Log.d("asdf", "projects viewmodel create")
    }

    fun hello() {

    }
}