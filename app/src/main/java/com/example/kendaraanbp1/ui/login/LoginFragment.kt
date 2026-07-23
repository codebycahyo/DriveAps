package com.example.kendaraanbp1.ui.login

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
import com.example.kendaraanbp1.databinding.FragmentLoginBinding
import com.example.kendaraanbp1.ui.common.viewmodel.ViewModelFactory
import com.example.kendaraanbp1.util.SessionManager
import com.example.kendaraanbp1.util.Validators
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    private var isSubmitting = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Clear the error highlight as soon as the user edits a field.
        binding.emailInputLayout.editText?.doAfterTextChanged { clearError(binding.emailInputLayout) }
        binding.passwordInputLayout.editText?.doAfterTextChanged { clearError(binding.passwordInputLayout) }

        binding.loginButton.setOnClickListener { attemptLogin() }

        binding.footerLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.forgotPassword.setOnClickListener { showForgotPasswordDialog() }
    }

    private fun attemptLogin() {
        if (isSubmitting) return
        val email = binding.emailInputLayout.editText?.text?.toString()?.trim().orEmpty()
        val password = binding.passwordInputLayout.editText?.text?.toString().orEmpty()

        when {
            email.isEmpty() -> {
                showFieldError(binding.emailInputLayout, getString(R.string.auth_error_email_empty)); return
            }
            !Validators.isValidEmail(email) -> {
                showFieldError(binding.emailInputLayout, getString(R.string.auth_error_email_invalid)); return
            }
            password.isEmpty() -> {
                showFieldError(binding.passwordInputLayout, getString(R.string.auth_error_password_empty)); return
            }
        }

        setSubmitting(true)
        viewModel.login(email, password) { outcome ->
            if (_binding == null) return@login
            setSubmitting(false)
            when (outcome) {
                is AuthOutcome.Success -> {
                    SessionManager.saveSession(
                        requireContext(), outcome.user.id, outcome.user.name, outcome.user.email
                    )
                    findNavController().navigate(R.id.action_loginFragment_to_homeDashboardFragment)
                }
                is AuthOutcome.Failure -> {
                    when (outcome.field) {
                        AuthField.EMAIL -> showFieldError(binding.emailInputLayout, outcome.message)
                        AuthField.PASSWORD -> showFieldError(binding.passwordInputLayout, outcome.message)
                        else -> Snackbar.make(binding.root, outcome.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setSubmitting(submitting: Boolean) {
        isSubmitting = submitting
        binding.loginButton.isEnabled = !submitting
        binding.loginButton.text =
            getString(if (submitting) R.string.login_loading else R.string.login_cta)
    }

    private fun showFieldError(til: TextInputLayout, message: String) {
        til.setBackgroundResource(R.drawable.bg_input_field_error)
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun clearError(til: TextInputLayout) {
        til.setBackgroundResource(R.drawable.bg_input_field)
    }

    private fun showForgotPasswordDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.auth_forgot_title)
            .setMessage(R.string.auth_forgot_offline)
            .setPositiveButton(R.string.auth_forgot_ok, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
