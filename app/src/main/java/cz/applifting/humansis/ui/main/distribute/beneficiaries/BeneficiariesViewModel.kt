package cz.applifting.humansis.ui.main.distribute.beneficiaries

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 5. 9. 2019
 */

// todo do not inject db or service, use repository pattern
class BeneficiariesViewModel @Inject constructor(private val humansisDB: HumansisDB) :
    BaseViewModel() {

    private val beneficiariesLD = MutableLiveData<List<BeneficiaryLocal>>()
    internal val beneficiariesViewStateLD: MutableLiveData<BeneficiariesViewState> =
        MutableLiveData()

    internal val statsLD: MutableLiveData<Pair<Int, Int>> = MutableLiveData()

    internal val searchResults = MediatorLiveData<List<BeneficiaryLocal>>()

    init {
        searchResults.addSource(beneficiariesLD) {
            searchResults.value = it.sortedBy { beneficiary -> beneficiary.familyName }
        }
    }

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

    /**
     * Filters beneficiaries by provided query.
     */
    internal fun search(input: String) = beneficiariesLD.value?.let {

        val query = input.toLowerCase(Locale.getDefault())

        searchResults.value = it.filter { beneficiary ->

            val familyName = beneficiary.familyName?.toLowerCase(Locale.getDefault()) ?: ""
            val givenName = beneficiary.givenName?.toLowerCase(Locale.getDefault()) ?: ""

            familyName.startsWith(query) || givenName.startsWith(query)
        }

    }

    /**
     * Sorts currently displayed beneficiaries by family name.
     */
    internal fun sortBeneficiaries() = searchResults.value?.let {
        searchResults.value = it.sortedBy { beneficiary -> beneficiary.familyName }
    }

}