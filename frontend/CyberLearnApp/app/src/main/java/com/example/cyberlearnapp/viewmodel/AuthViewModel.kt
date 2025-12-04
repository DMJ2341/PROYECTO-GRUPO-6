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

    private val _verificationEmail = MutableStateFlow<String?>(null)
    val verificationEmail: StateFlow<String?> = _verificationEmail.asStateFlow()

    private val _recoveryState = MutableStateFlow<RecoveryState>(RecoveryState.Idle)
    val recoveryState: StateFlow<RecoveryState> = _recoveryState.asStateFlow()

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

                    if (data.requiresVerification) {
                        _verificationEmail.value = email
                        _authState.value = AuthState.RequiresVerification(email)
                    } else {
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

    // ==========================================
    // 🔐 RECUPERACIÓN DE CONTRASEÑA
    // ==========================================

    fun sendRecoveryEmail(email: String) {
        viewModelScope.launch {
            _recoveryState.value = RecoveryState.Loading
            try {
                val response = apiService.forgotPassword(mapOf("email" to email))
                if (response.isSuccessful) {
                    _recoveryState.value = RecoveryState.EmailSent(email)
                } else {
                    _recoveryState.value = RecoveryState.Error("No se pudo enviar el correo. Verifica que exista.")
                }
            } catch (e: Exception) {
                _recoveryState.value = RecoveryState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun validateRecoveryToken(email: String, token: String) {
        viewModelScope.launch {
            _recoveryState.value = RecoveryState.Loading
            try {
                val response = apiService.validateResetToken(mapOf("email" to email, "token" to token))
                if (response.isSuccessful) {
                    _recoveryState.value = RecoveryState.TokenValid
                } else {
                    _recoveryState.value = RecoveryState.Error("Código inválido o expirado")
                }
            } catch (e: Exception) {
                _recoveryState.value = RecoveryState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun resetPassword(email: String, token: String, newPass: String) {
        viewModelScope.launch {
            _recoveryState.value = RecoveryState.Loading
            try {
                val response = apiService.resetPassword(
                    mapOf(
                        "email" to email,
                        "token" to token,
                        "new_password" to newPass
                    )
                )
                if (response.isSuccessful) {
                    _recoveryState.value = RecoveryState.PasswordResetSuccess
                } else {
                    _recoveryState.value = RecoveryState.Error("No se pudo cambiar la contraseña")
                }
            } catch (e: Exception) {
                _recoveryState.value = RecoveryState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun resetRecoveryFlow() {
        _recoveryState.value = RecoveryState.Idle
    }

    fun resetNavigation() {
        _authState.value = AuthState.Idle
    }

    fun logout() {
        handleSessionError()
    }
}

// ==========================================
// SEALED CLASSES
// ==========================================

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class RequiresVerification(val email: String) : AuthState()
    object CodeResent : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class RecoveryState {
    object Idle : RecoveryState()
    object Loading : RecoveryState()
    data class EmailSent(val email: String) : RecoveryState()
    object TokenValid : RecoveryState()
    object PasswordResetSuccess : RecoveryState()
    data class Error(val message: String) : RecoveryState()
}