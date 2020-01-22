package cz.applifting.humansis.ui.main.distribute.distributions

import android.content.Context
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.model.ui.DistributionItemWrapper
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.ui.main.BaseListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
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

    val distributionsLD: MutableLiveData<List<DistributionItemWrapper>> = MutableLiveData()

    private var projectId: Int? = null

    fun init(projectId: Int) {
        if (this.projectId != null) {
            return
        }
        this.projectId = projectId

        launch {
            showRetrieving(true)

            distributionsRepository
                .getDistributionsOffline(projectId)
                .map { newDistributions ->
                    newDistributions.map {
                        val reachedBeneficiaries = beneficiariesRepository.countReachedBeneficiariesOffline(it.id)
                        DistributionItemWrapper(it, reachedBeneficiaries)
                    }

                }
                .collect {
                    distributionsLD.value = it
                    showRetrieving(false, it.isNotEmpty())
                }
        }


    }
}