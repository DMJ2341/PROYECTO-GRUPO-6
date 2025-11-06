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
                // 🔍 DEBUG 1: OBTENER TOKEN
                println("🔑 [DEBUG] === INICIANDO CARGA DE PROGRESO ===")
                val token = userRepository.getToken().first()

                // 🔍 DEBUG 2: VERIFICAR TOKEN
                println("🔑 [DEBUG] Token leído de DataStore: ${token?.let {
                    "LONGITUD: ${it.length} -> ${it.take(30)}..."
                } ?: "NULL"}")

                if (token == null || token.isEmpty()) {
                    _errorMessage.value = "No autenticado - Token vacío o nulo"
                    _isLoading.value = false
                    println("❌ [DEBUG] Token es null o vacío - ABORTANDO")
                    return@launch
                }

                // 🔍 DEBUG 3: PREPARAR HEADER
                val authHeader = "Bearer $token"
                println("📤 [DEBUG] Header completo: $authHeader")
                println("📤 [DEBUG] Longitud header: ${authHeader.length}")
                println("📤 [DEBUG] Inicio del token: ${token.take(50)}...")

                // 🔍 DEBUG 4: HACER LA PETICIÓN
                println("🌐 [DEBUG] Haciendo request a /api/user/progress...")
                val response = apiService.getUserProgress(authHeader)

                // 🔍 DEBUG 5: ANALIZAR RESPUESTA
                println("📥 [DEBUG] Response code: ${response.code()}")
                println("📥 [DEBUG] Response isSuccessful: ${response.isSuccessful}")
                println("📥 [DEBUG] Response headers: ${response.headers()}")

                if (response.isSuccessful) {
                    val progressData = response.body()
                    println("✅ [DEBUG] Progreso cargado exitosamente: $progressData")
                    _userProgress.value = progressData
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("❌ [DEBUG] Error HTTP ${response.code()}: $errorBody")

                    when (response.code()) {
                        401 -> _errorMessage.value = "Error 401: No autorizado - Token inválido o expirado"
                        403 -> _errorMessage.value = "Error 403: Prohibido - Sin permisos"
                        404 -> _errorMessage.value = "Error 404: Recurso no encontrado"
                        500 -> _errorMessage.value = "Error 500: Error interno del servidor"
                        else -> _errorMessage.value = "Error ${response.code()}: $errorBody"
                    }
                }

            } catch (e: Exception) {
                println("💥 [DEBUG] EXCEPCIÓN: ${e.message}")
                println("💥 [DEBUG] Stack trace:")
                e.printStackTrace()

                _errorMessage.value = when {
                    e.message?.contains("Unable to resolve host") == true ->
                        "Error de conexión: No se puede conectar al servidor"
                    e.message?.contains("timeout") == true ->
                        "Error de conexión: Timeout del servidor"
                    else -> "Error de conexión: ${e.message}"
                }
            } finally {
                _isLoading.value = false
                println("🏁 [DEBUG] === FIN CARGA DE PROGRESO ===")
            }
        }
    }

    fun loadUserBadges() {
        viewModelScope.launch {
            println("🛡️ [DEBUG] Cargando badges...")
            try {
                val token = userRepository.getToken().first()

                println("🔑 [DEBUG-BADGES] Token: ${token?.let { "LONGITUD: ${it.length}" } ?: "NULL"}")

                if (token == null || token.isEmpty()) {
                    println("❌ [DEBUG-BADGES] Token vacío - No se cargan badges")
                    return@launch
                }

                val response = apiService.getUserBadges("Bearer $token")
                println("📥 [DEBUG-BADGES] Response code: ${response.code()}")

                if (response.isSuccessful) {
                    val badges = response.body()?.badges ?: emptyList()
                    println("✅ [DEBUG-BADGES] Badges cargados: ${badges.size}")
                    _userBadges.value = badges
                } else {
                    println("❌ [DEBUG-BADGES] Error cargando badges: ${response.code()}")
                }
            } catch (e: Exception) {
                println("💥 [DEBUG-BADGES] Error: ${e.message}")
            }
        }
    }

    // 🔍 FUNCIÓN DE DEBUG TEMPORAL
    fun debugAuthStatus() {
        viewModelScope.launch {
            println("=== 🔍 DEBUG AUTH STATUS ===")
            val token = userRepository.getToken().first()
            val user = userRepository.getUserData().first()

            println("🔑 Token en DataStore: ${token?.let {
                "LONGITUD: ${it.length} -> ${it.take(20)}..."
            } ?: "NULL"}")

            println("👤 User en DataStore: $user")
            println("📱 User en ViewModel: ${_userProgress.value}")
            println("=== 🏁 FIN DEBUG AUTH STATUS ===")
        }
    }

    // 🔍 FUNCIÓN PARA PROBAR TOKEN MANUALMENTE
    fun testTokenManually(testToken: String) {
        viewModelScope.launch {
            println("🧪 [TEST] Probando token manual: ${testToken.take(30)}...")
            try {
                val response = apiService.getUserProgress("Bearer $testToken")
                println("🧪 [TEST] Response code: ${response.code()}")
                println("🧪 [TEST] Response body: ${response.body()}")
            } catch (e: Exception) {
                println("🧪 [TEST] Error: ${e.message}")
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
        println("🧹 [DEBUG] Error limpiado")
    }
}