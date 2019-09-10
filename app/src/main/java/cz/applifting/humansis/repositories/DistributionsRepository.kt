package cz.applifting.humansis.repositories

import android.app.Application
import cz.applifting.humansis.R
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.model.api.Commodity
import cz.applifting.humansis.model.db.DistributionLocal
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 09, September, 2019
 */
class DistributionsRepository @Inject constructor(val service: HumansisService, val db: HumansisDB, val context: Application) {

    suspend fun getDistributionsOnline(projectId: Int): List<DistributionLocal>? {
        return try {
            val result = service
                .getDistributions(projectId)
                .map {
                    DistributionLocal(
                        it.id,
                        it.name,
                        it.distributionBeneficiaries?.size ?: 0,
                        parseCommodities(it.commodities),
                        it.dateDistribution ?: context.getString(R.string.unknown),
                        projectId,
                        it.type
                    )
                }

            db.distributionsDao().deleteByProject(projectId)
            db.distributionsDao().insertAll(result)

            result
        } catch (e: HttpException) {
            null
        }
    }

    suspend fun getDistributionsOffline(projectId: Int): List<DistributionLocal> {
        return db.distributionsDao().getByProject(projectId) ?: listOf()
    }


    private fun parseCommodities(commodities: List<Commodity>): List<String> {
        return commodities.map {
            it.modalityType.name?.name ?: context.getString(R.string.unknown)
        }
    }
}