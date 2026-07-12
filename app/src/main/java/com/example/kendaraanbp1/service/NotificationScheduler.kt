package com.example.kendaraanbp1.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar

class NotificationScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleServiceNotifs(serviceId: Long, nextServiceDateMillis: Long, title: String, message: String) {
        val days = listOf(0, 1, 7)
        for (dayCode in days) {
            val notificationId = (10_000_000 + (serviceId * 10) + dayCode).toInt()
            scheduleAlarm(notificationId, nextServiceDateMillis, dayCode, title, message)
        }
    }

    fun cancelServiceNotifs(serviceId: Long) {
        val days = listOf(0, 1, 7)
        for (dayCode in days) {
            val notificationId = (10_000_000 + (serviceId * 10) + dayCode).toInt()
            cancelAlarm(notificationId)
        }
    }

    fun scheduleDocumentNotifs(documentId: Long, expiryDateMillis: Long, title: String, message: String) {
        val days = listOf(0, 1, 7)
        for (dayCode in days) {
            val notificationId = (20_000_000 + (documentId * 10) + dayCode).toInt()
            scheduleAlarm(notificationId, expiryDateMillis, dayCode, title, message)
        }
    }

    fun cancelDocumentNotifs(documentId: Long) {
        val days = listOf(0, 1, 7)
        for (dayCode in days) {
            val notificationId = (20_000_000 + (documentId * 10) + dayCode).toInt()
            cancelAlarm(notificationId)
        }
    }

    private fun scheduleAlarm(notificationId: Int, targetDateMillis: Long, daysBefore: Int, title: String, message: String) {
        // Calculate trigger time
        val calendar = Calendar.getInstance().apply {
            timeInMillis = targetDateMillis
            add(Calendar.DAY_OF_YEAR, -daysBefore)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val triggerAtMillis = calendar.timeInMillis
        val now = System.currentTimeMillis()
        
        // Skip if the time has already passed
        if (triggerAtMillis < now) {
            Log.d("NotificationScheduler", "Skipping alarm $notificationId because trigger time is in the past")
            return
        }

        val intent = Intent(context, NotificationPublisher::class.java).apply {
            putExtra(NotificationPublisher.EXTRA_NOTIFICATION_ID, notificationId)
            putExtra(NotificationPublisher.EXTRA_TITLE, title)
            putExtra(NotificationPublisher.EXTRA_MESSAGE, message)
            if (daysBefore > 0) {
                putExtra(NotificationPublisher.EXTRA_MESSAGE, "$message (H-$daysBefore)")
            }
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                } else {
                    // Fallback if permission not granted
                    alarmManager.setWindow(AlarmManager.RTC_WAKEUP, triggerAtMillis, 1000 * 60 * 10, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
            Log.d("NotificationScheduler", "Scheduled alarm $notificationId at $triggerAtMillis")
        } catch (e: SecurityException) {
            Log.e("NotificationScheduler", "Exact alarm permission missing", e)
        }
    }

    private fun cancelAlarm(notificationId: Int) {
        val intent = Intent(context, NotificationPublisher::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("NotificationScheduler", "Cancelled alarm $notificationId")
        }
    }
}
