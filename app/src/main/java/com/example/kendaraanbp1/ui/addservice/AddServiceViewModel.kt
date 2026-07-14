package com.example.kendaraanbp1.ui.addservice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kendaraanbp1.data.local.entity.ServiceLogEntity
import com.example.kendaraanbp1.data.repository.Resource
import com.example.kendaraanbp1.data.repository.ServiceRepository
import com.example.kendaraanbp1.service.NotificationScheduler
import kotlinx.coroutines.launch

class AddServiceViewModel(
    private val repository: ServiceRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    fun saveServiceEntry(
        vehicleId: Long,
        category: String,
        workshopName: String,
        odometer: Int,
        totalCost: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val serviceLog = ServiceLogEntity(
            vehicleId = vehicleId,
            date = System.currentTimeMillis(),
            category = category,
            workshopName = workshopName,
            odometer = odometer,
            totalCost = totalCost,
            receiptPhotoPath = null,
            nextServiceDate = null,
            nextServiceOdometer = null
        )
        viewModelScope.launch {
            val resource = repository.insertLog(serviceLog)
            if (resource is Resource.Success) {
                val finalId = resource.data ?: return@launch
                serviceLog.nextServiceDate?.let { dateMillis ->
                    notificationScheduler.scheduleServiceNotifs(
                        serviceId = finalId,
                        nextServiceDateMillis = dateMillis,
                        title = "Pengingat Servis Kendaraan",
                        message = "Servis ${serviceLog.category} sudah dekat."
                    )
                }
                onSuccess()
            } else if (resource is Resource.Error) {
                onError(resource.message ?: "Terjadi kesalahan tidak diketahui")
            }
        }
    }

    fun updateServiceEntry(
        id: Long,
        vehicleId: Long,
        originalDate: Long,
        category: String,
        workshopName: String,
        odometer: Int,
        totalCost: Double,
        originalNextServiceDate: Long?,
        originalNextServiceOdo: Int?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val serviceLog = ServiceLogEntity(
            id = id,
            vehicleId = vehicleId,
            date = originalDate,
            category = category,
            workshopName = workshopName,
            odometer = odometer,
            totalCost = totalCost,
            receiptPhotoPath = null,
            nextServiceDate = originalNextServiceDate,
            nextServiceOdometer = originalNextServiceOdo
        )
        viewModelScope.launch {
            val resource = repository.updateLog(serviceLog)
            if (resource is Resource.Success) {
                serviceLog.nextServiceDate?.let { dateMillis ->
                    notificationScheduler.scheduleServiceNotifs(
                        serviceId = serviceLog.id,
                        nextServiceDateMillis = dateMillis,
                        title = "Pengingat Servis Kendaraan",
                        message = "Servis ${serviceLog.category} sudah dekat."
                    )
                } ?: run {
                    notificationScheduler.cancelServiceNotifs(serviceLog.id)
                }
                onSuccess()
            } else if (resource is Resource.Error) {
                onError(resource.message ?: "Terjadi kesalahan tidak diketahui")
            }
        }
    }
}
