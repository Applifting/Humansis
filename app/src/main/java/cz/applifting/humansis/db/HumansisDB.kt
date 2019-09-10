package cz.applifting.humansis.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cz.applifting.humansis.db.converters.StringListConverter
import cz.applifting.humansis.db.converters.TargetConverter
import cz.applifting.humansis.db.daos.BeneficiaryDao
import cz.applifting.humansis.db.daos.DistributionsDao
import cz.applifting.humansis.db.daos.ProjectsDao
import cz.applifting.humansis.db.daos.UserDao
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.model.db.DistributionLocal
import cz.applifting.humansis.model.db.ProjectLocal
import cz.applifting.humansis.model.db.User

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 21, August, 2019
 */
@Database(
    entities = [User::class, BeneficiaryLocal::class, ProjectLocal::class, DistributionLocal::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(StringListConverter::class, TargetConverter::class)
abstract class HumansisDB : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun beneficiariesDao(): BeneficiaryDao
    abstract fun projectsDao(): ProjectsDao
    abstract fun distributionsDao(): DistributionsDao
}