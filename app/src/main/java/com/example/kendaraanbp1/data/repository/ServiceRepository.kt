package com.example.kendaraanbp1.data.repository

import com.example.kendaraanbp1.data.local.dao.ServiceLogDao
import com.example.kendaraanbp1.data.local.entity.ServiceLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ServiceRepository(private val serviceLogDao: ServiceLogDao) {

    fun getLogsByVehicle(vehicleId: Long): Flow<Resource<List<ServiceLogEntity>>> = serviceLogDao.getLogsByVehicle(vehicleId)
        .map { Resource.Success(it) as Resource<List<ServiceLogEntity>> }
        .catch { e -> emit(Resource.Error(e.message ?: "Terjadi kesalahan saat memuat riwayat servis")) }

    fun getUpcomingServices(vehicleId: Long, currentTime: Long): Flow<Resource<List<ServiceLogEntity>>> = 
        serviceLogDao.getUpcomingServices(vehicleId, currentTime)
            .map { Resource.Success(it) as Resource<List<ServiceLogEntity>> }
            .catch { e -> emit(Resource.Error(e.message ?: "Terjadi kesalahan saat memuat jadwal servis")) }

    suspend fun insertLog(log: ServiceLogEntity): Resource<Long> {
        return try {
            val id = serviceLogDao.insertLog(log)
            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal menyimpan riwayat servis")
        }
    }

    suspend fun updateLog(log: ServiceLogEntity): Resource<Unit> {
        return try {
            serviceLogDao.updateLog(log)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal memperbarui riwayat servis")
        }
    }

    suspend fun deleteLog(log: ServiceLogEntity): Resource<Unit> {
        return try {
            serviceLogDao.deleteLog(log)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal menghapus riwayat servis")
        }
    }
}
