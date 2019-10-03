package cz.applifting.humansis.ui.main.distribute.beneficiary

import androidx.lifecycle.MutableLiveData
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

    val distributedLD = MutableLiveData<Boolean>()
    val qrBookletIdLD = MutableLiveData<String>()

    init {
        qrBookletIdLD.value = null
    }

    internal fun editBeneficiary(isDistributed: Boolean, beneficiaryId: Int) {
        launch {
            val beneficiary = beneficieriesRepository.getBeneficiaryOffline(beneficiaryId)
            val updatedBeneficiary = if (qrBookletIdLD.value != null) beneficiary.copy(distributed = isDistributed, qrBooklets = mutableListOf(qrBookletIdLD.value!!)) else beneficiary.copy(distributed = true)
            beneficieriesRepository.updateBeneficiaryOffline(updatedBeneficiary)

            if (isDistributed) {
                pendingChangesRepository.createPendingChange(beneficiaryId)
            } else {
                // TODO delete pending change
            }

            distributedLD.value = true
        }
    }

    fun setScannedBooklet(bookletId: String?) {
        qrBookletIdLD.value = bookletId
    }
}