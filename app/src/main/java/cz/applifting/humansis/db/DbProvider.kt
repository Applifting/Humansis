package cz.applifting.humansis.db

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.commonsware.cwac.saferoom.SafeHelperFactory
import javax.inject.Singleton


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 11, September, 2019
 */
const val DB_NAME = "humansis-db"

@Singleton
class DbProvider(val context: Context) {

    lateinit var db: HumansisDB

    fun init(password: ByteArray, oldPass: ByteArray? = null) {
        val factory = SafeHelperFactory(if (oldPass != null) {String(oldPass).toCharArray()} else {String(password).toCharArray()})

        if (!::db.isInitialized) {
            db = Room.databaseBuilder(
                context,
                HumansisDB::class.java, DB_NAME
            )
                .openHelperFactory(factory)
                .addMigrations(MIGRATION_13_14)
                .fallbackToDestructiveMigration()
                .build()
        }


        if (oldPass != null) {
            SafeHelperFactory.rekey(db.openHelper.readableDatabase, String(password).toCharArray())
        }
    }

    fun get(): HumansisDB {
        return db
    }

    fun isInitialized(): Boolean = ::db.isInitialized
}

private val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE 'beneficiaries' ADD COLUMN 'referralType' TEXT")
        database.execSQL("ALTER TABLE 'beneficiaries' ADD COLUMN 'referralNote' TEXT")
        database.execSQL("ALTER TABLE 'beneficiaries' ADD COLUMN 'isReferralChanged' INT NOT NULL DEFAULT 0")
    }
}
