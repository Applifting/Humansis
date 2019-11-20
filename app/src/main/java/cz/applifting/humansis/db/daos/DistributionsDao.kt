package cz.applifting.humansis.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cz.applifting.humansis.model.db.DistributionLocal
import kotlinx.coroutines.flow.Flow

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 09, September, 2019
 */
@Dao
interface DistributionsDao {
    @Query("SELECT * FROM distributions WHERE projectId = :projectId")
    fun getByProject(projectId: Int): Flow<List<DistributionLocal>>

    @Query("SELECT * FROM distributions WHERE projectId = :projectId")
    suspend fun getByProjectSuspend(projectId: Int): List<DistributionLocal>

    @Query("SELECT * FROM distributions WHERE id = :distributionId")
    suspend fun getById(distributionId: Int): DistributionLocal?

    @Query("DELETE FROM distributions WHERE projectId = :projectId")
    suspend fun deleteByProject(projectId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(distributions: List<DistributionLocal>)

    @Query("DELETE FROM distributions")
    suspend fun deleteAll()

    @Query("SELECT * FROM distributions WHERE projectId = :projectId AND completed = 0")
    suspend fun findUncompletedDistributions(projectId: Int): List<DistributionLocal>?
}