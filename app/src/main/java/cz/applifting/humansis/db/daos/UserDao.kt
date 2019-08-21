package cz.applifting.humansis.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cz.applifting.humansis.model.db.User

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 21, August, 2019
 */
@Dao
interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): User?

    @Insert
    suspend fun insert(user: User)

    @Query("DELETE FROM user")
    suspend fun deleteAll()
}