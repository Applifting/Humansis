package cz.applifting.humansis.ui.main.distribute.beneficiary

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.repositories.PendingChangesRepository
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 9. 9. 2019
 */

class BeneficiaryViewModel @Inject constructor(private val beneficieriesRepository: BeneficieriesRepository, private val pendingChangesRepository: PendingChangesRepository) :
    BaseViewModel() {

    val beneficiaryLD = MutableLiveData<BeneficiaryLocal>()
    var distributed: Boolean? = null


    fun loadBeneficiary(id: Int) {
        launch {
            beneficiaryLD.value = beneficieriesRepository.getBeneficiaryOffline(id)
        }
    }

    internal fun editBeneficiary(isDistributed: Boolean, beneficiaryId: Int, qrBooklet: String?) {
        launch {
            val beneficiary = beneficieriesRepository.getBeneficiaryOffline(beneficiaryId)

            val updatedBeneficiary = beneficiary.copy(distributed = isDistributed, qrBooklets = listOfNotNull(qrBooklet), edited = true)
            beneficieriesRepository.updateBeneficiaryOffline(updatedBeneficiary)

            if (isDistributed) {
                pendingChangesRepository.createPendingChange(beneficiaryId)
            } else {
                pendingChangesRepository.deletePendingChangeByBeneficiaryId(beneficiaryId)
            }

            distributed = isDistributed
            beneficiaryLD.value = updatedBeneficiary
        }
    }
}