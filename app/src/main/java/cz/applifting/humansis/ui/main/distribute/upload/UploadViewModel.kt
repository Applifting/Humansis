package cz.applifting.humansis.ui.main.distribute.upload

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.extensions.getDate
import cz.applifting.humansis.model.db.PendingChangeLocal
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.repositories.PendingChangesRepository
import cz.applifting.humansis.ui.main.BaseListViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 1. 10. 2019
 */

const val LAST_DATA_UPDATE = "LAST_UPDATE"

class UploadViewModel @Inject constructor(
    private val pendingChangesRepository: PendingChangesRepository,
    private val distributionsRepository: DistributionsRepository,
    private val beneficieriesRepository: BeneficieriesRepository,
    sp: SharedPreferences
) : BaseListViewModel() {

    val pendingChangesLD: MutableLiveData<List<PendingChangeLocal>> = MutableLiveData()

    val changesUploadedLD: MutableLiveData<Boolean> = MutableLiveData()

    val lastUpdate = sp.getDate(LAST_DATA_UPDATE)

    init {
        loadPendingChanges()
    }

    private fun loadPendingChanges() {
        launch {
            val pendingChangesLocal = pendingChangesRepository.getPendingChanges()
            pendingChangesLD.value = pendingChangesLocal
        }
    }

    internal fun uploadChanges() = pendingChangesLD.value?.let { changes ->
        launch {

            changes.forEach { change ->
                val beneficiaryLocal = beneficieriesRepository.getBeneficiaryOffline(change.beneficiaryId)
                distributionsRepository.setDistributedRelief(beneficiaryLocal.reliefs)
            }

            pendingChangesRepository.deleteAllPendingChanges()
            changesUploadedLD.value = true
        }
    }
}