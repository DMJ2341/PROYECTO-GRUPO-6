package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.*
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

    // ✅ NUEVO: Estado de verificación de email
    private val _verificationEmail = MutableStateFlow<String?>(null)
    val verificationEmail: StateFlow<String?> = _verificationEmail.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        val token = AuthManager.getToken()
        if (!token.isNullOrEmpty()) {
            _authState.value = AuthState.Loading

            viewModelScope.launch {
                try {
                    val response = apiService.getUserProfile("Bearer $token")

                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        _currentUser.value = data.user
                        _authState.value = AuthState.Success
                    } else {
                        handleSessionError()
                    }
                } catch (e: Exception) {
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
        _authState.value = AuthState.Idle
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.login(LoginRequest(email, pass))
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    AuthManager.saveToken(data.accessToken)
                    AuthManager.saveRefreshToken(data.refreshToken)
                    _currentUser.value = data.user
                    _authState.value = AuthState.Success
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Credenciales inválidas"
                        403 -> "Debes verificar tu email antes de iniciar sesión"
                        else -> "Error en el login"
                    }
                    _authState.value = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun register(name: String, email: String, pass: String, termsAccepted: Boolean) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            // Validaciones en el cliente
            if (!termsAccepted) {
                _authState.value = AuthState.Error("Debes aceptar los términos y condiciones")
                return@launch
            }

            if (name.isBlank()) {
                _authState.value = AuthState.Error("El nombre es requerido")
                return@launch
            }

            if (pass.length < 8) {
                _authState.value = AuthState.Error("La contraseña debe tener al menos 8 caracteres")
                return@launch
            }

            try {
                val response = apiService.register(RegisterRequest(email, pass, name))
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!

                    // ✅ NUEVO: Verificar si requiere verificación
                    if (data.requiresVerification) {
                        _verificationEmail.value = email
                        _authState.value = AuthState.RequiresVerification(email)
                    } else {
                        // Flujo antiguo (por si el backend no envía código)
                        if (data.accessToken != null && data.refreshToken != null) {
                            AuthManager.saveToken(data.accessToken)
                            AuthManager.saveRefreshToken(data.refreshToken)
                            _currentUser.value = data.user
                            _authState.value = AuthState.Success
                        }
                    }
                } else {
                    _authState.value = AuthState.Error("Error en registro: ${response.message()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    // ✅ NUEVO: Verificar código de email
    fun verifyEmail(email: String, code: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.verifyEmail(VerifyEmailRequest(email, code))
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    AuthManager.saveToken(data.accessToken)
                    AuthManager.saveRefreshToken(data.refreshToken)
                    _currentUser.value = data.user
                    _verificationEmail.value = null
                    _authState.value = AuthState.Success
                } else {
                    val errorMsg = when (response.code()) {
                        400 -> "Código inválido o expirado"
                        404 -> "Usuario no encontrado"
                        else -> "Error verificando email"
                    }
                    _authState.value = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    // ✅ NUEVO: Reenviar código
    fun resendCode(email: String) {
        viewModelScope.launch {
            try {
                val response = apiService.resendVerificationCode(ResendCodeRequest(email))
                if (response.isSuccessful) {
                    _authState.value = AuthState.CodeResent
                } else {
                    _authState.value = AuthState.Error("No se pudo reenviar el código")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error reenviando código")
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
    data class RequiresVerification(val email: String) : AuthState()  // ✅ NUEVO
    object CodeResent : AuthState()  // ✅ NUEVO
    data class Error(val message: String) : AuthState()
}