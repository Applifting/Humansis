package cz.applifting.humansis.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cz.applifting.humansis.model.db.VulnerabilityLocal

@Dao
interface VulnerabilitiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(provinces: List<VulnerabilityLocal>)

    @Query("DELETE FROM vulnerabilities")
    suspend fun deleteAll()

    @Query("SELECT * FROM vulnerabilities")
    suspend fun getAll(): List<VulnerabilityLocal>

}