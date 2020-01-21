package cz.applifting.humansis.ui.main.distribute.beneficiary.confirm

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.R
import cz.applifting.humansis.model.ReferralType
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConfirmBeneficiaryViewModel @Inject constructor(private val beneficiariesRepository: BeneficieriesRepository) :
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
            beneficiaryLD.value = beneficiariesRepository.getBeneficiaryOffline(id).also {
                // initialize fields
                if (referralTypeLD.value == null) {
                    referralTypeLD.value = it.referralType
                }
                if (referralNoteLD.value == null) {
                    referralNoteLD.value = it.referralNote
                }
            }
        }
    }

    fun tryConfirmDistribution(): Boolean {
        if (validateFields()) {
            confirmDistribution()
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
        if (beneficiaryLD.value?.referralType != null && referralType == null) {
            // BE limitation
            errorLD.postValue(R.string.referral_validation_error_unset)
            return false
        }
        return true
    }

    private fun confirmDistribution() {
        launch {
            val beneficiary = beneficiaryLD.value!!

            val updatedBeneficiary = beneficiary.copy(
                distributed = true,
                edited = true,
                referralType = referralTypeLD.value,
                referralNote = referralNoteLD.value,
                isReferralChanged = beneficiary.referralType != referralTypeLD.value || beneficiary.referralNote != referralNoteLD.value
            )

            beneficiariesRepository.updateBeneficiaryOffline(updatedBeneficiary)
            beneficiariesRepository.updateReferralOfMultiple(updatedBeneficiary)
            beneficiaryLD.value = updatedBeneficiary
        }
    }
}