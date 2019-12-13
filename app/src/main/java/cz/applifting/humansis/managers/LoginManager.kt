package cz.applifting.humansis.managers

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.commonsware.cwac.saferoom.SafeHelperFactory
import cz.applifting.humansis.R
import cz.applifting.humansis.db.DbProvider
import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.extensions.suspendCommit
import cz.applifting.humansis.misc.*
import cz.applifting.humansis.model.api.LoginReqRes
import cz.applifting.humansis.model.db.User
import cz.applifting.humansis.ui.main.LAST_DOWNLOAD_KEY
import kotlinx.coroutines.supervisorScope
import net.sqlcipher.database.SQLiteException
import javax.inject.Inject


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 21, August, 2019
 *
 * This class is something like a repository, but it is not, as it is required by API service and can't depend on it
 */
const val SP_DB_PASS_KEY = "humansis-db"
const val SP_SALT_KEY = "humansis-db-pass-salt"
const val KEYSTORE_KEY_ALIAS = "HumansisDBKey"
const val SP_COUNTRY = "country"

class LoginManager @Inject constructor(private val dbProvider: DbProvider, private val sp: SharedPreferences, private val context: Context) {

    val db: HumansisDB by lazy { dbProvider.get() }

    @SuppressLint("CommitPrefEdits")
    suspend fun login(userResponse: LoginReqRes, originalPass: ByteArray): User {
        // Initialize db and save the DB password in shared prefs
        // The hashing of pass might be unnecessary, but why not. I am passing it to 3-rd part lib.
        val dbPass = hashSHA512(originalPass.plus(retrieveOrInitDbSalt().toByteArray()), 1000)
        val defaultCountry = userResponse.projects?.firstOrNull()?.iso3 ?: "SYR"

        if (retrieveUser()?.invalidPassword == true) {
            // This case handles token expiration on backend. DB is decrypted with the old pass, but is rekyed using the new one.
            val oldEncryptedPassword = sp.getString(SP_DB_PASS_KEY, null) ?: throw IllegalStateException("DB password lost")
            val oldDecryptedPassword = decryptUsingKeyStoreKey(base64decode(oldEncryptedPassword), KEYSTORE_KEY_ALIAS, context)
                ?: throw IllegalStateException("DB password couldn't be decrypted")

            dbProvider.init(dbPass, oldDecryptedPassword)
        } else {
            dbProvider.init(dbPass, "default".toByteArray())
        }

        with(sp.edit()) {
            // Note that encryptUsingKeyStoreKey generates and stores IV to shared prefs
            val encryptedDbPass = base64encode(encryptUsingKeyStoreKey(dbPass, KEYSTORE_KEY_ALIAS, context))
            putString(SP_DB_PASS_KEY, encryptedDbPass)
            putString(SP_COUNTRY, defaultCountry)
            suspendCommit()
        }


        val db = dbProvider.get()

        val id = userResponse.id?.toInt() ?: throw HumansisError(context.getString(R.string.error_missing_user_id))
        val user = User(id, userResponse.username, userResponse.email, userResponse.password)
        db.userDao().insert(user)

        return user
    }

    suspend fun logout() {
        // TODO clear all other data
        db.apply {
            clearAllTables()
        }

        sp.edit().putString(LAST_DOWNLOAD_KEY, null).suspendCommit()
        sp.edit().putString(SP_DB_PASS_KEY, null).suspendCommit()

        encryptDefault()
    }

    suspend fun markInvalidPassword() {
        val user = db.userDao().getUser()
        if (user != null) {
            db.userDao().update(user.copy(invalidPassword = true))
        }
    }

    // Initializes DB if the key is available. Otherwise returns false.
    fun tryInitDB(): Boolean {
        if (dbProvider.isInitialized()) { return true }
        val encryptedPassword = sp.getString(SP_DB_PASS_KEY, null) ?: return false
        val decryptedPassword = decryptUsingKeyStoreKey(base64decode(encryptedPassword), KEYSTORE_KEY_ALIAS, context) ?: return false

        dbProvider.init(decryptedPassword)

        return true
    }

    suspend fun retrieveUser(): User? {
        return supervisorScope {
            try {
                val db = dbProvider.get()
                db.userDao().getUser()
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun getAuthHeader(): String? {
        if (!dbProvider.isInitialized()) return null

        val user = retrieveUser()
        return user?.let {
            generateXWSSEHeader(user.username, user.saltedPassword ?: "", sp.getBoolean("test", false))
        }
    }

    private fun encryptDefault() {
        if (dbProvider.isInitialized()) {
            try {
                SafeHelperFactory.rekey(db.openHelper.readableDatabase, "default".toCharArray())
            } catch (e: SQLiteException) {
                Log.d("humansis", e.toString())
            }
        }
    }

    private suspend fun retrieveOrInitDbSalt(): String {
        var salt = sp.getString(SP_SALT_KEY, null)

        if (salt == null) {
            salt = generateNonce()
            sp.edit()
                .putString(SP_SALT_KEY, salt)
                .suspendCommit()
        }

        return salt
    }
}