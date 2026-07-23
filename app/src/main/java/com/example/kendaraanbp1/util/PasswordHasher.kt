package com.example.kendaraanbp1.util

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Salted, iterated SHA-256 password hashing. Uses only java.security +
 * android.util.Base64 so it works on the app's full minSdk range (24+),
 * unlike PBKDF2WithHmacSHA256 which needs API 26.
 *
 * Stored format: "<base64 salt>:<base64 hash>".
 */
object PasswordHasher {

    private const val ITERATIONS = 12000
    private const val SALT_BYTES = 16

    fun hash(password: String): String {
        val salt = ByteArray(SALT_BYTES)
        SecureRandom().nextBytes(salt)
        val digest = deriveKey(password, salt)
        return Base64.encodeToString(salt, Base64.NO_WRAP) + ":" +
            Base64.encodeToString(digest, Base64.NO_WRAP)
    }

    fun verify(password: String, stored: String): Boolean {
        val parts = stored.split(":")
        if (parts.size != 2) return false
        return try {
            val salt = Base64.decode(parts[0], Base64.NO_WRAP)
            val expected = Base64.decode(parts[1], Base64.NO_WRAP)
            val actual = deriveKey(password, salt)
            constantTimeEquals(expected, actual)
        } catch (e: Exception) {
            false
        }
    }

    private fun deriveKey(password: String, salt: ByteArray): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        var digest = md.digest(password.toByteArray(Charsets.UTF_8))
        repeat(ITERATIONS) {
            md.reset()
            md.update(salt)
            digest = md.digest(digest)
        }
        return digest
    }

    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].toInt() xor b[i].toInt())
        }
        return result == 0
    }
}
