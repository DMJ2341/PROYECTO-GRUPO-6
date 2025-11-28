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

data class DashboardState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val userXp: Int = 0,
    val userLevel: Int = 1,
    val badges: List<Badge> = emptyList(),
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
        refreshDashboard()
        loadDailyTerm()
    }

    // ✅ PÚBLICO: Para ser llamado desde DashboardScreen con ON_RESUME
    fun refreshDashboard() {
        viewModelScope.launch {
            // No ponemos isLoading = true para evitar parpadeos en refresh
            try {
                val response = userRepo.getDashboard()
                val data = response.dashboard

                _state.value = _state.value.copy(
                    userXp = data.totalXp,
                    userLevel = data.level,
                    hasPreferenceResult = data.hasPreferenceResult,
                    completedCourses = data.completedCourses,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun loadDailyTerm() {
        viewModelScope.launch {
            try {
                val dailyTermWrapper = userRepo.getDailyTerm()
                _state.value = _state.value.copy(dailyTerm = dailyTermWrapper)
            } catch (e: Exception) {
                // Error silencioso para daily term
            }
        }
    }
}