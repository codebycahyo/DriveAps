package com.example.kendaraanbp1.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kendaraanbp1.data.local.entity.VehicleDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Query("SELECT * FROM vehicle_documents WHERE vehicleId = :vehicleId ORDER BY createdAt DESC")
    fun getDocumentsByVehicle(vehicleId: Long): Flow<List<VehicleDocumentEntity>>

    @Query("SELECT * FROM vehicle_documents WHERE vehicleId = :vehicleId AND expiryDate IS NOT NULL AND expiryDate <= :targetDate AND expiryDate >= :currentTime ORDER BY expiryDate ASC")
    fun getExpiringDocuments(vehicleId: Long, currentTime: Long, targetDate: Long): Flow<List<VehicleDocumentEntity>>

    @Query("SELECT * FROM vehicle_documents WHERE expiryDate IS NOT NULL")
    suspend fun getAllDocumentsWithExpiry(): List<VehicleDocumentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: VehicleDocumentEntity): Long

    @Update
    suspend fun updateDocument(document: VehicleDocumentEntity)

    @Delete
    suspend fun deleteDocument(document: VehicleDocumentEntity)
}
