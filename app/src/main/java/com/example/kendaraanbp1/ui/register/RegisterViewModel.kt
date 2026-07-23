package com.example.kendaraanbp1.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kendaraanbp1.data.repository.AuthOutcome
import com.example.kendaraanbp1.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun register(name: String, email: String, password: String, onResult: (AuthOutcome) -> Unit) {
        viewModelScope.launch {
            onResult(authRepository.register(name, email, password))
        }
    }
}
