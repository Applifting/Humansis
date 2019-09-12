package cz.applifting.humansis.ui.main.distribute.distributions

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.model.db.DistributionLocal
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.ui.main.BaseListViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class DistributionsViewModel @Inject constructor(
    private val distributionsRepository: DistributionsRepository
) : BaseListViewModel() {

    val distributionsLD: MutableLiveData<List<DistributionLocal>> = MutableLiveData()

    fun loadDistributions(projectId: Int, download: Boolean = false) {
        launch {
            if (download) {
                showRefreshing()
            } else {
                showRetrieving()
            }

            val distributions = if (download) {
                distributionsRepository.getDistributionsOnline(projectId)
            } else {
                distributionsRepository.getDistributionsOffline(projectId)
            }

            distributionsLD.value = distributions
            finishLoading(distributions)
        }
    }
}