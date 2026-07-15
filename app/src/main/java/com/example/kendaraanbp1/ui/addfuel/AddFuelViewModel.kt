package com.example.kendaraanbp1.ui.addfuel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kendaraanbp1.data.local.entity.FuelLogEntity
import com.example.kendaraanbp1.data.repository.FuelRepository
import com.example.kendaraanbp1.data.repository.Resource
import kotlinx.coroutines.launch

class AddFuelViewModel(
    private val repository: FuelRepository
) : ViewModel() {

    fun saveFuelEntry(
        vehicleId: Long,
        liters: Double,
        pricePerLiter: Double,
        totalCost: Double,
        odometer: Int,
        stationName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val finalTotalCost = if (totalCost > 0.0) totalCost else (liters * pricePerLiter)
        val fuelLog = FuelLogEntity(
            vehicleId = vehicleId,
            date = System.currentTimeMillis(),
            liters = liters,
            pricePerLiter = pricePerLiter,
            totalCost = finalTotalCost,
            odometer = odometer,
            stationName = stationName,
            receiptPhotoPath = null
        )
        viewModelScope.launch {
            val resource = repository.insertLog(fuelLog)
            if (resource is Resource.Success) {
                onSuccess()
            } else if (resource is Resource.Error) {
                onError(resource.message ?: "Terjadi kesalahan tidak diketahui")
            }
        }
    }

    fun updateFuelEntry(
        id: Long,
        vehicleId: Long,
        originalDate: Long,
        liters: Double,
        pricePerLiter: Double,
        totalCost: Double,
        odometer: Int,
        stationName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val finalTotalCost = if (totalCost > 0.0) totalCost else (liters * pricePerLiter)
        val fuelLog = FuelLogEntity(
            id = id,
            vehicleId = vehicleId,
            date = originalDate,
            liters = liters,
            pricePerLiter = pricePerLiter,
            totalCost = finalTotalCost,
            odometer = odometer,
            stationName = stationName,
            receiptPhotoPath = null
        )
        viewModelScope.launch {
            val resource = repository.updateLog(fuelLog)
            if (resource is Resource.Success) {
                onSuccess()
            } else if (resource is Resource.Error) {
                onError(resource.message ?: "Terjadi kesalahan tidak diketahui")
            }
        }
    }
}
