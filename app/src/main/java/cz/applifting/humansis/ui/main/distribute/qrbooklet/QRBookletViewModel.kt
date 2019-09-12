package cz.applifting.humansis.ui.main.distribute.qrbooklet

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

class QRBookletViewModel @Inject constructor(private val repository: BeneficieriesRepository) :
    BaseViewModel() {

    private var beneficiaryLD = MutableLiveData<BeneficiaryLocal>()

    internal val distributedLD = MutableLiveData<Boolean>()

    internal val refreshingLD = MutableLiveData<Boolean>()

    internal fun confirm() = beneficiaryLD.value?.let {

        launch {
            val confirmedBeneficiary = it.copy(distributed = true)
            beneficiaryLD.value = confirmedBeneficiary
            repository.updateBeneficiaryOffline(confirmedBeneficiary)
            distributedLD.value = true
        }

    }

    internal fun loadBeneficiary(beneficiaryId: Int) {
        launch {
            refreshingLD.value = true
            val beneficiary = repository.getBeneficiaryOffline(beneficiaryId)
            beneficiaryLD.value = beneficiary
            distributedLD.value = beneficiary.distributed
            refreshingLD.value = false
        }
    }
}