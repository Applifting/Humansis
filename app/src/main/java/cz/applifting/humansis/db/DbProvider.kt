package cz.applifting.humansis.db

import android.content.Context
import androidx.room.Room
import com.commonsware.cwac.saferoom.SafeHelperFactory
import cz.applifting.humansis.R
import cz.applifting.humansis.misc.HumansisError

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 11, September, 2019
 */
class DbProvider(val context: Context) {

    private lateinit var db: HumansisDB

    fun init(password: ByteArray) {
        val factory = SafeHelperFactory(password)

        db = Room.databaseBuilder(
            context,
            HumansisDB::class.java, "humansis-db"
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }

    fun get(): HumansisDB {
        if (!::db.isInitialized) throw HumansisError(context.getString(R.string.error_db_not_initialized))
        return db
    }

    fun isInitialized(): Boolean = ::db.isInitialized
}