package cz.applifting.humansis.ui.main.distribute.beneficiary

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 9. 9. 2019
 */

class BeneficiaryViewModel @Inject constructor(private val repository: BeneficieriesRepository) : BaseViewModel() {

    val distributedLD = MutableLiveData<Boolean>()
    val qrBookletIdLD = MutableLiveData<String>()

    init {
        qrBookletIdLD.value = null
    }

    internal fun editBeneficiary(isDistributed: Boolean, beneficiaryId: Int) {
        launch {
            val beneficiary = repository.getBeneficiaryOffline(beneficiaryId)
            repository.updateBeneficiaryOffline(beneficiary.copy(distributed = isDistributed, QRBookletCode = qrBookletIdLD.value, edited = true))
            distributedLD.value = isDistributed
        }
    }

    fun setScannedBooklet(bookletId: String?) {
        qrBookletIdLD.value = bookletId
    }
}