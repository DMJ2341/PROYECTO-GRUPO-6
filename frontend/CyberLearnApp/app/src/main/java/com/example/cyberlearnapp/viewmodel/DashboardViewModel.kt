package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.models.CourseProgress
import com.example.cyberlearnapp.network.models.DailyTermWrapper
import com.example.cyberlearnapp.network.models.GlossaryStats
import com.example.cyberlearnapp.repository.GlossaryRepository
import com.example.cyberlearnapp.repository.UserRepository
import com.example.cyberlearnapp.utils.RefreshEvent
import com.example.cyberlearnapp.utils.RefreshEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val isLoading: Boolean = true,
    val error: String? = null,

    // Datos del Usuario
    val userXp: Int = 0,
    val userLevel: Int = 1,
    val currentStreak: Int = 0,
    val badgesCount: Int = 0,

    // Contenido
    val dailyTerm: DailyTermWrapper? = null,
    val coursesProgress: List<CourseProgress> = emptyList(),

    // Progreso General
    val completedCourses: Int = 0,
    val totalCourses: Int = 0,

    // Glosario Stats
    val glossaryStats: GlossaryStats? = null,

    // Flags de Estado
    val hasPreferenceResult: Boolean = false,
    val finalExamPassed: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val glossaryRepo: GlossaryRepository,
    private val refreshEventBus: RefreshEventBus
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        loadAllData()
        listenToRefreshEvents()
    }

    private fun listenToRefreshEvents() {
        viewModelScope.launch {
            refreshEventBus.refreshEvent.collect { event ->
                when (event) {
                    RefreshEvent.Dashboard -> {
                        refreshDashboard()
                    }
                    else -> { /* Otros eventos */ }
                }
            }
        }
    }

    fun loadAllData() {
        refreshDashboard()
        loadDailyTerm()
        loadGlossaryStats()
    }

    fun refreshDashboard() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val response = userRepo.getDashboard()
                val data = response.dashboard

                _state.value = _state.value.copy(
                    userXp = data.totalXp,
                    userLevel = data.level,
                    currentStreak = data.currentStreak,
                    badgesCount = data.badgesCount,
                    hasPreferenceResult = data.hasPreferenceResult,
                    completedCourses = data.completedCourses,
                    totalCourses = data.totalCourses,
                    coursesProgress = data.coursesProgress,
                    finalExamPassed = data.finalExamPassed,
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
                // Error silencioso
            }
        }
    }

    private fun loadGlossaryStats() {
        viewModelScope.launch {
            try {
                val stats = glossaryRepo.getStats()
                _state.value = _state.value.copy(glossaryStats = stats)
            } catch (e: Exception) {
                // Error silencioso
            }
        }
    }
}