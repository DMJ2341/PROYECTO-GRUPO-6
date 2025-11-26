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
                val data = userRepo.getDashboard()
                _state.value = _state.value.copy(
                    userXp = data.totalXp,
                    userLevel = data.level,
                    badges = data.badges,
                    hasPreferenceResult = data.preferenceResult != null,
                    completedCourses = data.coursesCompleted,
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

data class DashboardState(
    val userXp: Int = 0,
    val userLevel: Int = 1,
    val badges: List<Badge> = emptyList(),
    val dailyTerm: DailyTermWrapper? = null,
    val hasPreferenceResult: Boolean = false,
    val completedCourses: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)