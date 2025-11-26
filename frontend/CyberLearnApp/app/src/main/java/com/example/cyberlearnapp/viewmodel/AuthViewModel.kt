package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.LoginRequest
import com.example.cyberlearnapp.network.models.RegisterRequest
import com.example.cyberlearnapp.network.models.User
import com.example.cyberlearnapp.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        val token = AuthManager.getToken()
        if (!token.isNullOrEmpty()) {
            // 1. Indicamos que estamos cargando para mostrar spinner si es necesario
            _authState.value = AuthState.Loading

            viewModelScope.launch {
                try {
                    // 2. Validamos el token obteniendo el perfil real del usuario
                    // Añadimos "Bearer " porque el backend lo espera así
                    val response = apiService.getUserProfile("Bearer $token")

                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        // 3. Sesión válida: Guardamos el usuario real
                        _currentUser.value = data.user
                        _authState.value = AuthState.Success
                    } else {
                        // 4. Token inválido o expirado: Limpiamos sesión
                        handleSessionError()
                    }
                } catch (e: Exception) {
                    // Error de red (sin internet, servidor caído)
                    // Opción A: Dejar pasar si quieres soporte offline (requiere base de datos local)
                    // Opción B (Más segura): Pedir login de nuevo
                    _authState.value = AuthState.Error("No se pudo validar la sesión: ${e.message}")
                }
            }
        } else {
            _authState.value = AuthState.Idle
        }
    }

    private fun handleSessionError() {
        AuthManager.clear()
        _currentUser.value = null
        _authState.value = AuthState.Idle // Esto debería llevar al LoginScreen
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.login(LoginRequest(email, pass))
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    AuthManager.saveToken(data.accessToken)
                    _currentUser.value = data.user
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Credenciales inválidas")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.register(RegisterRequest(email, pass, name))
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    AuthManager.saveToken(data.accessToken)
                    _currentUser.value = data.user
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Error en registro: ${response.code()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun resetNavigation() {
        _authState.value = AuthState.Idle
    }

    fun logout() {
        handleSessionError()
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}