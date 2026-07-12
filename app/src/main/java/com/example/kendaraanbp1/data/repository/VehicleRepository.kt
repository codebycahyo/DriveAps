package com.example.kendaraanbp1.data.repository

import com.example.kendaraanbp1.data.local.dao.VehicleDao
import com.example.kendaraanbp1.data.local.entity.VehicleEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class VehicleRepository(private val vehicleDao: VehicleDao) {

    fun getAllVehicles(): Flow<Resource<List<VehicleEntity>>> = vehicleDao.getAllVehicles()
        .map { Resource.Success(it) as Resource<List<VehicleEntity>> }
        .catch { e -> emit(Resource.Error(e.message ?: "Terjadi kesalahan saat memuat data kendaraan")) }

    suspend fun getVehicleById(id: Long): Resource<VehicleEntity> {
        return try {
            val vehicle = vehicleDao.getVehicleById(id)
            if (vehicle != null) {
                Resource.Success(vehicle)
            } else {
                Resource.Error("Kendaraan tidak ditemukan")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Terjadi kesalahan saat memuat kendaraan")
        }
    }

    suspend fun insertVehicle(vehicle: VehicleEntity): Resource<Long> {
        return try {
            val id = vehicleDao.insertVehicle(vehicle)
            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal menyimpan data kendaraan")
        }
    }

    suspend fun updateVehicle(vehicle: VehicleEntity): Resource<Unit> {
        return try {
            vehicleDao.updateVehicle(vehicle)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal memperbarui data kendaraan")
        }
    }

    suspend fun deleteVehicle(vehicle: VehicleEntity): Resource<Unit> {
        return try {
            vehicleDao.deleteVehicle(vehicle)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal menghapus data kendaraan")
        }
    }
}
