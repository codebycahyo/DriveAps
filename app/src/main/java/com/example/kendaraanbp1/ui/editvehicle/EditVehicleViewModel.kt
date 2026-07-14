package com.example.kendaraanbp1.ui.editvehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kendaraanbp1.data.local.entity.VehicleEntity
import com.example.kendaraanbp1.data.repository.Resource
import com.example.kendaraanbp1.data.repository.VehicleRepository
import com.example.kendaraanbp1.data.repository.FuelRepository
import com.example.kendaraanbp1.data.repository.ServiceRepository
import com.example.kendaraanbp1.data.repository.DocumentRepository
import com.example.kendaraanbp1.service.NotificationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditVehicleViewModel(
    private val repository: VehicleRepository,
    private val serviceRepo: ServiceRepository,
    private val documentRepo: DocumentRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    private val _vehicle = MutableStateFlow<VehicleEntity?>(null)
    val vehicle: StateFlow<VehicleEntity?> = _vehicle.asStateFlow()

    fun loadVehicle(id: Long) {
        viewModelScope.launch {
            repository.getAllVehicles().collect { resource ->
                if (resource is Resource.Success) {
                    val vehicles = resource.data ?: emptyList()
                    _vehicle.value = vehicles.find { it.id == id }
                }
            }
        }
    }

    fun updateVehicle(
        type: String,
        brand: String,
        model: String,
        plateNumber: String,
        year: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentVehicle = _vehicle.value ?: return
        val updatedVehicle = currentVehicle.copy(
            vehicleType = type,
            brand = brand,
            model = model,
            plateNumber = plateNumber,
            year = year,
            updatedAt = System.currentTimeMillis()
        )
        
        viewModelScope.launch {
            val resource = repository.insertVehicle(updatedVehicle) // REPLACE strategy handles update
            if (resource is Resource.Success) {
                onSuccess()
            } else if (resource is Resource.Error) {
                onError(resource.message ?: "Gagal mengupdate kendaraan")
            }
        }
    }

    fun deleteVehicle(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentVehicle = _vehicle.value ?: return
        viewModelScope.launch {
            try {
                // Cancel notifications for all services related to this vehicle
                val serviceRes = serviceRepo.getLogsByVehicle(currentVehicle.id).first()
                serviceRes.data?.forEach { service ->
                    notificationScheduler.cancelServiceNotifs(service.id)
                }

                // Cancel notifications for all documents related to this vehicle
                val docRes = documentRepo.getDocumentsByVehicle(currentVehicle.id).first()
                docRes.data?.forEach { doc ->
                    notificationScheduler.cancelDocumentNotifs(doc.id)
                }

                // Finally delete the vehicle. Database CASCADE will handle DB entries.
                val deleteRes = repository.deleteVehicle(currentVehicle)
                if (deleteRes is Resource.Success) {
                    onSuccess()
                } else {
                    onError(deleteRes.message ?: "Gagal menghapus kendaraan")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Terjadi kesalahan")
            }
        }
    }
}
