package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.cyberlearnapp.repository.UserRepository
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ✅ SOLUCIÓN: Estado único para navegación inmediata
    private val _shouldNavigateToMain = MutableStateFlow(false)
    val shouldNavigateToMain: StateFlow<Boolean> = _shouldNavigateToMain.asStateFlow()

    init {
        loadStoredUser()
    }

    fun loadStoredUser() {
        viewModelScope.launch {
            userRepository.getUserData().collect { user ->
                _currentUser.value = user
                println("👤 [DEBUG] Usuario cargado desde DataStore: $user")
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        _isLoading.value = true
        _errorMessage.value = null
        _shouldNavigateToMain.value = false

        viewModelScope.launch {
            try {
                println("🔐 [DEBUG] Iniciando registro para: $email")
                val response = apiService.register(
                    com.example.cyberlearnapp.network.RegisterRequest(email, password, name)
                )

                println("📡 [DEBUG] Response code registro: ${response.code()}")
                println("📡 [DEBUG] Response body registro: ${response.body()}")

                if (response.isSuccessful && response.body()?.success == true) {
                    val userData = response.body()?.user
                    val token = response.body()?.token ?: ""

                    println("✅ [DEBUG] Token recibido registro: ${if (token.isNotEmpty()) "LONGITUD: ${token.length}" else "VACÍO"}")
                    println("✅ [DEBUG] User data registro: $userData")

                    if (userData != null && token.isNotEmpty()) {
                        userRepository.saveLoginData(token, userData)
                        println("💾 [DEBUG] Datos de registro guardados en DataStore")

                        // ✅ SOLUCIÓN: Actualizar currentUser y activar navegación simultáneamente
                        _currentUser.value = userData

                        // ✅ CRÍTICO: Primero desactivar loading, LUEGO activar navegación
                        _isLoading.value = false

                        // Pequeño delay para asegurar que la UI actualizó el loading
                        kotlinx.coroutines.delay(50)

                        _shouldNavigateToMain.value = true

                        debugAuthStatus()
                    } else {
                        println("❌ [DEBUG] Registro: UserData null o token vacío")
                        _errorMessage.value = "Error: Token vacío recibido del servidor"
                        _isLoading.value = false
                    }
                } else {
                    val errorMsg = response.body()?.message ?: "Error en el registro"
                    println("❌ [DEBUG] Registro fallido: $errorMsg")
                    _errorMessage.value = errorMsg
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                println("💥 [DEBUG] Excepción en registro: ${e.message}")
                _errorMessage.value = "Error de conexión: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null
        _shouldNavigateToMain.value = false

        viewModelScope.launch {
            try {
                println("🔐 [DEBUG] Iniciando login para: $email")
                val response = apiService.login(
                    com.example.cyberlearnapp.network.LoginRequest(email, password)
                )

                println("📡 [DEBUG] Login response code: ${response.code()}")
                println("📡 [DEBUG] Login response body: ${response.body()}")

                if (response.isSuccessful && response.body()?.success == true) {
                    val userData = response.body()?.user
                    val token = response.body()?.token ?: ""

                    println("✅ [DEBUG] Token recibido login: ${if (token.isNotEmpty()) "LONGITUD: ${token.length} -> ${token.take(30)}..." else "VACÍO"}")
                    println("✅ [DEBUG] User data login: $userData")

                    if (userData != null && token.isNotEmpty()) {
                        userRepository.saveLoginData(token, userData)
                        println("💾 [DEBUG] Datos de login guardados en DataStore")

                        // ✅ SOLUCIÓN: Actualizar currentUser y activar navegación simultáneamente
                        _currentUser.value = userData

                        // ✅ CRÍTICO: Primero desactivar loading, LUEGO activar navegación
                        _isLoading.value = false

                        // Pequeño delay para asegurar que la UI actualizó el loading
                        kotlinx.coroutines.delay(50)

                        _shouldNavigateToMain.value = true

                        debugAuthStatus()
                    } else {
                        println("❌ [DEBUG] Login: UserData null o token vacío")
                        _errorMessage.value = "Error: Token vacío recibido del servidor"
                        _isLoading.value = false
                    }
                } else {
                    val errorMsg = response.body()?.message ?: "Error en el login"
                    println("❌ [DEBUG] Login fallido: $errorMsg")
                    _errorMessage.value = errorMsg
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                println("💥 [DEBUG] Excepción en login: ${e.message}")
                _errorMessage.value = "Error de conexión: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun resetNavigation() {
        _shouldNavigateToMain.value = false
    }

    fun logout() {
        viewModelScope.launch {
            println("🚪 [DEBUG] Cerrando sesión...")
            userRepository.clearLoginData()
            _currentUser.value = null
            _shouldNavigateToMain.value = false
            _isLoading.value = false
            _errorMessage.value = null
            println("✅ [DEBUG] Sesión cerrada")
        }
    }

    private fun debugAuthStatus() {
        viewModelScope.launch {
            println("=== 🔍 AUTH DEBUG ===")
            val token = userRepository.getToken().first()
            val user = userRepository.getUserData().first()

            println("🔑 Token guardado: ${token?.let { "LONGITUD: ${it.length}" } ?: "NULL"}")
            println("👤 User guardado: $user")
            println("=== 🏁 FIN AUTH DEBUG ===")
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}