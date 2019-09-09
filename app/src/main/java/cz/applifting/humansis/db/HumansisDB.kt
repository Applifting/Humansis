package cz.applifting.humansis.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cz.applifting.humansis.db.converters.StringListConverter
import cz.applifting.humansis.db.daos.BeneficiaryDao
import cz.applifting.humansis.db.daos.UserDao
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.model.db.User

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 21, August, 2019
 */
@Database(
    entities = [User::class, BeneficiaryLocal::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class HumansisDB : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun beneficiaryDao(): BeneficiaryDao
}