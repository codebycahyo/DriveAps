package com.example.kendaraanbp1

import android.app.Application
import com.example.kendaraanbp1.util.SettingsManager

class DriveTrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Apply the saved theme (Light / Dark / Follow System) and language
        // before any Activity inflates, so the correct night mode is used
        // from the very first frame and there is no flash on cold start.
        SettingsManager.applySettings(this)
    }
}
