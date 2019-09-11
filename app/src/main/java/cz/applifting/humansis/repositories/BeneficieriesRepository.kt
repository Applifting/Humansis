package cz.applifting.humansis.repositories

import android.content.Context
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.db.DbProvider
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.model.api.Vulnerability
import cz.applifting.humansis.model.db.BeneficiaryLocal
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
                        it.beneficiary.givenName,
                        it.beneficiary.familyName,
                        distributionId,
                        it.beneficiary.distributed,
                        parseVulnerabilities(it.beneficiary.vulnerabilities)
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
        val db = dbProvider.get()
        return db.beneficiariesDao().getByDistribution(distributionId) ?: listOf()
    }

    suspend fun getBeneficiaryOffline(beneficiaryId: Int): BeneficiaryLocal {
        val db = dbProvider.get()
        return db.beneficiariesDao().findById(beneficiaryId)
    }

    suspend fun updateBeneficiaryOffline(beneficiary: BeneficiaryLocal) {
        val db = dbProvider.get()
        return db.beneficiariesDao().update(beneficiary)
    }

    private fun parseVulnerabilities(vulnerability: List<Vulnerability>): List<String> {
        return vulnerability.map { it.vulnerabilityName }
    }

}