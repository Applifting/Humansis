package cz.applifting.humansis.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cz.applifting.humansis.model.db.BeneficiaryLocal

@Dao
interface BeneficiaryDao {
    @Query("SELECT * FROM beneficiaries where distributionId = :distributionId")
    suspend fun getByDistribution(distributionId: Int): List<BeneficiaryLocal>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(beneficiaryLocal: List<BeneficiaryLocal>)

    @Query("DELETE FROM beneficiaries WHERE distributionId = :distributionId")
    suspend fun deleteByDistribution(distributionId: Int)

    @Query("DELETE FROM beneficiaries")
    suspend fun deleteAll()
}