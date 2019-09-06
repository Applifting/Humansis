package cz.applifting.humansis.ui.main.distribute.beneficiaries

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.ui.BaseViewModel
import cz.applifting.humansis.ui.main.distribute.distributions.DistributionsViewState
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
    internal val beneficiariesViewStateLD: MutableLiveData<BeneficiariesViewState> = MutableLiveData()

    fun loadBeneficiaries(distributionId: Int) {
        launch {
            beneficiariesViewStateLD.value = BeneficiariesViewState(true)
            val distributions = humansisDB.beneficiaryDao().getDistributionBeneficiaries(distributionId)
            beneficiariesLD.value = distributions
            beneficiariesViewStateLD.value = BeneficiariesViewState(false)
        }
    }

}