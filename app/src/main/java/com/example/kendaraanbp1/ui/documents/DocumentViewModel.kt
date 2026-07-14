package com.example.kendaraanbp1.ui.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kendaraanbp1.data.local.entity.VehicleDocumentEntity
import com.example.kendaraanbp1.data.repository.DocumentRepository
import com.example.kendaraanbp1.data.repository.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DocumentViewModel(
    private val documentRepo: DocumentRepository,
    private val notificationScheduler: com.example.kendaraanbp1.service.NotificationScheduler
) : ViewModel() {

    private val _vehicleId = MutableStateFlow<Long?>(null)

    fun setVehicleId(id: Long?) {
        _vehicleId.value = id
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val documents: StateFlow<Resource<List<VehicleDocumentEntity>>> = _vehicleId.flatMapLatest { id ->
        if (id == null) return@flatMapLatest flowOf(Resource.Success(emptyList()))
        documentRepo.getDocumentsByVehicle(id)
    }.stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading())

    fun addOrUpdate(
        doc: VehicleDocumentEntity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (doc.id == 0L) {
                val resource = documentRepo.insertDocument(doc)
                if (resource is Resource.Success) {
                    val finalId = resource.data ?: return@launch
                    doc.expiryDate?.let { dateMillis ->
                        notificationScheduler.scheduleDocumentNotifs(
                            documentId = finalId,
                            expiryDateMillis = dateMillis,
                            title = "Pengingat Dokumen Kendaraan",
                            message = "Dokumen ${doc.documentType} akan segera berakhir."
                        )
                    }
                    onSuccess()
                } else if (resource is Resource.Error) {
                    onError(resource.message ?: "Gagal")
                }
            } else {
                val resource = documentRepo.updateDocument(doc)
                if (resource is Resource.Success) {
                    doc.expiryDate?.let { dateMillis ->
                        notificationScheduler.scheduleDocumentNotifs(
                            documentId = doc.id,
                            expiryDateMillis = dateMillis,
                            title = "Pengingat Dokumen Kendaraan",
                            message = "Dokumen ${doc.documentType} akan segera berakhir."
                        )
                    } ?: run {
                        notificationScheduler.cancelDocumentNotifs(doc.id)
                    }
                    onSuccess()
                } else if (resource is Resource.Error) {
                    onError(resource.message ?: "Gagal")
                }
            }
        }
    }

    fun deleteDocument(
        doc: VehicleDocumentEntity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val resource = documentRepo.deleteDocument(doc)
            if (resource is Resource.Success) {
                notificationScheduler.cancelDocumentNotifs(doc.id)
                onSuccess()
            } else if (resource is Resource.Error) {
                onError(resource.message ?: "Gagal")
            }
        }
    }
}
