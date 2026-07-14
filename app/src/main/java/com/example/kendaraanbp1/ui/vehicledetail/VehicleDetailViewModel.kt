package com.example.kendaraanbp1.ui.vehicledetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kendaraanbp1.R
import com.example.kendaraanbp1.data.local.entity.VehicleEntity
import com.example.kendaraanbp1.data.model.VehicleActivityItem
import com.example.kendaraanbp1.data.repository.FuelRepository
import com.example.kendaraanbp1.data.repository.Resource
import com.example.kendaraanbp1.data.repository.ServiceRepository
import com.example.kendaraanbp1.data.repository.VehicleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.kendaraanbp1.util.Formatters.toRupiah
import com.example.kendaraanbp1.util.VehicleStats

import com.example.kendaraanbp1.data.repository.DocumentRepository
import com.example.kendaraanbp1.service.NotificationScheduler

class VehicleDetailViewModel(
    private val vehicleRepo: VehicleRepository,
    private val fuelRepo: FuelRepository,
    private val serviceRepo: ServiceRepository,
    private val documentRepo: DocumentRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    private val _vehicleId = MutableStateFlow<Long?>(null)

    fun setVehicleId(id: Long?) {
        _vehicleId.value = id
    }

    private val _vehicleDetails = MutableStateFlow<Resource<VehicleEntity>>(Resource.Loading())
    val vehicleDetails: StateFlow<Resource<VehicleEntity>> = _vehicleDetails

    init {
        viewModelScope.launch {
            _vehicleId.collect { id ->
                if (id != null) {
                    _vehicleDetails.value = vehicleRepo.getVehicleById(id)
                }
            }
        }
    }

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

    @OptIn(ExperimentalCoroutinesApi::class)
    val vehicleActivities: StateFlow<Resource<List<VehicleActivityItem>>> = _vehicleId.flatMapLatest { id ->
        if (id == null) return@flatMapLatest flowOf(Resource.Success(emptyList()))
        
        combine(
            fuelRepo.getLogsByVehicle(id),
            serviceRepo.getLogsByVehicle(id)
        ) { fuelRes, serviceRes ->
            val fuelData = fuelRes.data ?: emptyList()
            val serviceData = serviceRes.data ?: emptyList()

            val fuelItems = fuelData.map { log ->
                VehicleActivityItem(
                    id = log.id,
                    title = "Isi BBM",
                    date = dateFormat.format(Date(log.date)),
                    subtitle = "${log.stationName} • ${log.liters} Liter",
                    amountLabel = "Rp ${log.totalCost.toLong()}",
                    iconRes = R.drawable.ic_fuel,
                    iconTintRes = R.color.icon_blue,
                    iconBgRes = R.color.status_info_container
                )
            }

            val serviceItems = serviceData.map { log ->
                VehicleActivityItem(
                    id = log.id + 10000,
                    title = log.category,
                    date = dateFormat.format(Date(log.date)),
                    subtitle = log.workshopName,
                    amountLabel = log.totalCost.toRupiah(),
                    iconRes = R.drawable.ic_wrench,
                    iconTintRes = R.color.status_success,
                    iconBgRes = R.color.surface_teal_light
                )
            }

            Resource.Success((fuelItems + serviceItems).take(15)) as Resource<List<VehicleActivityItem>>
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading())

    data class Stats(
        val totalFuelLabel: String,
        val serviceCountLabel: String,
        val odometerLabel: String,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val vehicleStats: StateFlow<Stats> = _vehicleId.flatMapLatest { id ->
        if (id == null) return@flatMapLatest flowOf(Stats("0L", "0", "0 km"))

        combine(
            fuelRepo.getLogsByVehicle(id),
            serviceRepo.getLogsByVehicle(id)
        ) { fuelRes, serviceRes ->
            val fuel = fuelRes.data ?: emptyList()
            val service = serviceRes.data ?: emptyList()

            val totalLiters = fuel.sumOf { it.liters }
            val litersLabel = if (totalLiters % 1.0 == 0.0) {
                totalLiters.toInt().toString()
            } else {
                String.format(Locale("in", "ID"), "%.1f", totalLiters)
            }
            val odo = VehicleStats.lastOdometer(fuel, service)

            Stats(
                totalFuelLabel = "${litersLabel}L",
                serviceCountLabel = service.size.toString(),
                odometerLabel = "${VehicleStats.formatThousands(odo)} km",
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, Stats("0L", "0", "0 km"))

}
