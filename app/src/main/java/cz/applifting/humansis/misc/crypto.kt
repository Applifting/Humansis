package cz.applifting.humansis.misc

import android.annotation.SuppressLint
import android.content.Context
import android.security.KeyPairGeneratorSpec
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.security.*
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal
import kotlin.random.Random

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 19, August, 2019
 */

// TODO generate salt and save it securely
const val DB_SALT = "JWJDs187P0Z7g248djf0oN78eZtn1f4eaf"
const val DB_KEY_ALIAS = "HumansisDBKey"
const val DB_SP_KEY = "humansis-db"

// TODO measure these functions and check which - if not all - should be done on background thread

suspend fun saltPassword(salt: String, password: String): String {
    return withContext(Dispatchers.Default) {
        val salted = "$password{$salt}".toByteArray()
        var digest = hashSHA512(salted)

        for (i in 1..4999) {
            digest = hashSHA512(digest.plus(salted))
        }

        Base64.encodeToString(digest, Base64.NO_WRAP)
    }
}

@SuppressLint("SimpleDateFormat")
fun generateXWSSEHeader(username: String, saltedPassword: String): String {
    val nonce = generateNonce()

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val createdAt = sdf.format(Date())

    val digest = generateDigest(saltedPassword, nonce, createdAt)
    val nonce64 = Base64.encodeToString(nonce.toByteArray(), Base64.NO_WRAP)

    return "UsernameToken Username=\"$username\", PasswordDigest=\"$digest\", Nonce=\"$nonce64\", Created=\"$createdAt\""
}

fun generateDigest(saltedPassword: String, nonce: String, created: String): String {
    val mix = nonce + created + saltedPassword
    return hashSHA1(mix)
}

fun hashSHA512(input: ByteArray): ByteArray {
    return MessageDigest
        .getInstance("SHA-512")
        .digest(input)
}

fun encryptUsingKeyStoreKey(secret: ByteArray, keyAlias: String, context: Context): ByteArray {
    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)

    if (!keyStore.containsAlias(keyAlias)) {
        generateKeyStoreRSAKey(keyAlias, context)
    }

    val keyEntry = keyStore.getEntry(keyAlias, null) as KeyStore.PrivateKeyEntry

    return encrypt(keyEntry.certificate.publicKey, secret)
}

fun decryptUsingKeyStoreKey(secret: ByteArray, keyAlias: String): ByteArray {
    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)

    if (!keyStore.containsAlias(keyAlias)) {
        throw HumansisError("Key with '$keyAlias' alias has to be generated first.")
    }

    //val keyEntry = keyStore.getEntry(keyAlias, null) as KeyStore.PrivateKeyEntry

    val privateKey = keyStore.getKey(keyAlias, null) as PrivateKey

    return decrypt(privateKey, secret)
}

fun base64encode(value: ByteArray): String = Base64.encodeToString(value, Base64.NO_WRAP)

fun base64decode(value: String): ByteArray = Base64.decode(value, Base64.NO_WRAP)

private fun generateNonce(): String {
    val nonceChars = "0123456789abcdef"
    val nonce = StringBuilder()

    for (i in 0..15) {
        nonce.append(nonceChars[Random.nextInt(nonceChars.length)])
    }

    return nonce.toString()
}

private fun hashSHA1(s: String): String {
    return Base64.encodeToString(MessageDigest.getInstance("SHA-1").digest(s.toByteArray()), Base64.NO_WRAP)
}


private fun encrypt(publicKey: PublicKey, secret: ByteArray): ByteArray {
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)
    return cipher.doFinal(secret)
}

private fun decrypt(privateKey: PrivateKey, secret: ByteArray): ByteArray {
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.DECRYPT_MODE, privateKey, cipher.parameters)
    return cipher.doFinal(secret)
}

private fun generateKeyStoreRSAKey(keyAlias: String, context: Context) {
    val kpg = KeyPairGenerator.getInstance(
        "RSA", "AndroidKeyStore"
    )

    val startDate = Calendar.getInstance()
    val endDate = Calendar.getInstance()
    endDate.add(Calendar.YEAR, 30)

    // Using deprecated method to support API below 23
    val keyGeneratorSpec = KeyPairGeneratorSpec
        .Builder(context)
        .setAlias(keyAlias)
        .setSubject(X500Principal("CN=$keyAlias"))
        .setSerialNumber(BigInteger.TEN)
        .setStartDate(startDate.time)
        .setEndDate(endDate.time)
        .build()

    kpg.initialize(keyGeneratorSpec)

    val keys = kpg.genKeyPair()
}