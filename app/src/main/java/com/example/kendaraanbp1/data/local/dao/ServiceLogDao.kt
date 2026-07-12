package com.example.kendaraanbp1.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kendaraanbp1.data.local.entity.ServiceLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceLogDao {

    @Query("SELECT * FROM service_logs WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getLogsByVehicle(vehicleId: Long): Flow<List<ServiceLogEntity>>

    @Query("SELECT * FROM service_logs WHERE vehicleId = :vehicleId AND nextServiceDate IS NOT NULL AND nextServiceDate >= :currentTime ORDER BY nextServiceDate ASC")
    fun getUpcomingServices(vehicleId: Long, currentTime: Long): Flow<List<ServiceLogEntity>>

    @Query("SELECT * FROM service_logs WHERE nextServiceDate IS NOT NULL")
    suspend fun getAllLogsWithUpcomingService(): List<ServiceLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ServiceLogEntity): Long

    @Update
    suspend fun updateLog(log: ServiceLogEntity)

    @Delete
    suspend fun deleteLog(log: ServiceLogEntity)
}
