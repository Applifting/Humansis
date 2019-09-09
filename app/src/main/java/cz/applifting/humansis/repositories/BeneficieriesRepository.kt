package cz.applifting.humansis.repositories

import android.app.Application
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.model.api.Vulnerability
import cz.applifting.humansis.model.db.BeneficiaryLocal
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 09, September, 2019
 */
class BeneficieriesRepository @Inject constructor(val service: HumansisService, val db: HumansisDB, val context: Application) {

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
        } catch (e: HttpException) {
            null
        }
    }

    suspend fun getDistributionsOffline(distributionId: Int): List<BeneficiaryLocal> {
        return db.beneficiariesDao().getByDistribution(distributionId) ?: listOf()
    }


    private fun parseVulnerabilities(vulnerability: List<Vulnerability>): List<String> {
        return vulnerability.map { it.vulnerabilityName }
    }

}