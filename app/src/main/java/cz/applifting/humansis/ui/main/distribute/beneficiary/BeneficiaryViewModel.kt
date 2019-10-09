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

    fun loadBeneficiary(id: Int) {
        launch {
            val beneficiary = beneficieriesRepository
                .getBeneficiaryOffline(id)
            beneficiary.currentViewing = true

            beneficiaryLD.value = beneficiary
        }
    }

    fun scanQRBooklet(code: String?) {
        val beneficiary = beneficiaryLD.value?.copy(
            qrBooklets = if (code != null) listOf(code) else listOfNotNull()
        ) ?: throw IllegalStateException()
        beneficiary.currentViewing = true

        beneficiaryLD.value = beneficiary
    }

    internal fun editBeneficiary() {
        launch {
            val beneficiary = beneficiaryLD.value ?: throw IllegalStateException("Beneficiary was not loaded")

            val updatedBeneficiary = beneficiary.copy(
                distributed = !beneficiary.distributed,
                edited = !beneficiary.distributed,
                qrBooklets = if (beneficiary.distributed) mutableListOf() else beneficiary.qrBooklets
            )

            updatedBeneficiary.currentViewing = false

            beneficieriesRepository.updateBeneficiaryOffline(updatedBeneficiary)
            beneficiaryLD.value = updatedBeneficiary
        }
    }
}