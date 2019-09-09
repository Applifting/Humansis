package cz.applifting.humansis.repositories

import android.app.Application
import cz.applifting.humansis.R
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.model.db.ProjectLocal
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 09, September, 2019
 */
class ProjectsRepository @Inject constructor(val service: HumansisService, val db: HumansisDB, val context: Application) {

    suspend fun getProjectsOnline(): List<ProjectLocal>? {
        return try {
            val result = service
                .getProjects()
                .map { ProjectLocal(it.id, it.name ?: context.getString(R.string.unknown), it.numberOfHouseholds ?: -1) }
            
            db.projectsDao().deleteAll()
            db.projectsDao().insertAll(result)

            result
        } catch (e: HttpException) {
            null
        }
    }

    suspend fun getProjectsOffline(): List<ProjectLocal> {
        return db.projectsDao().retrieveAll() ?: listOf()
    }
}