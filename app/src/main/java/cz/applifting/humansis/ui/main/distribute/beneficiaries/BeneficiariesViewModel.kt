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

    enum class Sort {
        DEFAULT,
        AZ,
        ZA
    }

    private val beneficiariesLD = MutableLiveData<List<BeneficiaryLocal>>()
    internal val beneficiariesViewStateLD: MutableLiveData<ListComponentState> = MutableLiveData()
    internal val statsLD: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    internal val searchResultsLD = MediatorLiveData<List<BeneficiaryLocal>>()
    internal val currentSort = MutableLiveData<Sort>()
    private var searchText: String? = null

    init {
        currentSort.value = Sort.DEFAULT
        searchResultsLD.addSource(beneficiariesLD) { list ->
            searchResultsLD.value = list?.defaultSort()
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

            searchText?.let {
                if (it.isNotEmpty()) {
                    search(it)
                }
            }
        }
    }

    /**
     * Filters beneficiaries by provided query.
     */
    internal fun search(input: String) = beneficiariesLD.value?.let {
        searchText = input
        val query = input.toLowerCase(Locale.getDefault())

        if (query.isEmpty()) {
            searchResultsLD.value = it.defaultSort()
            return@let
        }

        searchResultsLD.value = it.filter { beneficiary ->

            val familyName = beneficiary.familyName?.toLowerCase(Locale.getDefault()) ?: ""
            val givenName = beneficiary.givenName?.toLowerCase(Locale.getDefault()) ?: ""
            val beneficiaryId = beneficiary.beneficiaryId.toString()
            val id = beneficiary.id.toString()

            val matched = mutableListOf<Boolean>()
            val splitQuery = query.split(" ").filter { subQuery -> subQuery.isNotEmpty() }

            splitQuery.forEach { subQuery ->
                matched.add(familyName.startsWith(subQuery) || givenName.startsWith(subQuery) || id == subQuery || beneficiaryId == subQuery)
            }

            matched.contains(true)

        }.defaultSort()

    }

    internal fun sortBeneficiaries() = searchResultsLD.value?.let { list ->
        currentSort.value = nextSort()
        searchResultsLD.value = list.run {
            when (currentSort.value) {
                Sort.DEFAULT -> defaultSort()
                Sort.AZ -> sortAZ()
                Sort.ZA -> sortZA()
                else -> defaultSort()
            }
        }
    }

    /**
     * Sorts currently displayed beneficiaries by family name, undistributed puts first.
     */
    private fun List<BeneficiaryLocal>.defaultSort(): List<BeneficiaryLocal> {
        return this.sortedWith(compareBy({ it.distributed }, { it.familyName }))
    }

    /**
     * Sorts currently displayed beneficiaries by family name A to Z
     */
    private fun List<BeneficiaryLocal>.sortAZ(): List<BeneficiaryLocal> {
        return this.sortedWith(compareBy { it.familyName })
    }

    /**
     * Sorts currently displayed beneficiaries by family name Z to A
     */
    private fun List<BeneficiaryLocal>.sortZA(): List<BeneficiaryLocal> {
        return this.sortedWith(compareBy { it.familyName }).reversed()
    }

    private fun nextSort(): Sort {
        return when (currentSort.value) {
            Sort.DEFAULT -> Sort.AZ
            Sort.AZ -> Sort.ZA
            Sort.ZA -> Sort.AZ
            else -> Sort.DEFAULT
        }
    }

}