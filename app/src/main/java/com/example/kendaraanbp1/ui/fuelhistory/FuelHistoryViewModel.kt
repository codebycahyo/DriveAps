package com.example.kendaraanbp1.ui.fuelhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kendaraanbp1.data.local.entity.FuelLogEntity
import com.example.kendaraanbp1.data.repository.FuelRepository
import com.example.kendaraanbp1.data.repository.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class FuelHistoryViewModel(
    private val fuelRepo: FuelRepository
) : ViewModel() {

    private val _vehicleId = MutableStateFlow<Long?>(null)

    fun setVehicleId(id: Long?) {
        _vehicleId.value = id
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val fuelLogs: StateFlow<Resource<List<FuelLogEntity>>> = _vehicleId.flatMapLatest { id ->
        if (id == null) return@flatMapLatest flowOf(Resource.Success(emptyList()))
        fuelRepo.getLogsByVehicle(id)
    }.stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading())

    // For total expense, let's say last 30 days
    @OptIn(ExperimentalCoroutinesApi::class)
    val totalExpense: StateFlow<Resource<Double>> = _vehicleId.flatMapLatest { id ->
        if (id == null) return@flatMapLatest flowOf(Resource.Success(0.0))
        val end = System.currentTimeMillis()
        val start = end - (30L * 24 * 60 * 60 * 1000)
        fuelRepo.getTotalExpenseByPeriod(id, start, end)
    }.stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading())
}
