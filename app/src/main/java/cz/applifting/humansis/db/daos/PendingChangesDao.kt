package cz.applifting.humansis.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cz.applifting.humansis.model.db.PendingChangeLocal

@Dao
interface PendingChangesDao {
    @Query("SELECT * FROM pending_changes")
    suspend fun getAll(): List<PendingChangeLocal>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pendingChange: PendingChangeLocal)

    @Query("DELETE FROM pending_changes WHERE id = :pendingChangeId")
    suspend fun deleteById(pendingChangeId: Int)

    @Query("DELETE FROM pending_changes")
    suspend fun deleteAll()

    @Query("SELECT COUNT(id) FROM pending_changes")
    suspend fun countPendingChanges(): Int
}