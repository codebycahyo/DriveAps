package com.example.kendaraanbp1.util

import android.content.Context

/**
 * Persists the currently signed-in user so the app can auto-login on relaunch.
 * Offline only — just a small SharedPreferences record of the local account.
 */
object SessionManager {

    private const val PREFS = "drivetrack_session"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun saveSession(context: Context, userId: Long, name: String, email: String) {
        prefs(context).edit()
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean =
        prefs(context).getLong(KEY_USER_ID, -1L) != -1L

    fun getUserId(context: Context): Long =
        prefs(context).getLong(KEY_USER_ID, -1L)

    fun getUserName(context: Context): String? =
        prefs(context).getString(KEY_USER_NAME, null)

    fun getUserEmail(context: Context): String? =
        prefs(context).getString(KEY_USER_EMAIL, null)

    fun logout(context: Context) {
        prefs(context).edit().clear().apply()
    }
}
