package com.example.kendaraanbp1.ui.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kendaraanbp1.data.local.entity.VehicleEntity
import com.example.kendaraanbp1.data.repository.Resource
import com.example.kendaraanbp1.data.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedVehicleViewModel(private val vehicleRepository: VehicleRepository) : ViewModel() {

    private val _selectedVehicleId = MutableStateFlow<Long?>(null)
    val selectedVehicleId: StateFlow<Long?> = _selectedVehicleId.asStateFlow()

    private val _allVehicles = MutableStateFlow<Resource<List<VehicleEntity>>>(Resource.Loading())
    val allVehicles: StateFlow<Resource<List<VehicleEntity>>> = _allVehicles.asStateFlow()

    init {
        viewModelScope.launch {
            vehicleRepository.getAllVehicles().collect { resource ->
                _allVehicles.value = resource

                if (resource is Resource.Success) {
                    val vehicles = resource.data ?: emptyList()
                    val currentId = _selectedVehicleId.value

                    when {
                        // Auto-select first vehicle if none selected
                        currentId == null -> {
                            vehicles.firstOrNull()?.let { _selectedVehicleId.value = it.id }
                        }
                        // If the currently selected vehicle was deleted, fallback to first or null
                        vehicles.none { it.id == currentId } -> {
                            _selectedVehicleId.value = vehicles.firstOrNull()?.id
                        }
                        // else: selection still valid, do nothing
                    }
                }
            }
        }
    }

    fun selectVehicle(id: Long) {
        _selectedVehicleId.value = id
    }
}
