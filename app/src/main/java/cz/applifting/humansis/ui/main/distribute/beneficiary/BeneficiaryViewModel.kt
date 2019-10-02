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
    val bookletIdLD = MutableLiveData<String>()

    init {
        bookletIdLD.value = null
    }

    internal fun markAsDistributed(beneficiaryId: Int) {
        launch {
            val beneficiary = beneficieriesRepository.getBeneficiaryOffline(beneficiaryId)
            val updatedBeneficiary = if (bookletIdLD.value != null) beneficiary.copy(distributed = true, booklets = mutableListOf(bookletIdLD.value!!)) else beneficiary.copy(distributed = true)
            beneficieriesRepository.updateBeneficiaryOffline(updatedBeneficiary)
            pendingChangesRepository.createPendingChange(beneficiaryId)
            distributedLD.value = true
        }
    }

    fun setScannedBooklet(bookletId: String?) {
        bookletIdLD.value = bookletId
    }
}