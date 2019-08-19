package cz.applifting.humansis.misc

import android.annotation.SuppressLint
import android.util.Base64
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 19, August, 2019
 */

fun saltPassword(salt: String, password: String): String {
    val salted = "$password{$salt}".toByteArray()
    var digest = hashSHA512(salted)

    for (i in 1..4999) {
        digest = hashSHA512(digest.plus(salted))
    }

    return Base64.encodeToString(digest, Base64.NO_WRAP)
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

private fun hashSHA512(input: ByteArray): ByteArray {
    return MessageDigest
        .getInstance("SHA-512")
        .digest(input)
}