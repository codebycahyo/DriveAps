package com.example.kendaraanbp1.ui.onboarding

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.kendaraanbp1.R

public class OnboardingWelcomeFragmentDirections private constructor() {
  public companion object {
    public fun actionOnboardingWelcomeFragmentToOnboardingScanFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_onboardingWelcomeFragment_to_onboardingScanFragment)
  }
}
