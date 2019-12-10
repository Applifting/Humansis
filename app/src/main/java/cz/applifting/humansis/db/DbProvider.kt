package cz.applifting.humansis.db

import android.content.Context
import androidx.room.Room
import com.commonsware.cwac.saferoom.SafeHelperFactory


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 11, September, 2019
 */
const val DB_NAME = "humansis-db"

class DbProvider(val context: Context) {

    var db: HumansisDB? = null

    fun init(password: ByteArray, oldPass: ByteArray? = null) {
        val factory = SafeHelperFactory(if (oldPass != null) {String(oldPass).toCharArray()} else {String(password).toCharArray()})

        db = Room.databaseBuilder(
            context,
            HumansisDB::class.java, DB_NAME
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()

        if (oldPass != null) {
            SafeHelperFactory.rekey(db?.openHelper?.readableDatabase, String(password).toCharArray())
        }
    }

    fun get(): HumansisDB {
        return db ?: throw IllegalStateException("Db not initialized")
    }

    fun reset() {
        db = null
    }

    fun encryptDefault() {
        SafeHelperFactory.rekey(db?.openHelper?.readableDatabase, "default".toCharArray())
    }

    fun isInitialized(): Boolean = db != null
}