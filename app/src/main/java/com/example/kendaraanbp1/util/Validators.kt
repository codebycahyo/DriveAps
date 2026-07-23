package com.example.kendaraanbp1.util

import android.util.Patterns

/** Shared client-side form validation rules. */
object Validators {

    const val MIN_PASSWORD_LENGTH = 6

    fun isValidEmail(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean =
        password.length >= MIN_PASSWORD_LENGTH
}
