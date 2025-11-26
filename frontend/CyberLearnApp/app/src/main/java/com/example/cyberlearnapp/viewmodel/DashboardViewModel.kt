// app/src/main/java/com/example/cyberlearnapp/viewmodel/DashboardViewModel.kt

package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.models.DailyTermWrapper
import com.example.cyberlearnapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val isLoading: Boolean = true,
    val userXp: Int = 0,
    val userLevel: Int = 1,
    val completedCourses: Int = 0,
    val dailyTerm: DailyTermWrapper? = null,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val dashboard = userRepository.getDashboard()
                val dailyTerm = userRepository.getDailyTerm()

                _state.update {
                    it.copy(
                        isLoading = false,
                        userXp = dashboard.dashboard.total_xp,
                        userLevel = dashboard.dashboard.level,
                        completedCourses = dashboard.dashboard.completed_courses,
                        dailyTerm = dailyTerm
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun completeDailyTerm(termId: Int) {
        if (state.value.dailyTerm?.alreadyViewedToday == true) return

        viewModelScope.launch {
            try {
                val response = userRepository.completeDailyTerm(termId)
                if (response.success) {
                    _state.update { current ->
                        current.copy(
                            userXp = current.userXp + response.xpEarned,
                            userLevel = (current.userXp + response.xpEarned) / 100 + 1
                        )
                    }
                    // Recargar término para actualizar estado
                    loadDashboardData()
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al ganar XP") }
            }
        }
    }
}