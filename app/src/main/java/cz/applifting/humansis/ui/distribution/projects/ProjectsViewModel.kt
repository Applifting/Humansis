package cz.applifting.humansis.ui.distribution.projects

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.db.Project
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class ProjectsViewModel @Inject constructor(val service: HumansisService) : ViewModel() {

    private val _projects = MutableLiveData(
        listOf(
            Project("Project 1"),
            Project("Project 2"),
            Project("Project 3"),
            Project("Project 4")
        )
    )

    val projects: LiveData<List<Project>>
        get() = _projects

    init {
        Log.d("asdf", "projects viewmodel create")
    }

    fun hello() {
        GlobalScope.launch {
            try {
                val response = service.getSalt("demo@humansis.org")
                Log.d("asdf", response.salt )
            } catch (e: HttpException) {
                Log.d("asdf", e.response().toString())
            }

        }
        Log.d("asdf", "Hello from projects viewmodel")
    }
}