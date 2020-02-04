package cz.applifting.humansis.repositories

import android.content.Context
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.db.DbProvider
import cz.applifting.humansis.model.db.ProvinceLocal
import cz.applifting.humansis.model.db.VulnerabilityLocal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HouseholdsRepository @Inject constructor(val service: HumansisService, val dbProvider: DbProvider, val context: Context) {

    suspend fun getFormDataOnline() {
        service.getProvinces().map {
            ProvinceLocal(
                id = it.id,
                name = it.name
            )
        }.also {
            dbProvider.get().provincesDao().also { dao ->
                dao.deleteAll()
                dao.insertAll(it)
            }
        }
        service.getVulnerabilityCriteria().map {
            VulnerabilityLocal(
                id = it.id,
                name = it.vulnerabilityName
            )
        }.also {
            dbProvider.get().vulnerabilitiesDao().also { dao ->
                dao.deleteAll()
                dao.insertAll(it)
            }
        }
    }

}
