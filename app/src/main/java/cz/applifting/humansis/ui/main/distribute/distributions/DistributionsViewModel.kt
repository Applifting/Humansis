package cz.applifting.humansis.ui.main.distribute.distributions

import android.content.Context
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.model.ui.DistributionModel
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.ui.main.BaseListViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class DistributionsViewModel @Inject constructor(
    private val distributionsRepository: DistributionsRepository,
    private val beneficiariesRepository: BeneficieriesRepository,
    context: Context
) : BaseListViewModel(context) {

    val distributionsLD: MutableLiveData<List<DistributionModel>> = MutableLiveData()

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

            val distributionsModel = distributions?.map {

                // todo maybe count on db layer
                val reachedBeneficiaries = beneficiariesRepository.countReachedBeneficiariesOffline(it.id)
                val distributionModel = DistributionModel(
                    it.id,
                    it.name,
                    it.numberOfBeneficiaries,
                    it.commodities,
                    it.dateOfDistribution,
                    it.projectId,
                    it.target,
                    it.completed,
                    reachedBeneficiaries
                )

                distributionModel
            }

            distributionsLD.value = distributionsModel
            finishLoading(distributions)
        }
    }
}