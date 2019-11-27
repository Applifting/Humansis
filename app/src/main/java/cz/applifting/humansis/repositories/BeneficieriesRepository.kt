package cz.applifting.humansis.repositories

import android.content.Context
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.db.DbProvider
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.model.CommodityType
import cz.applifting.humansis.model.api.*
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.model.db.CommodityLocal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 09, September, 2019
 */
@Singleton
class BeneficieriesRepository @Inject constructor(val service: HumansisService, val dbProvider: DbProvider, val context: Context) {

    val db: HumansisDB by lazy { dbProvider.get() }

    suspend fun getBeneficieriesOnline(distributionId: Int): List<BeneficiaryLocal>? {

        val distribution = dbProvider.get().distributionsDao().getById(distributionId)

        val result = service
            .getDistributionBeneficiaries(distributionId)
            .map {
                BeneficiaryLocal(
                    it.id,
                    it.beneficiary.id,
                    it.beneficiary.givenName,
                    it.beneficiary.familyName,
                    distributionId,
                    isReliefDistributed(it.reliefs) || isBookletDistributed(it.booklets),
                    parseVulnerabilities(it.beneficiary.vulnerabilities),
                    parseReliefs(it.reliefs),
                    parseQRBooklets(it.booklets),
                    false,
                    parseCommodities(it.booklets, distribution?.commodities),
                    it.beneficiary.nationalIds?.getOrNull(0)?.idNumber
                )
            }

        db.beneficiariesDao().deleteByDistribution(distributionId)
        db.beneficiariesDao().insertAll(result)

        return result
    }

    fun getAllBeneficieriesOffline(): Flow<List<BeneficiaryLocal>> {
        return db.beneficiariesDao().getAllBeneficieries()
    }

    fun getBeneficieriesOffline(distributionId: Int): Flow<List<BeneficiaryLocal>> {
        return db.beneficiariesDao().getByDistribution(distributionId)
    }

    suspend fun getBeneficieriesOfflineSuspend(distributionId: Int): List<BeneficiaryLocal> {
        return db.beneficiariesDao().getByDistributionSuspend(distributionId)
    }

    suspend fun getBeneficiaryOffline(beneficiaryId: Int): BeneficiaryLocal {
        return db.beneficiariesDao().findById(beneficiaryId)
    }

    suspend fun updateBeneficiaryOffline(beneficiary: BeneficiaryLocal) {
        return db.beneficiariesDao().update(beneficiary)
    }

    suspend fun countReachedBeneficiariesOffline(distributionId: Int): Int {
        return db.beneficiariesDao().countReachedBeneficiaries(distributionId)
    }

    suspend fun distribute(beneficiaryId: Int) {
        val beneficiaryLocal = db.beneficiariesDao().findById(beneficiaryId)

        if (beneficiaryLocal.reliefIDs.isNotEmpty()) {
            setDistributedRelief(beneficiaryLocal.reliefIDs)
        }

        if (beneficiaryLocal.qrBooklets?.isNotEmpty() == true) {
            assignBooklet(beneficiaryLocal.qrBooklets.first(), beneficiaryLocal.beneficiaryId, beneficiaryLocal.distributionId)
        }
    }

    suspend fun checkBoookletAssignedLocally(bookletId: String): Boolean {
        val booklets = db.beneficiariesDao().getAllBooklets()

        booklets?.forEach {
            if (it.contains(bookletId)) {
                return true
            }
        }

        return false
    }

    private suspend fun setDistributedRelief(ids: List<Int>) {
        service.setDistributedRelief(DistributedReliefRequest(ids))
    }

    private suspend fun assignBooklet(code: String, beneficiaryId: Int, distributionId: Int) {
        service.assignBooklet(beneficiaryId, distributionId, AssingBookletRequest(code))
    }

    private fun parseVulnerabilities(vulnerability: List<Vulnerability>): List<String> {
        return vulnerability.map { it.vulnerabilityName }
    }

    private fun parseReliefs(reliefs: List<Relief>): List<Int> {
        return reliefs.map { it.id }
    }

    private fun parseQRBooklets(booklets: List<Booklet>): List<String> {
        return booklets.map { it.code }
    }

    private fun parseCommodities(booklets: List<Booklet>, commodities: List<CommodityLocal>?): List<CommodityLocal> {

        if (booklets.isNotEmpty()) {
            return booklets.map { booklet ->
                val bookletValue = booklet.vouchers.sumBy { it.value }
                CommodityLocal(CommodityType.QR_VOUCHER, bookletValue, booklet.currency)
            }
        }

        return commodities ?: mutableListOf()

    }

    private fun isReliefDistributed(reliefs: List<Relief>): Boolean {
        if (reliefs.isEmpty()) {
            return false
        }

        reliefs.forEach {
            if (it.distributedAt == null) {
                return false
            }
        }

        return true
    }

    private fun isBookletDistributed(booklets: List<Booklet>): Boolean {
        if (booklets.isEmpty()) {
            return false
        }

        booklets.forEach {
            if (it.status != 1) {
                return false
            }
        }

        return true
    }
}