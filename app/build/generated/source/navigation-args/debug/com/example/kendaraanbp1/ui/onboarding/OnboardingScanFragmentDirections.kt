package com.example.kendaraanbp1.ui.onboarding

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.kendaraanbp1.R

public class OnboardingScanFragmentDirections private constructor() {
  public companion object {
    public fun actionOnboardingScanFragmentToOnboardingReminderFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_onboardingScanFragment_to_onboardingReminderFragment)
  }
}
