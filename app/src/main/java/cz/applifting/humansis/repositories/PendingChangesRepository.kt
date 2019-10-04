package cz.applifting.humansis.repositories

import cz.applifting.humansis.db.DbProvider
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.model.db.PendingChangeLocal
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 1. 10. 2019
 */
@Singleton
class PendingChangesRepository @Inject constructor(val dbProvider: DbProvider) {

    val db: HumansisDB by lazy { dbProvider.get() }

    suspend fun getPendingChanges(): List<PendingChangeLocal>? {
        return db.pendingChangesDao().getAll()
    }

    suspend fun createPendingChange(beneficiaryId: Int) {
        val pendingChangeLocal = PendingChangeLocal(beneficiaryId = beneficiaryId,  date = Date())
        return db.pendingChangesDao().insert(pendingChangeLocal)
    }

    suspend fun deletePendingChange(pendingChangeId: Int) {
        return db.pendingChangesDao().deleteById(pendingChangeId)
    }

    suspend fun deletePendingChangeByBeneficiaryId(beneficiaryId: Int) {
        return db.pendingChangesDao().deleteByBeneficiaryId(beneficiaryId)
    }

    suspend fun deleteAllPendingChanges() {
        return db.pendingChangesDao().deleteAll()
    }

    suspend fun hasPendingChanges(): Boolean {
        return db.pendingChangesDao().countPendingChanges() > 0
    }

}