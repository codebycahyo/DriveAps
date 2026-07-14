package com.example.kendaraanbp1.util

import com.example.kendaraanbp1.data.local.entity.FuelLogEntity
import com.example.kendaraanbp1.data.local.entity.ServiceLogEntity
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

/**
 * Real, DB-derived statistics used across the dashboard, vehicle detail and
 * history screens. Kept in one place so every screen shows the same number.
 * All functions degrade gracefully (return null / 0) when there is not enough
 * data — callers render "–" instead of a fabricated value.
 */
object VehicleStats {

    private val numberFormat = NumberFormat.getNumberInstance(Locale("in", "ID"))

    /** e.g. 12500 -> "12.500". */
    fun formatThousands(value: Number): String = numberFormat.format(value)

    /**
     * Fuel economy in km/L using the full-to-full method: distance travelled
     * between the first and last fill divided by the fuel added in between
     * (every fill except the last, whose fuel has not been consumed yet).
     * Returns null when it cannot be computed honestly (fewer than 2 fills,
     * no odometer progress, or no fuel recorded).
     */
    fun fuelEconomyKmPerL(logs: List<FuelLogEntity>): Double? {
        if (logs.size < 2) return null
        val sorted = logs.sortedBy { it.odometer }
        val distance = sorted.last().odometer - sorted.first().odometer
        if (distance <= 0) return null
        val litersUsed = sorted.dropLast(1).sumOf { it.liters }
        if (litersUsed <= 0.0) return null
        return distance / litersUsed
    }

    /** Formats a km/L value like "14.5", or "–" when null. */
    fun formatKmPerL(value: Double?): String =
        if (value == null) "–" else String.format(Locale("in", "ID"), "%.1f", value)

    /** Highest odometer reading seen across fuel and service logs (0 if none). */
    fun lastOdometer(fuel: List<FuelLogEntity>, service: List<ServiceLogEntity>): Int {
        val maxFuel = fuel.maxOfOrNull { it.odometer } ?: 0
        val maxService = service.maxOfOrNull { it.odometer } ?: 0
        return maxOf(maxFuel, maxService)
    }

    /** Epoch millis for 00:00 on the first day of the current calendar month. */
    fun startOfCurrentMonth(): Long = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
