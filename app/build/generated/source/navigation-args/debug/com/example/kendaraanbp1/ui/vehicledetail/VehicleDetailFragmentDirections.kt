package com.example.kendaraanbp1.ui.vehicledetail

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.kendaraanbp1.R

public class VehicleDetailFragmentDirections private constructor() {
  public companion object {
    public fun actionVehicleDetailFragmentToHomeDashboardFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_vehicleDetailFragment_to_homeDashboardFragment)

    public fun actionVehicleDetailFragmentToFuelHistoryFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_vehicleDetailFragment_to_fuelHistoryFragment)

    public fun actionVehicleDetailFragmentToServiceHistoryFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_vehicleDetailFragment_to_serviceHistoryFragment)
  }
}
