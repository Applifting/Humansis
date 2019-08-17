package cz.applifting.humansis.ui.login

import android.util.Log
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.ui.BaseViewModel
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject
import kotlin.experimental.and


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 17, August, 2019
 */
class LoginViewModel @Inject constructor(val mService: HumansisService): BaseViewModel() {

    fun login(username: String, password: String): Boolean {
        launch {
            try {
                val salt = mService.getSalt(username)
                //val hashedPassword = saltPassword(salt.salt, password)

                val hashedPassword = saltPassword("salt", "password")
                Log.d("asdf", hashedPassword)
            } catch (e: Exception) {
                Log.d("asdf", e.toString())
            }

        }

        return true
    }

    private fun saltPassword(salt: String, password: String): String {
        val salted = "$password{$salt}".toByteArray()
        var digest = hashSSH512(salted)
        Log.d("asdf", "salted: $salted")
        Log.d("asdf", "digest: $digest")

        for (i in 1..10) {
            val tohash = digest.plus(salted)
            Log.d("asdf", "${i}: ${tohash}")
            digest = hashSSH512(tohash)
            Log.d("asdf", "${i}: ${bytesToString(digest)}")
        }

        return bytesToString(digest)
    }

    private fun hashSSH512(input: ByteArray): ByteArray {
        val bytes = MessageDigest
            .getInstance("SHA-512")
            .digest(input)

        return bytes
    }

    private fun bytesToString(bytes: ByteArray): String {
        val HEX_CHARS = "0123456789abcdef"
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()
    }

    private fun stringToHex(input: String): String {
        val bytes = input.toByteArray()
        val hexArray = "0123456789abcdef".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = (bytes[j] and 0xFF.toByte()).toInt()
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

}