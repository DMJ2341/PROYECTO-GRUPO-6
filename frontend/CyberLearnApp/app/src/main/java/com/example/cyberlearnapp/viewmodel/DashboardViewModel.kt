package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.models.DailyTermWrapper
import com.example.cyberlearnapp.network.models.Badge
import com.example.cyberlearnapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Asumimos que este estado ya está definido correctamente en DashboardModels.kt o aquí.
data class DashboardState(
    val isLoading: Boolean = true,
    val error: String? = null,

    val userXp: Int = 0,
    val userLevel: Int = 1,
    val badges: List<Badge> = emptyList(), // Esta lista suele ser poblada por otro repo o está en DashboardResponse
    val dailyTerm: DailyTermWrapper? = null,
    val hasPreferenceResult: Boolean = false,
    val completedCourses: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userRepo: UserRepository
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
                // userRepo.getDashboard() devuelve DashboardResponse
                val response = userRepo.getDashboard()

                // ✅ CORRECCIÓN 1: Acceder a la propiedad 'dashboard'
                val data = response.dashboard

                _state.value = _state.value.copy(
                    // ✅ CORRECCIÓN 2: Usar los nombres de propiedades correctos del modelo
                    userXp = data.totalXp,
                    userLevel = data.level,
                    // Se asume que data.badgesCount es usado en otro lugar o se usa una lista vacía
                    // La propiedad badges aquí debe ser poblada por un BadgeRepository o estar en el Summary.
                    hasPreferenceResult = data.hasPreferenceResult,
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
                // ✅ Se usa el repositorio (asumiendo que tiene la lógica de token)
                val dailyTermWrapper = userRepo.getDailyTerm()
                _state.value = _state.value.copy(dailyTerm = dailyTermWrapper)
            } catch (e: Exception) {
                // Error silencioso para no romper el dashboard
            }
        }
    }
}