package com.example.kendaraanbp1.data.repository

import com.example.kendaraanbp1.data.local.dao.DocumentDao
import com.example.kendaraanbp1.data.local.entity.VehicleDocumentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DocumentRepository(private val documentDao: DocumentDao) {

    fun getDocumentsByVehicle(vehicleId: Long): Flow<Resource<List<VehicleDocumentEntity>>> = documentDao.getDocumentsByVehicle(vehicleId)
        .map { Resource.Success(it) as Resource<List<VehicleDocumentEntity>> }
        .catch { e -> emit(Resource.Error(e.message ?: "Terjadi kesalahan saat memuat dokumen")) }

    fun getExpiringDocuments(vehicleId: Long, currentTime: Long, targetDate: Long): Flow<Resource<List<VehicleDocumentEntity>>> = 
        documentDao.getExpiringDocuments(vehicleId, currentTime, targetDate)
            .map { Resource.Success(it) as Resource<List<VehicleDocumentEntity>> }
            .catch { e -> emit(Resource.Error(e.message ?: "Terjadi kesalahan saat memuat jadwal dokumen")) }

    suspend fun insertDocument(document: VehicleDocumentEntity): Resource<Long> {
        return try {
            val id = documentDao.insertDocument(document)
            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal menyimpan dokumen")
        }
    }

    suspend fun updateDocument(document: VehicleDocumentEntity): Resource<Unit> {
        return try {
            documentDao.updateDocument(document)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal memperbarui dokumen")
        }
    }

    suspend fun deleteDocument(document: VehicleDocumentEntity): Resource<Unit> {
        return try {
            documentDao.deleteDocument(document)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal menghapus dokumen")
        }
    }
}
