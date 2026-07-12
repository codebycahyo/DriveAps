package com.example.kendaraanbp1.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kendaraanbp1.data.local.entity.FuelLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelLogDao {

    @Query("SELECT * FROM fuel_logs WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getLogsByVehicle(vehicleId: Long): Flow<List<FuelLogEntity>>

    @Query("SELECT SUM(totalCost) FROM fuel_logs WHERE vehicleId = :vehicleId AND date >= :startDate AND date <= :endDate")
    fun getTotalExpenseByPeriod(vehicleId: Long, startDate: Long, endDate: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: FuelLogEntity): Long

    @Update
    suspend fun updateLog(log: FuelLogEntity)

    @Delete
    suspend fun deleteLog(log: FuelLogEntity)
}
