package com.example.kendaraanbp1.ui.register

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.kendaraanbp1.R

public class RegisterFragmentDirections private constructor() {
  public companion object {
    public fun actionRegisterFragmentToHomeDashboardFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_registerFragment_to_homeDashboardFragment)
  }
}
