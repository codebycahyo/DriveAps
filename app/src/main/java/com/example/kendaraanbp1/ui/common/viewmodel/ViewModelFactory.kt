package com.example.kendaraanbp1.ui.common.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kendaraanbp1.data.local.AppDatabase
import com.example.kendaraanbp1.data.repository.DocumentRepository
import com.example.kendaraanbp1.data.repository.FuelRepository
import com.example.kendaraanbp1.data.repository.ServiceRepository
import com.example.kendaraanbp1.data.repository.VehicleRepository
import com.example.kendaraanbp1.ui.documents.DocumentViewModel
import com.example.kendaraanbp1.ui.fuelhistory.FuelHistoryViewModel
import com.example.kendaraanbp1.ui.home.HomeViewModel
import com.example.kendaraanbp1.ui.servicehistory.ServiceHistoryViewModel
import com.example.kendaraanbp1.ui.vehicledetail.VehicleDetailViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(context)
        val vehicleRepo = VehicleRepository(database.vehicleDao())
        val fuelRepo = FuelRepository(database.fuelLogDao())
        val serviceRepo = ServiceRepository(database.serviceLogDao())
        val documentRepo = DocumentRepository(database.documentDao())
        val notificationScheduler = com.example.kendaraanbp1.service.NotificationScheduler(context)

        return when {
            modelClass.isAssignableFrom(SharedVehicleViewModel::class.java) -> {
                SharedVehicleViewModel(vehicleRepo) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(fuelRepo, serviceRepo, documentRepo) as T
            }
            modelClass.isAssignableFrom(VehicleDetailViewModel::class.java) -> {
                VehicleDetailViewModel(vehicleRepo, fuelRepo, serviceRepo, documentRepo, notificationScheduler) as T
            }
            modelClass.isAssignableFrom(FuelHistoryViewModel::class.java) -> {
                FuelHistoryViewModel(fuelRepo) as T
            }
            modelClass.isAssignableFrom(ServiceHistoryViewModel::class.java) -> {
                ServiceHistoryViewModel(serviceRepo, notificationScheduler) as T
            }
            modelClass.isAssignableFrom(DocumentViewModel::class.java) -> {
                DocumentViewModel(documentRepo, notificationScheduler) as T
            }
            modelClass.isAssignableFrom(com.example.kendaraanbp1.ui.addvehicle.AddVehicleViewModel::class.java) -> {
                com.example.kendaraanbp1.ui.addvehicle.AddVehicleViewModel(vehicleRepo) as T
            }
            modelClass.isAssignableFrom(com.example.kendaraanbp1.ui.editvehicle.EditVehicleViewModel::class.java) -> {
                com.example.kendaraanbp1.ui.editvehicle.EditVehicleViewModel(vehicleRepo, serviceRepo, documentRepo, notificationScheduler) as T
            }
            modelClass.isAssignableFrom(com.example.kendaraanbp1.ui.addfuel.AddFuelViewModel::class.java) -> {
                com.example.kendaraanbp1.ui.addfuel.AddFuelViewModel(fuelRepo) as T
            }
            modelClass.isAssignableFrom(com.example.kendaraanbp1.ui.addservice.AddServiceViewModel::class.java) -> {
                com.example.kendaraanbp1.ui.addservice.AddServiceViewModel(serviceRepo, notificationScheduler) as T
            }
            modelClass.isAssignableFrom(com.example.kendaraanbp1.ui.adddocument.AddDocumentViewModel::class.java) -> {
                com.example.kendaraanbp1.ui.adddocument.AddDocumentViewModel(documentRepo, notificationScheduler) as T
            }
            modelClass.isAssignableFrom(com.example.kendaraanbp1.ui.profile.ProfileViewModel::class.java) -> {
                com.example.kendaraanbp1.ui.profile.ProfileViewModel(vehicleRepo, fuelRepo, serviceRepo) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
