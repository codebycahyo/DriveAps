package com.example.kendaraanbp1.ui.onboarding

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.kendaraanbp1.R

public class OnboardingReminderFragmentDirections private constructor() {
  public companion object {
    public fun actionOnboardingReminderFragmentToLoginFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_onboardingReminderFragment_to_loginFragment)
  }
}
