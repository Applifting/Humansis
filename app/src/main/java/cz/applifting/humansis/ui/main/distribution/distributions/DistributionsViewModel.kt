package cz.applifting.humansis.ui.main.distribution.distributions

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.model.api.Distribution
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class DistributionsViewModel @Inject constructor(
    private val service: HumansisService
) : BaseViewModel() {

    val distributionsLD: MutableLiveData<List<Distribution>> = MutableLiveData()
    val distributionsViewStateLD: MutableLiveData<DistributionsViewState> = MutableLiveData()

    fun loadDistributions(projectId: Int) {
        launch {
            distributionsViewStateLD.value = DistributionsViewState(true)
            val distributions = service.getDistributions(projectId)
            distributionsLD.value = distributions
            distributionsViewStateLD.value = DistributionsViewState(false)
        }
    }
}