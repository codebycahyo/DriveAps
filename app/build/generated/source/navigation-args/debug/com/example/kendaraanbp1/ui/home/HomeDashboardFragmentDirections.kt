package com.example.kendaraanbp1.ui.home

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.kendaraanbp1.R

public class HomeDashboardFragmentDirections private constructor() {
  public companion object {
    public fun actionHomeDashboardFragmentToAddVehicleFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeDashboardFragment_to_addVehicleFragment)

    public fun actionHomeDashboardFragmentToVehicleDetailFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeDashboardFragment_to_vehicleDetailFragment)

    public fun actionHomeDashboardFragmentToVehicleDocumentsFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeDashboardFragment_to_vehicleDocumentsFragment)

    public fun actionHomeDashboardFragmentToServiceHistoryFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeDashboardFragment_to_serviceHistoryFragment)

    public fun actionHomeDashboardFragmentToProfileFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeDashboardFragment_to_profileFragment)
  }
}
