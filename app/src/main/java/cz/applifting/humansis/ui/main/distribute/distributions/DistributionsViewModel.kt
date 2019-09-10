package cz.applifting.humansis.ui.main.distribute.distributions

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.model.db.DistributionLocal
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.ui.BaseViewModel
import cz.applifting.humansis.ui.components.ListComponentState
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class DistributionsViewModel @Inject constructor(
    private val distributionsRepository: DistributionsRepository
) : BaseViewModel() {

    val distributionsLD: MutableLiveData<List<DistributionLocal>> = MutableLiveData()
    val listStateLD: MutableLiveData<ListComponentState> = MutableLiveData()

    fun loadDistributions(projectId: Int, download: Boolean) {

        launch {
            listStateLD.value = ListComponentState(isRefreshing = download, isRetrieving = !download)

            val distributions = if (download) {
                distributionsRepository.getDistributionsOnline(projectId)
            } else {
                distributionsRepository.getDistributionsOffline(projectId)
            }

            distributionsLD.value = distributions
            listStateLD.value = ListComponentState()
        }
    }

/*    private suspend fun saveBeneficiaries(distributions: List<Distribution>) {

        distributions.map { distribution ->
            val beneficiaries = mutableListOf<BeneficiaryLocal>()

            val distributionBeneficiaries = service.getByDistribution(distribution.id)

            distributionBeneficiaries.map { distributionBeneficiary ->

                val vulnerabilities = distributionBeneficiary.beneficiary.vulnerabilities
                val vulnerabilitiesLocal = mutableListOf<String>()

                vulnerabilities.map {
                    vulnerabilitiesLocal.add(it.vulnerabilityName)
                }

                // todo find out mapping for distributed flag
                val beneficiaryLocal = BeneficiaryLocal(
                    distributionBeneficiary.id,
                    distributionBeneficiary.beneficiary.givenName,
                    distributionBeneficiary.beneficiary.familyName,
                    distribution.id,
                    distributionBeneficiary.beneficiary.distributed,
                    vulnerabilitiesLocal
                )
                beneficiaries.add(beneficiaryLocal)

            }

            humansisDB.beneficiariesDao().insertAll(beneficiaries)
        }
    }*/
}