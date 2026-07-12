package com.example.kendaraanbp1.util

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object PermissionHelper {

    fun checkAndRequestNotificationPermission(
        context: Context,
        requestLauncher: ActivityResultLauncher<String>
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fun checkAndRequestExactAlarmPermission(
        context: Context,
        launchSettings: (Intent) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                MaterialAlertDialogBuilder(context)
                    .setTitle("Izin Alarm Tepat Waktu")
                    .setMessage("Aplikasi membutuhkan izin Alarm Tepat Waktu (Exact Alarm) agar notifikasi jadwal servis dan masa berlaku dokumen selalu muncul tepat waktu.\n\nMohon izinkan pada layar berikutnya.")
                    .setPositiveButton("Pengaturan") { _, _ ->
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = Uri.parse("package:${context.packageName}")
                        }
                        launchSettings(intent)
                    }
                    .setNegativeButton("Nanti", null)
                    .show()
            }
        }
    }
}
