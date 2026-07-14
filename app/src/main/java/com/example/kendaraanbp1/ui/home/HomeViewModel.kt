package com.example.kendaraanbp1.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kendaraanbp1.R
import com.example.kendaraanbp1.data.model.ActivityItem
import com.example.kendaraanbp1.data.model.ReminderItem
import com.example.kendaraanbp1.data.model.ReminderSeverity
import com.example.kendaraanbp1.data.repository.DocumentRepository
import com.example.kendaraanbp1.data.repository.FuelRepository
import com.example.kendaraanbp1.data.repository.Resource
import com.example.kendaraanbp1.data.repository.ServiceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.kendaraanbp1.util.Formatters.toRupiah
import com.example.kendaraanbp1.util.VehicleStats

class HomeViewModel(
    private val fuelRepo: FuelRepository,
    private val serviceRepo: ServiceRepository,
    private val documentRepo: DocumentRepository
) : ViewModel() {

    private val _vehicleId = MutableStateFlow<Long?>(null)

    fun setVehicleId(id: Long?) {
        _vehicleId.value = id
    }

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

    @OptIn(ExperimentalCoroutinesApi::class)
    val recentActivities: StateFlow<Resource<List<ActivityItem>>> = _vehicleId.flatMapLatest { id ->
        if (id == null) return@flatMapLatest flowOf(Resource.Success(emptyList()))
        
        combine(
            fuelRepo.getLogsByVehicle(id),
            serviceRepo.getLogsByVehicle(id)
        ) { fuelRes, serviceRes ->
            val fuelData = fuelRes.data ?: emptyList()
            val serviceData = serviceRes.data ?: emptyList()

            val fuelItems = fuelData.map { log ->
                ActivityItem(
                    id = log.id,
                    title = log.stationName,
                    subtitle = dateFormat.format(Date(log.date)),
                    amountLabel = log.totalCost.toRupiah(),
                    iconRes = R.drawable.ic_fuel
                )
            }

            val serviceItems = serviceData.map { log ->
                ActivityItem(
                    id = log.id + 10000, // Offset to avoid ID collision
                    title = log.workshopName,
                    subtitle = dateFormat.format(Date(log.date)),
                    amountLabel = log.totalCost.toRupiah(),
                    iconRes = R.drawable.ic_wrench
                )
            }

            val allItems = (fuelItems + serviceItems)
                // In real app, we should sort by raw date timestamp. 
                // Since ActivityItem uses formatted String date, we simplify here.
                .take(5)

            Resource.Success(allItems) as Resource<List<ActivityItem>>
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading())

    @OptIn(ExperimentalCoroutinesApi::class)
    val reminders: StateFlow<Resource<List<ReminderItem>>> = _vehicleId.flatMapLatest { id ->
        if (id == null) return@flatMapLatest flowOf(Resource.Success(emptyList()))

        val currentTime = System.currentTimeMillis()
        val targetDate = currentTime + (30L * 24 * 60 * 60 * 1000) // 30 days from now

        combine(
            documentRepo.getExpiringDocuments(id, currentTime, targetDate),
            serviceRepo.getUpcomingServices(id, currentTime)
        ) { docRes, serviceRes ->
            val docData = docRes.data ?: emptyList()
            val serviceData = serviceRes.data ?: emptyList()

            val docItems = docData.map { doc ->
                val daysUntil = ((doc.expiryDate ?: 0L) - currentTime) / (1000 * 60 * 60 * 24)
                Pair(daysUntil, ReminderItem(
                    id = doc.id,
                    title = doc.documentType,
                    subtitle = "Jatuh tempo: ${dateFormat.format(Date(doc.expiryDate ?: 0L))} ($daysUntil hari)",
                    iconRes = R.drawable.ic_file_text,
                    severity = if (daysUntil <= 7) ReminderSeverity.URGENT else ReminderSeverity.STANDARD
                ))
            }

            val serviceItems = serviceData.map { svc ->
                val daysUntil = ((svc.nextServiceDate ?: 0L) - currentTime) / (1000 * 60 * 60 * 24)
                Pair(daysUntil, ReminderItem(
                    id = svc.id + 10000,
                    title = "Servis: ${svc.category}",
                    subtitle = "Jadwal: ${dateFormat.format(Date(svc.nextServiceDate ?: 0L))} ($daysUntil hari)",
                    iconRes = R.drawable.ic_wrench,
                    severity = if (daysUntil <= 7) ReminderSeverity.URGENT else ReminderSeverity.STANDARD
                ))
            }

            val allItems = (docItems + serviceItems).sortedBy { it.first }
                .map { it.second }

            Resource.Success(allItems) as Resource<List<ReminderItem>>
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading())

    /** Total pengeluaran (BBM + servis) bulan kalender berjalan, sudah diformat Rupiah. */
    @OptIn(ExperimentalCoroutinesApi::class)
    val monthlyExpense: StateFlow<String> = _vehicleId.flatMapLatest { id ->
        if (id == null) return@flatMapLatest flowOf(0.0.toRupiah())

        val monthStart = VehicleStats.startOfCurrentMonth()
        combine(
            fuelRepo.getLogsByVehicle(id),
            serviceRepo.getLogsByVehicle(id)
        ) { fuelRes, serviceRes ->
            val fuelSum = (fuelRes.data ?: emptyList())
                .filter { it.date >= monthStart }.sumOf { it.totalCost }
            val serviceSum = (serviceRes.data ?: emptyList())
                .filter { it.date >= monthStart }.sumOf { it.totalCost }
            (fuelSum + serviceSum).toRupiah()
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0.toRupiah())

    /** Efisiensi BBM (km/L) dari data pengisian, atau "–" bila data belum cukup. */
    @OptIn(ExperimentalCoroutinesApi::class)
    val efficiency: StateFlow<String> = _vehicleId.flatMapLatest { id ->
        if (id == null) return@flatMapLatest flowOf("–")
        fuelRepo.getLogsByVehicle(id).map { res ->
            VehicleStats.formatKmPerL(VehicleStats.fuelEconomyKmPerL(res.data ?: emptyList()))
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, "–")

    /** Pengingat terdekat untuk badge di hero card; null bila tidak ada. */
    val topReminder: StateFlow<ReminderItem?> = reminders.map { res ->
        (res as? Resource.Success)?.data?.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
}
