package com.example.kendaraanbp1.ui.servicehistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kendaraanbp1.data.local.entity.ServiceLogEntity
import com.example.kendaraanbp1.data.repository.Resource
import com.example.kendaraanbp1.data.repository.ServiceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ServiceHistoryViewModel(
    private val serviceRepo: ServiceRepository,
    private val notificationScheduler: com.example.kendaraanbp1.service.NotificationScheduler
) : ViewModel() {

    private val _vehicleId = MutableStateFlow<Long?>(null)

    fun setVehicleId(id: Long?) {
        _vehicleId.value = id
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val serviceLogs: StateFlow<Resource<List<ServiceLogEntity>>> = _vehicleId.flatMapLatest { id ->
        if (id == null) return@flatMapLatest flowOf(Resource.Success(emptyList()))
        serviceRepo.getLogsByVehicle(id)
    }.stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading())

    fun addOrUpdate(log: ServiceLogEntity) {
        viewModelScope.launch {
            if (log.id == 0L) {
                val resource = serviceRepo.insertLog(log)
                if (resource is Resource.Success) {
                    val finalId = resource.data ?: return@launch
                    log.nextServiceDate?.let { dateMillis ->
                        notificationScheduler.scheduleServiceNotifs(
                            serviceId = finalId,
                            nextServiceDateMillis = dateMillis,
                            title = "Pengingat Servis Kendaraan",
                            message = "Servis ${log.category} sudah dekat."
                        )
                    }
                }
            } else {
                val resource = serviceRepo.updateLog(log)
                if (resource is Resource.Success) {
                    log.nextServiceDate?.let { dateMillis ->
                        notificationScheduler.scheduleServiceNotifs(
                            serviceId = log.id,
                            nextServiceDateMillis = dateMillis,
                            title = "Pengingat Servis Kendaraan",
                            message = "Servis ${log.category} sudah dekat."
                        )
                    } ?: run {
                        notificationScheduler.cancelServiceNotifs(log.id)
                    }
                }
            }
        }
    }

    fun deleteLog(log: ServiceLogEntity) {
        viewModelScope.launch {
            val resource = serviceRepo.deleteLog(log)
            if (resource is Resource.Success) {
                notificationScheduler.cancelServiceNotifs(log.id)
            }
        }
    }
}
