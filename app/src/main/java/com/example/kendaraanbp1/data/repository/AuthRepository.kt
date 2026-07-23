package com.example.kendaraanbp1.data.repository

import com.example.kendaraanbp1.data.local.dao.UserDao
import com.example.kendaraanbp1.data.local.entity.UserEntity
import com.example.kendaraanbp1.util.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Which field an auth failure should be attributed to (drives inline UI errors). */
enum class AuthField { NAME, EMAIL, PASSWORD, CONFIRM, TERMS, GENERAL }

sealed class AuthOutcome {
    data class Success(val user: UserEntity) : AuthOutcome()
    data class Failure(val field: AuthField, val message: String) : AuthOutcome()
}

/**
 * Offline account store. Register creates a local account (unique email, hashed
 * password); login verifies credentials against it. This is what makes
 * "register then immediately log in" work.
 */
class AuthRepository(private val userDao: UserDao) {

    suspend fun register(name: String, email: String, password: String): AuthOutcome =
        withContext(Dispatchers.Default) {
            try {
                val normalizedEmail = email.trim().lowercase()
                if (userDao.countByEmail(normalizedEmail) > 0) {
                    return@withContext AuthOutcome.Failure(
                        AuthField.EMAIL, "Email sudah terdaftar. Silakan masuk."
                    )
                }
                val user = UserEntity(
                    name = name.trim(),
                    email = normalizedEmail,
                    passwordHash = PasswordHasher.hash(password)
                )
                val id = userDao.insert(user)
                AuthOutcome.Success(user.copy(id = id))
            } catch (e: Exception) {
                AuthOutcome.Failure(AuthField.GENERAL, e.message ?: "Gagal membuat akun")
            }
        }

    suspend fun login(email: String, password: String): AuthOutcome =
        withContext(Dispatchers.Default) {
            try {
                val normalizedEmail = email.trim().lowercase()
                val user = userDao.getByEmail(normalizedEmail)
                    ?: return@withContext AuthOutcome.Failure(AuthField.EMAIL, "Email tidak terdaftar")
                if (!PasswordHasher.verify(password, user.passwordHash)) {
                    return@withContext AuthOutcome.Failure(AuthField.PASSWORD, "Kata sandi salah")
                }
                AuthOutcome.Success(user)
            } catch (e: Exception) {
                AuthOutcome.Failure(AuthField.GENERAL, e.message ?: "Gagal masuk")
            }
        }
}
