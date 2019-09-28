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
    val bookletIdLD = MutableLiveData<String>()

    init {
        bookletIdLD.value = null
    }

    internal fun markAsDistributed(isDistributed: Boolean, beneficiaryId: Int, isQRVoucher: Boolean) {
        launch {
            val beneficiary = repository.getBeneficiaryOffline(beneficiaryId)
            repository.updateBeneficiaryOffline(beneficiary.copy(distributed = isDistributed))
            distributedLD.value = isDistributed
        }
    }

    fun setScannedBooklet(bookletId: String?) {
        bookletIdLD.value = bookletId
    }
}