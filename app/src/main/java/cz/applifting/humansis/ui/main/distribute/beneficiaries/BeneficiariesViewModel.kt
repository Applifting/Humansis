package cz.applifting.humansis.ui.main.distribute.beneficiaries

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.ui.BaseViewModel
import cz.applifting.humansis.ui.components.ListComponentState
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 5. 9. 2019
 */

// todo do not inject db or service, use repository pattern
class BeneficiariesViewModel @Inject constructor(private val beneficieriesRepository: BeneficieriesRepository) : BaseViewModel() {

    private val beneficiariesLD = MutableLiveData<List<BeneficiaryLocal>>()
    internal val beneficiariesViewStateLD: MutableLiveData<ListComponentState> = MutableLiveData()
    internal val statsLD: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    internal val searchResults = MediatorLiveData<List<BeneficiaryLocal>>()

    init {
        searchResults.addSource(beneficiariesLD) { list ->
            searchResults.value = list.sort()
        }
    }

    fun loadBeneficiaries(distributionId: Int, download: Boolean) {
        launch {
            beneficiariesViewStateLD.value = ListComponentState(isRefreshing = download, isRetrieving = !download)

            val beneficiaries = if (download) {
                beneficieriesRepository.getBeneficieriesOnline(distributionId)
            } else {
                beneficieriesRepository.getDistributionsOffline(distributionId)
            }

            beneficiariesLD.value = beneficiaries
            statsLD.value = Pair(beneficiaries?.count { it.distributed } ?: 0, beneficiaries?.size ?: 0)
            beneficiariesViewStateLD.value = ListComponentState()
        }
    }

    /**
     * Filters beneficiaries by provided query.
     */
    internal fun search(input: String) = beneficiariesLD.value?.let {

        val query = input.toLowerCase(Locale.getDefault())

        if (query.isEmpty()) {
            searchResults.value = it.sort()
            return@let
        }

        //todo find out what is expected behaviour for filtering and sorting
        searchResults.value = it.filter { beneficiary ->

            val familyName = beneficiary.familyName?.toLowerCase(Locale.getDefault()) ?: ""
            val givenName = beneficiary.givenName?.toLowerCase(Locale.getDefault()) ?: ""

            familyName.startsWith(query) || givenName.startsWith(query)
        }.sort()

    }

    /**
     * Sorts currently displayed beneficiaries by family name, undistributed puts first.
     */
    internal fun sortBeneficiaries() = searchResults.value?.let { list ->
        searchResults.value = list.sort()
    }

    private fun List<BeneficiaryLocal>.sort(): List<BeneficiaryLocal> {
        return this.sortedWith(compareBy({ it.distributed }, { it.familyName }))
    }

}