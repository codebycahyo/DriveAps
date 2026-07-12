package com.example.kendaraanbp1.ui.login

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.kendaraanbp1.R

public class LoginFragmentDirections private constructor() {
  public companion object {
    public fun actionLoginFragmentToRegisterFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_loginFragment_to_registerFragment)

    public fun actionLoginFragmentToHomeDashboardFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_loginFragment_to_homeDashboardFragment)
  }
}
