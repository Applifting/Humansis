package cz.applifting.humansis.ui.main.distribute.beneficiary.confirm

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.R
import cz.applifting.humansis.model.ReferralType
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConfirmBeneficiaryViewModel @Inject constructor(private val beneficieriesRepository: BeneficieriesRepository) :
    BaseViewModel() {

    val beneficiaryLD = MutableLiveData<BeneficiaryLocal>()
    val referralType = MutableLiveData<ReferralType>()
    val referralNote = MutableLiveData<String>()
    val error = MutableLiveData<Int>()

    val referralTypes
        get() = listOf(R.string.referral_type_none)
            .plus(ReferralType.values().map { it.textId })

    fun loadBeneficiary(id: Int) {
        launch {
            beneficiaryLD.value = beneficieriesRepository.getBeneficiaryOffline(id)
        }
    }

    fun tryEditBeneficiary(): Boolean {
        if (validateFields()) {
            editBeneficiary()
            return true
        }
        return false
    }

    private fun validateFields(): Boolean {
        val referralType = referralType.value
        val referralNote = referralNote.value
        if ((referralType == null) xor referralNote.isNullOrEmpty()) {
            error.postValue(R.string.referral_validation_error_xor)
            return false
        }
        beneficiaryLD.value?.let {
            if (referralType == null) {
                error.postValue(R.string.referral_validation_error_unset)
                return false
            }
        }
        return true
    }

    private fun editBeneficiary() {
        launch {
            val beneficiary = beneficiaryLD.value ?: throw IllegalStateException("Beneficiary was not loaded")

            val updatedBeneficiary = beneficiary.copy(
                distributed = !beneficiary.distributed,
                edited = !beneficiary.distributed,
                qrBooklets = if (beneficiary.distributed) mutableListOf() else beneficiary.qrBooklets,
                referralType = referralType.value,
                referralNote = referralNote.value,
                isReferralChanged = beneficiary.referralType != referralType.value || beneficiary.referralNote != referralNote.value
            )

            beneficieriesRepository.updateBeneficiaryOffline(updatedBeneficiary)
            beneficiaryLD.value = updatedBeneficiary
        }
    }
}