package cz.applifting.humansis.db

import androidx.room.Database
import androidx.room.RoomDatabase
import cz.applifting.humansis.db.daos.UserDao
import cz.applifting.humansis.model.db.User

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 21, August, 2019
 */
@Database(entities = [User::class], version = 2, exportSchema = false)
abstract class HumansisDB: RoomDatabase() {
    abstract fun userDao(): UserDao
}