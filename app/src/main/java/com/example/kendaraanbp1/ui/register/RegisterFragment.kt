package com.example.kendaraanbp1.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kendaraanbp1.R
import com.example.kendaraanbp1.data.repository.AuthField
import com.example.kendaraanbp1.data.repository.AuthOutcome
import com.example.kendaraanbp1.databinding.FragmentRegisterBinding
import com.example.kendaraanbp1.ui.common.viewmodel.ViewModelFactory
import com.example.kendaraanbp1.util.SessionManager
import com.example.kendaraanbp1.util.Validators
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    private var isSubmitting = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nameInputLayout.editText?.doAfterTextChanged { clearError(binding.nameInputLayout) }
        binding.emailInputLayout.editText?.doAfterTextChanged { clearError(binding.emailInputLayout) }
        binding.passwordInputLayout.editText?.doAfterTextChanged { clearError(binding.passwordInputLayout) }
        binding.confirmPasswordInputLayout.editText?.doAfterTextChanged { clearError(binding.confirmPasswordInputLayout) }

        binding.registerButton.setOnClickListener { attemptRegister() }

        binding.footerLink.setOnClickListener { findNavController().popBackStack() }
    }

    private fun attemptRegister() {
        if (isSubmitting) return
        val name = binding.nameInputLayout.editText?.text?.toString()?.trim().orEmpty()
        val email = binding.emailInputLayout.editText?.text?.toString()?.trim().orEmpty()
        val password = binding.passwordInputLayout.editText?.text?.toString().orEmpty()
        val confirm = binding.confirmPasswordInputLayout.editText?.text?.toString().orEmpty()

        when {
            name.isEmpty() -> {
                showFieldError(binding.nameInputLayout, getString(R.string.auth_error_name_empty)); return
            }
            email.isEmpty() -> {
                showFieldError(binding.emailInputLayout, getString(R.string.auth_error_email_empty)); return
            }
            !Validators.isValidEmail(email) -> {
                showFieldError(binding.emailInputLayout, getString(R.string.auth_error_email_invalid)); return
            }
            password.isEmpty() -> {
                showFieldError(binding.passwordInputLayout, getString(R.string.auth_error_password_empty)); return
            }
            !Validators.isValidPassword(password) -> {
                showFieldError(binding.passwordInputLayout, getString(R.string.auth_error_password_short)); return
            }
            confirm.isEmpty() -> {
                showFieldError(binding.confirmPasswordInputLayout, getString(R.string.auth_error_confirm_empty)); return
            }
            password != confirm -> {
                showFieldError(binding.confirmPasswordInputLayout, getString(R.string.auth_error_confirm_mismatch)); return
            }
            !binding.termsCheckbox.isChecked -> {
                Snackbar.make(binding.root, getString(R.string.auth_error_terms), Snackbar.LENGTH_LONG).show(); return
            }
        }

        setSubmitting(true)
        viewModel.register(name, email, password) { outcome ->
            if (_binding == null) return@register
            setSubmitting(false)
            when (outcome) {
                is AuthOutcome.Success -> {
                    // Log the freshly created account in and continue to the app.
                    SessionManager.saveSession(
                        requireContext(), outcome.user.id, outcome.user.name, outcome.user.email
                    )
                    findNavController().navigate(R.id.action_registerFragment_to_homeDashboardFragment)
                }
                is AuthOutcome.Failure -> {
                    if (outcome.field == AuthField.EMAIL) {
                        showFieldError(binding.emailInputLayout, outcome.message)
                    } else {
                        Snackbar.make(binding.root, outcome.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setSubmitting(submitting: Boolean) {
        isSubmitting = submitting
        binding.registerButton.isEnabled = !submitting
        binding.registerButton.text =
            getString(if (submitting) R.string.register_loading else R.string.register_cta)
    }

    private fun showFieldError(til: TextInputLayout, message: String) {
        til.setBackgroundResource(R.drawable.bg_input_field_error)
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun clearError(til: TextInputLayout) {
        til.setBackgroundResource(R.drawable.bg_input_field_compact)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
