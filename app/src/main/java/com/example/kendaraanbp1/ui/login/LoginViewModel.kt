package com.example.kendaraanbp1.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kendaraanbp1.data.repository.AuthOutcome
import com.example.kendaraanbp1.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun login(email: String, password: String, onResult: (AuthOutcome) -> Unit) {
        viewModelScope.launch {
            onResult(authRepository.login(email, password))
        }
    }
}
