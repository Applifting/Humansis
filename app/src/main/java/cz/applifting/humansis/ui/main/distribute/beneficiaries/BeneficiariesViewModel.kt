package cz.applifting.humansis.ui.main.distribute.beneficiaries

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.ui.components.listComponent.ListComponentState
import cz.applifting.humansis.ui.main.BaseListViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 5. 9. 2019
 */
class BeneficiariesViewModel @Inject constructor(
    private val beneficieriesRepository: BeneficieriesRepository,
    context: Context
) : BaseListViewModel(context) {

    private val beneficiariesLD = MutableLiveData<List<BeneficiaryLocal>>()
    internal val beneficiariesViewStateLD: MutableLiveData<ListComponentState> = MutableLiveData()
    internal val statsLD: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    internal val searchResultsLD = MediatorLiveData<List<BeneficiaryLocal>>()

    init {
        searchResultsLD.addSource(beneficiariesLD) { list ->
            searchResultsLD.value = list?.sort()
        }
    }

    fun loadBeneficiaries(distributionId: Int, download: Boolean = false) {
        launch {
            if (download) {
                showRefreshing()
            } else {
                showRetrieving()
            }

            val beneficiaries = if (download) {
                beneficieriesRepository.getBeneficieriesOnline(distributionId)
            } else {
                beneficieriesRepository.getBeneficieriesOffline(distributionId)
            }
            beneficiariesLD.value = beneficiaries
            statsLD.value = Pair(beneficiaries?.count { it.distributed } ?: 0, beneficiaries?.size ?: 0)
            finishLoading(beneficiaries)
        }
    }

    /**
     * Filters beneficiaries by provided query.
     */
    internal fun search(input: String) = beneficiariesLD.value?.let {

        val query = input.toLowerCase(Locale.getDefault())

        if (query.isEmpty()) {
            searchResultsLD.value = it.sort()
            return@let
        }

        //todo find out what is expected behaviour for filtering and sorting
        searchResultsLD.value = it.filter { beneficiary ->

            val familyName = beneficiary.familyName?.toLowerCase(Locale.getDefault()) ?: ""
            val givenName = beneficiary.givenName?.toLowerCase(Locale.getDefault()) ?: ""
            val beneficiaryId = beneficiary.beneficiaryId.toString()
            val id = beneficiary.id.toString()

            val matched = mutableListOf<Boolean>()
            val splitQuery = query.split(" ")

            splitQuery.forEach { subQuery ->
                matched.add(familyName.startsWith(subQuery) || givenName.startsWith(subQuery) || id == subQuery || beneficiaryId == subQuery)
            }

            matched.contains(true)

        }.sort()

    }

    /**
     * Sorts currently displayed beneficiaries by family name, undistributed puts first.
     */
    internal fun sortBeneficiaries() = searchResultsLD.value?.let { list ->
        searchResultsLD.value = list.sort()
    }

    private fun List<BeneficiaryLocal>.sort(): List<BeneficiaryLocal> {
        return this.sortedWith(compareBy({ it.distributed }, { it.familyName }))
    }

}