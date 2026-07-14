package com.example.kendaraanbp1.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object SettingsManager {
    private const val PREFS_NAME = "drivetrack_settings"
    private const val KEY_THEME = "theme_mode"
    private const val KEY_LANGUAGE = "language_code"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun applySettings(context: Context) {
        val prefs = getPrefs(context)
        val themeMode = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(themeMode)

        val langCode = prefs.getString(KEY_LANGUAGE, "id") ?: "id"
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(langCode))
    }

    fun setThemeMode(context: Context, mode: Int) {
        getPrefs(context).edit().putInt(KEY_THEME, mode).apply()
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun getThemeMode(context: Context): Int {
        return getPrefs(context).getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun setLanguage(context: Context, langCode: String) {
        getPrefs(context).edit().putString(KEY_LANGUAGE, langCode).apply()
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(langCode))
    }

    fun getLanguage(context: Context): String {
        return getPrefs(context).getString(KEY_LANGUAGE, "id") ?: "id"
    }
}
