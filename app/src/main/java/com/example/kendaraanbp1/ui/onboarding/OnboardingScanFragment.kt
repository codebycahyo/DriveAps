package com.example.kendaraanbp1.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kendaraanbp1.R
import com.example.kendaraanbp1.databinding.FragmentOnboardingScanBinding
import com.example.kendaraanbp1.ui.util.applyBottomSystemBarPadding
import com.example.kendaraanbp1.ui.util.startFloatingAnimation

class OnboardingScanFragment : Fragment() {

    private var _binding: FragmentOnboardingScanBinding? = null
    private val binding get() = _binding!!
    private var floatAnim: android.animation.Animator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOnboardingScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomDock.applyBottomSystemBarPadding()
        floatAnim = binding.heroImage.startFloatingAnimation()

        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingScanFragment_to_onboardingReminderFragment)
        }

        binding.skipButton.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingScanFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        floatAnim?.cancel()
        floatAnim = null
        _binding = null
    }
}
