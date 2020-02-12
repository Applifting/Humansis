package cz.applifting.humansis.ui.main.distribute.beneficiary

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.repositories.BeneficiariesRepository
import cz.applifting.humansis.ui.BaseViewModel
import cz.applifting.humansis.ui.main.distribute.beneficiary.BeneficiaryDialog.Companion.ALREADY_ASSIGNED
import cz.applifting.humansis.ui.main.distribute.beneficiary.BeneficiaryDialog.Companion.INVALID_CODE
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 9. 9. 2019
 */

class BeneficiaryViewModel @Inject constructor(private val beneficiariesRepository: BeneficiariesRepository) :
    BaseViewModel() {

    val beneficiaryLD = MutableLiveData<BeneficiaryLocal>()
    val scannedIdLD = MutableLiveData<String>()
    val goBackEventLD = MutableLiveData<Unit>()

    var previousEditState: Boolean? = null
    var isAssignedInOtherDistribution: Boolean = false
    private set

    private val BOOKLET_REGEX = "^\\d{1,6}-\\d{1,6}-\\d{1,6}$".toRegex()

    fun initBeneficiary(id: Int) {
        launch {
            beneficiariesRepository.getBeneficiaryOfflineFlow(id)
                .collect {
                    it?.let {
                        isAssignedInOtherDistribution = beneficiariesRepository.isAssignedInOtherDistribution(it)
                        beneficiaryLD.value = it
                    } ?: run {
                        goBackEventLD.value = Unit
                    }
                }
        }
    }

    fun scanQRBooklet(code: String?) {
        launch {
            val beneficiary = beneficiaryLD.value!!.copy(
                qrBooklets = listOfNotNull(code)
            )

            beneficiariesRepository.updateBeneficiaryOffline(beneficiary)
            beneficiaryLD.value = beneficiary
        }
    }

    internal fun revertBeneficiary() {
        launch {
            val beneficiary = beneficiaryLD.value!!
            val updatedBeneficiary = beneficiary.copy(
                distributed = false,
                edited = false,
                qrBooklets = emptyList(),
                referralType = beneficiary.originalReferralType,
                referralNote = beneficiary.originalReferralNote
            )

            beneficiariesRepository.updateBeneficiaryOffline(updatedBeneficiary)
            beneficiaryLD.value = updatedBeneficiary
        }
    }

    internal fun checkScannedId(scannedId: String) {
        launch {
            val assigned = beneficiariesRepository.checkBoookletAssignedLocally(scannedId)

            val bookletId = when {
                assigned -> ALREADY_ASSIGNED
                BOOKLET_REGEX.matches(scannedId) -> scannedId
                else -> INVALID_CODE
            }
            scannedIdLD.value = bookletId
        }
    }
}