package com.example.kendaraanbp1.ui.profile

import androidx.lifecycle.ViewModel
import com.example.kendaraanbp1.data.repository.FuelRepository
import com.example.kendaraanbp1.data.repository.ServiceRepository
import com.example.kendaraanbp1.data.repository.VehicleRepository
import kotlinx.coroutines.flow.first

class ProfileViewModel(
    private val vehicleRepository: VehicleRepository,
    private val fuelRepository: FuelRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    suspend fun getVehicle(vehicleId: Long) = vehicleRepository.getVehicleById(vehicleId)
    
    suspend fun getFuelHistory(vehicleId: Long) = fuelRepository.getLogsByVehicle(vehicleId).first()
    
    suspend fun getServiceHistory(vehicleId: Long) = serviceRepository.getLogsByVehicle(vehicleId).first()
}
