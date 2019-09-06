package cz.applifting.humansis.ui.main.distribute.distributions

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.model.api.Distribution
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class DistributionsViewModel @Inject constructor(
    private val service: HumansisService, private val humansisDB: HumansisDB
) : BaseViewModel() {

    val distributionsLD: MutableLiveData<List<Distribution>> = MutableLiveData()
    val distributionsViewStateLD: MutableLiveData<DistributionsViewState> = MutableLiveData()

    fun loadDistributions(projectId: Int) {
        launch {
            distributionsViewStateLD.value = DistributionsViewState(true)
            val distributions = service.getDistributions(projectId)
            saveBeneficiaries(distributions)
            distributionsLD.value = distributions
            distributionsViewStateLD.value = DistributionsViewState(false)
        }
    }

    private suspend fun saveBeneficiaries(distributions: List<Distribution>) {

        distributions.map { distribution ->
            val beneficiaries = mutableListOf<BeneficiaryLocal>()

            val distributionBeneficiaries = service.getDistributionBeneficiaries(distribution.id)

            distributionBeneficiaries.map { distributionBeneficiary ->
                // todo find out mapping for distributed flag
                val beneficiaryLocal = BeneficiaryLocal(
                    distributionBeneficiary.id,
                    distributionBeneficiary.beneficiary.givenName,
                    distributionBeneficiary.beneficiary.familyName,
                    distribution.id,
                    distributionBeneficiary.beneficiary.distributed)
                beneficiaries.add(beneficiaryLocal)
            }

            humansisDB.beneficiaryDao().insertAll(beneficiaries)
        }
    }
}