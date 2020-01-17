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
    val error = MutableLiveData<String>()

    val referralTypes
        get() = listOf(R.string.referral_type_none)
            .plus(ReferralType.values().map { it.textId })

    fun loadBeneficiary(id: Int) {
        launch {
            beneficiaryLD.value = beneficieriesRepository.getBeneficiaryOffline(id)
        }
    }

    internal fun editBeneficiary() {
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