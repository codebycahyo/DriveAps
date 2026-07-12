package com.example.kendaraanbp1.ui.onboarding

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kendaraanbp1.R
import com.example.kendaraanbp1.databinding.FragmentOnboardingWelcomeBinding
import com.example.kendaraanbp1.ui.util.applyBottomSystemBarPadding

class OnboardingWelcomeFragment : Fragment() {

    private var _binding: FragmentOnboardingWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOnboardingWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomDock.applyBottomSystemBarPadding()
        applyBlurIfSupported(binding.blobBlue, 40f)
        applyBlurIfSupported(binding.blobViolet, 40f)
        applyBlurIfSupported(binding.blobTeal, 50f)

        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingWelcomeFragment_to_onboardingScanFragment)
        }
    }

    private fun applyBlurIfSupported(target: View, radiusPx: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            target.setRenderEffect(
                RenderEffect.createBlurEffect(radiusPx, radiusPx, Shader.TileMode.CLAMP)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
