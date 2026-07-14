package com.example.kendaraanbp1.ui.adddocument

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kendaraanbp1.data.local.entity.VehicleDocumentEntity
import com.example.kendaraanbp1.data.repository.DocumentRepository
import com.example.kendaraanbp1.data.repository.Resource
import com.example.kendaraanbp1.service.NotificationScheduler
import kotlinx.coroutines.launch

class AddDocumentViewModel(
    private val repository: DocumentRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    fun saveDocument(
        vehicleId: Long,
        documentType: String,
        documentNumber: String,
        validDays: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentTime = System.currentTimeMillis()
        val expiryTime = currentTime + (validDays * 24L * 60L * 60L * 1000L)
        
        val document = VehicleDocumentEntity(
            vehicleId = vehicleId,
            documentType = documentType,
            documentNumber = documentNumber,
            issuedDate = currentTime,
            expiryDate = expiryTime,
            photoPath = null
        )
        
        viewModelScope.launch {
            val resource = repository.insertDocument(document)
            if (resource is Resource.Success) {
                val finalId = resource.data ?: return@launch
                document.expiryDate?.let { dateMillis ->
                    notificationScheduler.scheduleDocumentNotifs(
                        documentId = finalId,
                        expiryDateMillis = dateMillis,
                        title = "Pengingat Dokumen Kendaraan",
                        message = "Dokumen ${document.documentType} akan segera berakhir."
                    )
                }
                onSuccess()
            } else if (resource is Resource.Error) {
                onError(resource.message ?: "Terjadi kesalahan tidak diketahui")
            }
        }
    }

    fun updateDocument(
        id: Long,
        vehicleId: Long,
        documentType: String,
        documentNumber: String,
        validDays: Int,
        originalIssuedDate: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val expiryTime = originalIssuedDate + (validDays * 24L * 60L * 60L * 1000L)
        
        val document = VehicleDocumentEntity(
            id = id,
            vehicleId = vehicleId,
            documentType = documentType,
            documentNumber = documentNumber,
            issuedDate = originalIssuedDate,
            expiryDate = expiryTime,
            photoPath = null
        )
        
        viewModelScope.launch {
            val resource = repository.updateDocument(document)
            if (resource is Resource.Success) {
                document.expiryDate?.let { dateMillis ->
                    notificationScheduler.scheduleDocumentNotifs(
                        documentId = document.id,
                        expiryDateMillis = dateMillis,
                        title = "Pengingat Dokumen Kendaraan",
                        message = "Dokumen ${document.documentType} akan segera berakhir."
                    )
                } ?: run {
                    notificationScheduler.cancelDocumentNotifs(document.id)
                }
                onSuccess()
            } else if (resource is Resource.Error) {
                onError(resource.message ?: "Terjadi kesalahan tidak diketahui")
            }
        }
    }
}
