package com.example.kendaraanbp1.ui.addfuel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kendaraanbp1.data.local.entity.FuelLogEntity
import com.example.kendaraanbp1.data.repository.FuelRepository
import kotlinx.coroutines.launch

class AddFuelViewModel(
    private val repository: FuelRepository
) : ViewModel() {

    fun saveFuelEntry(
        vehicleId: Long,
        liters: Double,
        pricePerLiter: Double,
        odometer: Int,
        stationName: String,
        onSuccess: () -> Unit
    ) {
        val totalCost = liters * pricePerLiter
        val fuelLog = FuelLogEntity(
            vehicleId = vehicleId,
            date = System.currentTimeMillis(),
            liters = liters,
            pricePerLiter = pricePerLiter,
            totalCost = totalCost,
            odometer = odometer,
            stationName = stationName,
            receiptPhotoPath = null
        )
        viewModelScope.launch {
            repository.insertLog(fuelLog)
            onSuccess()
        }
    }
}
