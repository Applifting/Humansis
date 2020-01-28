package cz.applifting.humansis.ui.main.distribute.beneficiary.confirm

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.orNullIfEmpty
import cz.applifting.humansis.model.ReferralType
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.repositories.BeneficiariesRepository
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConfirmBeneficiaryViewModel @Inject constructor(private val beneficiariesRepository: BeneficiariesRepository) :
    BaseViewModel() {

    private val beneficiaryLD = MutableLiveData<BeneficiaryLocal>()
    val referralTypeLD = MutableLiveData<ReferralType>()
    val referralNoteLD = MutableLiveData<String>()
    val errorLD = MutableLiveData<Int>()

    val referralTypes
        get() = listOf(R.string.referral_type_none)
            .plus(ReferralType.values().map { it.textId })

    fun initBeneficiary(id: Int) {
        beneficiaryLD.value ?: launch {
            beneficiaryLD.value = beneficiariesRepository.getBeneficiaryOffline(id)?.also {
                // initialize fields
                referralTypeLD.value = it.referralType
                referralNoteLD.value = it.referralNote
            }
        }
    }

    fun tryConfirm(): Boolean {
        if (validateFields()) {
            confirm()
            return true
        }
        return false
    }

    private fun validateFields(): Boolean {
        val referralType = referralTypeLD.value
        val referralNote = referralNoteLD.value
        if ((referralType == null) xor referralNote.isNullOrEmpty()) {
            // BE limitation
            errorLD.postValue(R.string.referral_validation_error_xor)
            return false
        }
        if (beneficiaryLD.value?.originalReferralType != null && referralType == null) {
            // BE limitation
            errorLD.postValue(R.string.referral_validation_error_unset)
            return false
        }
        return true
    }

    /**
     * True if this dialog is about confirming assignment of distribution.
     * Else false - the assignment is being reverted.
     */
    private val BeneficiaryLocal.isAssigning: Boolean
    get() = !distributed

    private fun confirm() {
        launch {
            val beneficiary = beneficiaryLD.value!!

            val updatedBeneficiary = beneficiary.copy(
                distributed = beneficiary.isAssigning,
                edited = beneficiary.isAssigning,
                referralType = referralTypeLD.value,
                referralNote = referralNoteLD.value.orNullIfEmpty()
            )

            beneficiariesRepository.updateBeneficiaryOffline(updatedBeneficiary)
            beneficiariesRepository.updateReferralOfMultiple(updatedBeneficiary)
            beneficiaryLD.value = updatedBeneficiary
        }
    }
}