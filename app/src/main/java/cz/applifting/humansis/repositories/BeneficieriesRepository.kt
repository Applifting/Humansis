package cz.applifting.humansis.repositories

import android.content.Context
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.db.DbProvider
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.model.api.*
import cz.applifting.humansis.model.db.BeneficiaryLocal
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 09, September, 2019
 */
@Singleton
class BeneficieriesRepository @Inject constructor(val service: HumansisService, val dbProvider: DbProvider, val context: Context) {

    val db: HumansisDB by lazy { dbProvider.get() }

    suspend fun getBeneficieriesOnline(distributionId: Int): List<BeneficiaryLocal>? {
        return try {
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
                        parseQRBooklets(it.booklets)
                    )
                }

            db.beneficiariesDao().deleteByDistribution(distributionId)
            db.beneficiariesDao().insertAll(result)

            result
        } catch (e: Throwable) {
            null
        }
    }

    suspend fun getBeneficieriesOffline(distributionId: Int): List<BeneficiaryLocal> {
        return db.beneficiariesDao().getByDistribution(distributionId) ?: listOf()
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

        if (beneficiaryLocal.qrBooklets.isNotEmpty()) {
            assignBooklet(beneficiaryLocal.qrBooklets.first(), beneficiaryLocal.beneficiaryId, beneficiaryLocal.distributionId)
        }

    }

    private suspend fun setDistributedRelief(ids: List<Int>) {
        try {
            val result = service.setDistributedRelief(DistributedReliefRequest(ids))
            result
        } catch (e: HttpException) {
            null
        }
    }

    private suspend fun assignBooklet(code: String, beneficiaryId: Int, distributionId: Int) {
        try {
            val result = service.assignBooklet(beneficiaryId, distributionId, AssingBookletRequest(code))
            result
        } catch (e: HttpException) {
            null
        }
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