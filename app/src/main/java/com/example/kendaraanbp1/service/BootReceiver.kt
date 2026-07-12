package com.example.kendaraanbp1.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.kendaraanbp1.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Boot completed, rescheduling notifications...")
            val pendingResult = goAsync()
            val scheduler = NotificationScheduler(context)
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    
                    // Reschedule Services
                    val serviceLogs = db.serviceLogDao().getAllLogsWithUpcomingService()
                    for (service in serviceLogs) {
                        service.nextServiceDate?.let { dateMillis ->
                            scheduler.scheduleServiceNotifs(
                                serviceId = service.id,
                                nextServiceDateMillis = dateMillis,
                                title = "Pengingat Servis Kendaraan",
                                message = "Servis ${service.category} sudah dekat."
                            )
                        }
                    }

                    // Reschedule Documents
                    val documents = db.documentDao().getAllDocumentsWithExpiry()
                    for (doc in documents) {
                        doc.expiryDate?.let { dateMillis ->
                            scheduler.scheduleDocumentNotifs(
                                documentId = doc.id,
                                expiryDateMillis = dateMillis,
                                title = "Pengingat Dokumen Kendaraan",
                                message = "Dokumen ${doc.documentType} akan segera berakhir."
                            )
                        }
                    }
                    Log.d("BootReceiver", "Finished rescheduling")
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error rescheduling notifications", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
