package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.Progress
import com.example.cyberlearnapp.network.models.UserBadge
import com.example.cyberlearnapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val apiService: ApiService,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProgress = MutableStateFlow<Progress?>(null)
    val userProgress: StateFlow<Progress?> = _userProgress.asStateFlow()

    private val _userBadges = MutableStateFlow<List<UserBadge>>(emptyList())
    val userBadges: StateFlow<List<UserBadge>> = _userBadges.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadUserProgress() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // ✅ OBTENER TOKEN DEL USUARIO (Forma corregida)
                val token = userRepository.getToken().first() // Llama a la función correcta

                if (token == null) {
                    _errorMessage.value = "No autenticado"
                    _isLoading.value = false
                    return@launch
                }

                println("🔹 UserViewModel - Token: $token")

                // Usa el token real
                val response = apiService.getUserProgress("Bearer $token")

                println("🔹 UserViewModel - Response code: ${response.code()}")

                if (response.isSuccessful) {
                    _userProgress.value = response.body()
                    println("✅ UserViewModel - Progreso cargado")
                } else {
                    _errorMessage.value = "Error cargando progreso: ${response.code()}"
                    println("❌ UserViewModel - Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
                println("❌ UserViewModel - Exception: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserBadges() {
        viewModelScope.launch {
            try {
                // ✅ OBTENER TOKEN DEL USUARIO (Forma corregida)
                val token = userRepository.getToken().first() // Llama a la función correcta

                if (token == null) {
                    return@launch
                }

                // Usa el token real
                val response = apiService.getUserBadges("Bearer $token")
                if (response.isSuccessful) { // La API de badges no tiene un campo "success"
                    _userBadges.value = response.body()?.badges ?: emptyList()
                }
            } catch (e: Exception) {
                println("Error cargando badges: ${e.message}")
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}