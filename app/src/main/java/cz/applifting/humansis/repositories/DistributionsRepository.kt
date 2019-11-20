package cz.applifting.humansis.repositories

import android.content.Context
import android.util.Log
import cz.applifting.humansis.R
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.db.DbProvider
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.model.CommodityType
import cz.applifting.humansis.model.api.Commodity
import cz.applifting.humansis.model.db.CommodityLocal
import cz.applifting.humansis.model.db.DistributionLocal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 09, September, 2019
 */
@Singleton
class DistributionsRepository @Inject constructor(val service: HumansisService, val dbProvider: DbProvider, val context: Context) {

    private val db: HumansisDB by lazy { dbProvider.get() }

    suspend fun getDistributionsOnline(projectId: Int): List<DistributionLocal>? {
        val result = service
            .getDistributions(projectId)
            .filter { // Skip all distributions distributing mobile money, as it is necessary to have a desktop for their distribution
                it.commodities.fold(true, { acc, commodity ->
                    commodity.modalityType.name != CommodityType.MOBILE_MONEY && acc
                })
            }
            .filter { it.validated && !it.archived && !it.completed }
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

        return result
    }

    fun getDistributionsOffline(projectId: Int): Flow<List<DistributionLocal>> {
        return db.distributionsDao().getByProject(projectId)
    }

    suspend fun getDistributionsOfflineSuspend(projectId: Int): List<DistributionLocal> {
        return db.distributionsDao().getByProjectSuspend(projectId)
    }

    suspend fun getUncompletedDistributions(projectId: Int): List<DistributionLocal> {
        return db.distributionsDao().findUncompletedDistributions(projectId) ?: listOf()
    }

    private fun parseCommodities(commodities: List<Commodity>): List<CommodityLocal> {
        return commodities.map {
            CommodityLocal(it.modalityType.name?.name ?: context.getString(R.string.unknown), it.value, it.unit)
        }
    }
}