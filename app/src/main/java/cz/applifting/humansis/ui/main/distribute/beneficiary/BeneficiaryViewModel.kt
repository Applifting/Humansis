package cz.applifting.humansis.ui.main.distribute.beneficiary

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.repositories.BeneficieriesRepository
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

class BeneficiaryViewModel @Inject constructor(private val beneficieriesRepository: BeneficieriesRepository) :
    BaseViewModel() {

    val beneficiaryLD = MutableLiveData<BeneficiaryLocal>()
    val scannedIdLD = MutableLiveData<String>()

    var previousEditState: Boolean? = null

    private val BOOKLET_REGEX = "^\\d{1,6}-\\d{1,6}-\\d{1,6}$".toRegex()

    fun initBeneficiary(id: Int) {
        launch {
            beneficieriesRepository.getBeneficiaryOfflineFlow(id)
                .collect {
                    beneficiaryLD.value = it
                }
        }
    }

    fun scanQRBooklet(code: String?) {
        launch {
            val beneficiary = beneficiaryLD.value!!.copy(
                qrBooklets = listOfNotNull(code)
            )

            beneficieriesRepository.updateBeneficiaryOffline(beneficiary)
            beneficiaryLD.value = beneficiary
        }
    }

    internal fun revertBeneficiary() {
        launch {
            val beneficiary = beneficiaryLD.value!!
            val updatedBeneficiary = beneficiary.copy(
                distributed = false,
                edited = false,
                qrBooklets = emptyList()
            )
            // TODO revert also referral?
            // would need to remember original referral

            beneficieriesRepository.updateBeneficiaryOffline(updatedBeneficiary)
            beneficiaryLD.value = updatedBeneficiary
        }
    }

    internal fun checkScannedId(scannedId: String) {
        launch {
            val assigned = beneficieriesRepository.checkBoookletAssignedLocally(scannedId)

            val bookletId = when {
                assigned -> ALREADY_ASSIGNED
                BOOKLET_REGEX.matches(scannedId) -> scannedId
                else -> INVALID_CODE
            }
            scannedIdLD.value = bookletId
        }
    }
}