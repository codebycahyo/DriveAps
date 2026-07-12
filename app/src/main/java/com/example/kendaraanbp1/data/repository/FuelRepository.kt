package com.example.kendaraanbp1.data.repository

import com.example.kendaraanbp1.data.local.dao.FuelLogDao
import com.example.kendaraanbp1.data.local.entity.FuelLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class FuelRepository(private val fuelLogDao: FuelLogDao) {

    fun getLogsByVehicle(vehicleId: Long): Flow<Resource<List<FuelLogEntity>>> = fuelLogDao.getLogsByVehicle(vehicleId)
        .map { Resource.Success(it) as Resource<List<FuelLogEntity>> }
        .catch { e -> emit(Resource.Error(e.message ?: "Terjadi kesalahan saat memuat riwayat BBM")) }

    fun getTotalExpenseByPeriod(vehicleId: Long, startDate: Long, endDate: Long): Flow<Resource<Double>> = 
        fuelLogDao.getTotalExpenseByPeriod(vehicleId, startDate, endDate)
            .map { Resource.Success(it ?: 0.0) as Resource<Double> }
            .catch { e -> emit(Resource.Error(e.message ?: "Terjadi kesalahan saat menghitung pengeluaran")) }

    suspend fun insertLog(log: FuelLogEntity): Resource<Long> {
        return try {
            val id = fuelLogDao.insertLog(log)
            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal menyimpan riwayat BBM")
        }
    }

    suspend fun updateLog(log: FuelLogEntity): Resource<Unit> {
        return try {
            fuelLogDao.updateLog(log)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal memperbarui riwayat BBM")
        }
    }

    suspend fun deleteLog(log: FuelLogEntity): Resource<Unit> {
        return try {
            fuelLogDao.deleteLog(log)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal menghapus riwayat BBM")
        }
    }
}
