package com.example.kendaraanbp1.ui.addvehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kendaraanbp1.data.local.entity.VehicleEntity
import com.example.kendaraanbp1.data.repository.VehicleRepository
import com.example.kendaraanbp1.data.repository.Resource
import kotlinx.coroutines.launch

class AddVehicleViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    fun saveVehicle(
        type: String,
        brand: String,
        model: String,
        plateNumber: String,
        year: Int,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        val vehicle = VehicleEntity(
            vehicleType = type,
            brand = brand,
            model = model,
            plateNumber = plateNumber,
            year = year,
            photoPath = null
        )
        viewModelScope.launch {
            val resource = repository.insertVehicle(vehicle)
            if (resource is Resource.Success) {
                resource.data?.let { id ->
                    onSuccess(id)
                }
            } else if (resource is Resource.Error) {
                onError(resource.message ?: "Terjadi kesalahan tidak diketahui")
            }
        }
    }
}
