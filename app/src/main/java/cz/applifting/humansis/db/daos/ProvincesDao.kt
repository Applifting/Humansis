package cz.applifting.humansis.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cz.applifting.humansis.model.db.ProvinceLocal

@Dao
interface ProvincesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(provinces: List<ProvinceLocal>)

    @Query("DELETE FROM provinces")
    suspend fun deleteAll()

    @Query("SELECT * FROM provinces")
    suspend fun getAll(): List<ProvinceLocal>

}