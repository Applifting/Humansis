package cz.applifting.humansis.ui.main.distribute.beneficiaries

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 5. 9. 2019
 */

// todo do not inject db or service, use repository pattern
class BeneficiariesViewModel @Inject constructor(private val humansisDB: HumansisDB) :
    BaseViewModel() {
    internal val beneficiariesLD = MutableLiveData<List<BeneficiaryLocal>>()
    internal val beneficiariesViewStateLD: MutableLiveData<BeneficiariesViewState> =
        MutableLiveData()

    internal val statsLD: MutableLiveData<Pair<Int, Int>> = MutableLiveData()

    fun loadBeneficiaries(distributionId: Int) {
        launch {
            beneficiariesViewStateLD.value = BeneficiariesViewState(true)
            val beneficiaries =
                humansisDB.beneficiaryDao().getDistributionBeneficiaries(distributionId)
            beneficiariesLD.value = beneficiaries
            statsLD.value =
                Pair(beneficiaries?.count { it.distributed } ?: 0, beneficiaries?.size ?: 0)
            beneficiariesViewStateLD.value = BeneficiariesViewState(false)
        }
    }

}