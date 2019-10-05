package cz.applifting.humansis.ui.main.distribute.beneficiary

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 9. 9. 2019
 */

class BeneficiaryViewModel @Inject constructor(private val beneficieriesRepository: BeneficieriesRepository) :
    BaseViewModel() {

    val beneficiaryLD = MutableLiveData<BeneficiaryLocal>()
    var distributed: Boolean? = null


    fun loadBeneficiary(id: Int) {
        launch {
            beneficiaryLD.value = beneficieriesRepository.getBeneficiaryOffline(id)
        }
    }

    internal fun editBeneficiary(isDistributed: Boolean, beneficiaryId: Int, qrBooklet: String?, rescan: Boolean = false) {
        launch {
            val beneficiary = beneficieriesRepository.getBeneficiaryOffline(beneficiaryId)

            val updatedBeneficiary = if (isDistributed) {
                confirm(beneficiary, qrBooklet)
            } else if (!isDistributed && qrBooklet != null) {
                if (beneficiary.edited) {
                    revert(beneficiary)
                } else {
                    scanQrBooklet(beneficiary, qrBooklet, rescan)
                }
            } else {
                if (rescan) {
                    scanQrBooklet(beneficiary, qrBooklet, rescan)
                } else {
                    revert(beneficiary)
                }
            }

            beneficiaryLD.value = updatedBeneficiary
        }
    }

    private suspend fun confirm(beneficiary: BeneficiaryLocal, qrBooklet: String?): BeneficiaryLocal {
        val updatedBeneficiary = beneficiary.copy(distributed = true, edited = true, qrBooklets = if (qrBooklet == null) mutableListOf() else listOfNotNull(qrBooklet))
        beneficieriesRepository.updateBeneficiaryOffline(updatedBeneficiary)
        distributed = true
        return updatedBeneficiary
    }

    private suspend fun revert(beneficiary: BeneficiaryLocal): BeneficiaryLocal {
        val updatedBeneficiary = beneficiary.copy(distributed = false, edited = false, qrBooklets = mutableListOf())
        beneficieriesRepository.updateBeneficiaryOffline(updatedBeneficiary)
        distributed = false
        return updatedBeneficiary
    }

    private suspend fun scanQrBooklet(beneficiary: BeneficiaryLocal, qrBooklet: String?, rescan: Boolean): BeneficiaryLocal {
        val updatedBeneficiary = beneficiary.copy(distributed = false, edited = !rescan, qrBooklets = if (rescan) mutableListOf() else listOfNotNull(qrBooklet))
        beneficieriesRepository.updateBeneficiaryOffline(updatedBeneficiary)
        return updatedBeneficiary
    }
}