package cz.applifting.humansis.ui.main.distribute.beneficiary

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 9. 9. 2019
 */

class BeneficiaryViewModel @Inject constructor(private val humansisDB: HumansisDB) : BaseViewModel() {

    private var beneficiaryLD = MutableLiveData<BeneficiaryLocal>()

    internal val beneficiaryViewStateLD: MutableLiveData<BeneficiaryViewState> = MutableLiveData()

    internal val distributed: Boolean
         get() = beneficiaryLD.value?.distributed ?: false

    internal fun confirm() = beneficiaryLD.value?.let {

        launch {
            val confirmedBeneficiary = it.copy(distributed = true)
            beneficiaryLD.value = confirmedBeneficiary
            humansisDB.beneficiariesDao().update(confirmedBeneficiary)
            beneficiaryViewStateLD.value = BeneficiaryViewState(distributed = true)
        }

    }

    internal fun loadBeneficiary(beneficiaryId: Int) {
        launch {
            beneficiaryViewStateLD.value = BeneficiaryViewState(true)
            val beneficiary = humansisDB.beneficiariesDao().findById(beneficiaryId)
            beneficiaryLD.value = beneficiary
            beneficiaryViewStateLD.value = BeneficiaryViewState(false, beneficiary.distributed)
        }
    }
}