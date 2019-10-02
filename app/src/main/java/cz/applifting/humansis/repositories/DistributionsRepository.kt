package cz.applifting.humansis.repositories

import android.content.Context
import cz.applifting.humansis.R
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.db.DbProvider
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.model.api.Commodity
import cz.applifting.humansis.model.api.DistributedReliefRequest
import cz.applifting.humansis.model.db.DistributionLocal
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 09, September, 2019
 */
@Singleton
class DistributionsRepository @Inject constructor(val service: HumansisService, val dbProvider: DbProvider, val context: Context) {

    val db: HumansisDB by lazy { dbProvider.get() }

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
                        it.type,
                        it.completed
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

    suspend fun getUncompletedDistributions(projectId: Int): List<DistributionLocal> {
        return db.distributionsDao().findUncompletedDistributions(projectId) ?: listOf()
    }

    private fun parseCommodities(commodities: List<Commodity>): List<String> {
        return commodities.map {
            it.modalityType.name?.name ?: context.getString(R.string.unknown)
        }
    }
}