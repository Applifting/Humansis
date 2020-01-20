package cz.applifting.humansis.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cz.applifting.humansis.model.db.PendingChangeLocal

// TODO remove
@Deprecated("not used")
@Dao
interface PendingChangesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pendingChange: PendingChangeLocal)

    @Query("DELETE FROM pending_changes WHERE id = :pendingChangeId")
    suspend fun deleteById(pendingChangeId: Int)

    @Query("DELETE FROM pending_changes WHERE beneficiaryId = :beneficiaryId")
    suspend fun deleteByBeneficiaryId(beneficiaryId: Int)

    @Query("DELETE FROM pending_changes")
    suspend fun deleteAll()

    @Query("SELECT COUNT(id) FROM pending_changes")
    suspend fun countPendingChanges(): Int
}