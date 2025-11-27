package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.models.DailyTermWrapper
import com.example.cyberlearnapp.network.models.Badge
import com.example.cyberlearnapp.repository.UserRepository
import com.example.cyberlearnapp.network.ApiService // Para daily term
import com.example.cyberlearnapp.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val apiService: ApiService // Inyectamos API directo para daily term por simplicidad
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        loadAllData()
    }

    fun loadAllData() {
        loadDashboard()
        loadDailyTerm()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val response = userRepo.getDashboard() // Devuelve DashboardResponse
                // ✅ CORREGIDO: Se accede a la propiedad 'dashboard'
                val data = response.dashboard

                _state.value = _state.value.copy(
                    // ✅ CORREGIDO: Acceso a propiedades directas de DashboardSummary
                    userXp = data.totalXp,
                    userLevel = data.level,
                    // NOTA: Se omite 'badges' por ahora, ya que el Summary solo tiene 'badgesCount'
                    // o se asume que 'badges' en el state debe ser poblado por otro medio.
                    // Si el estado tiene un campo `badgesCount`, usaríamos: data.badgesCount

                    // ✅ CORREGIDO: Uso de 'hasPreferenceResult' en lugar de 'preferenceResult'
                    hasPreferenceResult = data.hasPreferenceResult,

                    // ✅ CORREGIDO: Uso de 'completedCourses' en lugar de 'coursesCompleted'
                    completedCourses = data.completedCourses,

                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    private fun loadDailyTerm() {
        viewModelScope.launch {
            try {
                // Se asume que este es un flujo de ViewModel temporal, por lo que se usa AuthManager directo.
                val token = "Bearer ${AuthManager.getToken()}"
                val response = apiService.getDailyTerm(token)
                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(dailyTerm = response.body()!!)
                }
            } catch (e: Exception) {
                // Error silencioso para no romper el dashboard
            }
        }
    }
}

// Se asume que este estado fue definido en el archivo o importado de otro lugar,
// pero debe estar presente para que el código anterior compile correctamente.
data class DashboardState(
    val isLoading: Boolean = true,
    val error: String? = null,

    val userXp: Int = 0,
    val userLevel: Int = 1,
    val badges: List<Badge> = emptyList(), // Debe coincidir con el uso en loadDashboard()
    val dailyTerm: DailyTermWrapper? = null,
    val hasPreferenceResult: Boolean = false,
    val completedCourses: Int = 0
)