package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.RetrofitInstance
import com.example.cyberlearnapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserProgress(
    val userName: String = "Estudiante",
    val userEmail: String = "",
    val totalXp: Int = 0,
    val currentStreak: Int = 0,
    val streakBonus: Int = 0,
    val badgesCount: Int = 0,
    val level: Int = 1,
    val completedLessons: Int = 0,
    val completedCourses: Int = 0,
    val nextLevelXp: Int = 100,
    val progressPercentage: Double = 0.0,
    val coursesProgress: List<CourseProgress> = emptyList()
)

data class CourseProgress(
    val courseId: String,
    val courseTitle: String,
    val completedLessons: Int,
    val totalLessons: Int,
    val progressPercent: Double
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProgress = MutableStateFlow<UserProgress?>(null)
    val userProgress: StateFlow<UserProgress?> = _userProgress.asStateFlow()

    private val _userBadges = MutableStateFlow<List<com.example.cyberlearnapp.network.Badge>>(emptyList())
    val userBadges: StateFlow<List<com.example.cyberlearnapp.network.Badge>> = _userBadges.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadUserProgress() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                println("🔑 [DEBUG] Cargando progreso del usuario...")
                val token = userRepository.getToken().first()

                if (token == null || token.isEmpty()) {
                    _errorMessage.value = "No autenticado - Token vacío o nulo"
                    _isLoading.value = false
                    return@launch
                }

                val response = RetrofitInstance.api.getUserDashboard("Bearer $token")
                println("📥 [DEBUG] Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val dashboardResponse = response.body()!!
                    val dashboard = dashboardResponse.dashboard

                    if (dashboardResponse.success && dashboard != null) {
                        _userProgress.value = UserProgress(
                            totalXp = dashboard.total_xp ?: 0,
                            currentStreak = dashboard.current_streak ?: 0,
                            streakBonus = dashboard.streak_bonus ?: 0,
                            badgesCount = dashboard.badges_count ?: 0,
                            coursesProgress = dashboard.courses_progress?.map { courseProgress ->
                                CourseProgress(
                                    courseId = courseProgress.course_id ?: "",
                                    courseTitle = courseProgress.course_title ?: "",
                                    completedLessons = courseProgress.completed_lessons ?: 0,
                                    totalLessons = courseProgress.total_lessons ?: 0,
                                    progressPercent = courseProgress.progress_percent ?: 0.0
                                )
                            } ?: emptyList()
                        )
                        println("✅ [DEBUG] Progreso cargado: ${_userProgress.value}")
                    } else {
                        _errorMessage.value = "Error en la respuesta del servidor"
                    }
                } else {
                    _errorMessage.value = "Error HTTP ${response.code()}: ${response.message()}"
                }

            } catch (e: Exception) {
                println("💥 [DEBUG] Error: ${e.message}")
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserBadges() {
        viewModelScope.launch {
            println("🛡️ [DEBUG] Cargando badges...")
            try {
                val token = userRepository.getToken().first()

                if (token == null || token.isEmpty()) {
                    println("❌ [DEBUG-BADGES] Token vacío")
                    return@launch
                }

                val response = RetrofitInstance.api.getUserBadges("Bearer $token")
                println("📥 [DEBUG-BADGES] Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val badgeResponse = response.body()!!
                    if (badgeResponse.success) {
                        val badges = badgeResponse.badges ?: emptyList()
                        println("✅ [DEBUG-BADGES] Badges cargados: ${badges.size}")
                        _userBadges.value = badges
                    }
                } else {
                    println("❌ [DEBUG-BADGES] Error: ${response.code()}")
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

            println("🔑 Token: ${token?.let { "LONGITUD: ${it.length}" } ?: "NULL"}")
            println("👤 User: $user")
            println("📊 Progress: ${_userProgress.value}")
            println("=== 🏁 FIN DEBUG ===")
        }
    }

    fun loadUserDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                println("🔑 [DEBUG] Cargando progreso del usuario...")
                val token = userRepository.getToken().first()
                val userData = userRepository.getUserData().first()

                if (token == null || token.isEmpty()) {
                    _errorMessage.value = "No autenticado - Token vacío o nulo"
                    _isLoading.value = false
                    return@launch
                }

                val response = RetrofitInstance.api.getUserDashboard("Bearer $token")
                println("📥 [DEBUG] Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val dashboardResponse = response.body()!!
                    val dashboard = dashboardResponse.dashboard

                    if (dashboardResponse.success && dashboard != null) {
                        // ✅ CALCULAR CAMPOS FALTANTES
                        val totalXp = dashboard.total_xp ?: 0
                        val level = calculateLevel(totalXp)
                        val nextLevelXp = calculateNextLevelXp(totalXp)
                        val progressPercentage = calculateProgressPercentage(totalXp)
                        val completedLessons = calculateCompletedLessons(dashboard.courses_progress ?: emptyList())
                        val completedCourses = calculateCompletedCourses(dashboard.courses_progress ?: emptyList())

                        _userProgress.value = UserProgress(
                            userName = userData?.name ?: "Estudiante",
                            userEmail = userData?.email ?: "",
                            totalXp = totalXp,
                            currentStreak = dashboard.current_streak ?: 0,
                            streakBonus = dashboard.streak_bonus ?: 0,
                            badgesCount = dashboard.badges_count ?: 0,
                            level = level,
                            completedLessons = completedLessons,
                            completedCourses = completedCourses,
                            nextLevelXp = nextLevelXp,
                            progressPercentage = progressPercentage,
                            coursesProgress = dashboard.courses_progress?.map { courseProgress ->
                                CourseProgress(
                                    courseId = courseProgress.course_id ?: "",
                                    courseTitle = courseProgress.course_title ?: "",
                                    completedLessons = courseProgress.completed_lessons ?: 0,
                                    totalLessons = courseProgress.total_lessons ?: 0,
                                    progressPercent = courseProgress.progress_percent ?: 0.0
                                )
                            } ?: emptyList()
                        )
                        println("✅ [DEBUG] Progreso cargado: ${_userProgress.value}")
                    } else {
                        _errorMessage.value = "Error en la respuesta del servidor"
                    }
                } else {
                    _errorMessage.value = "Error HTTP ${response.code()}: ${response.message()}"
                }

            } catch (e: Exception) {
                println("💥 [DEBUG] Error: ${e.message}")
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 🔍 FUNCIÓN PARA PROBAR TOKEN MANUALMENTE
    fun testTokenManually(testToken: String) {
        viewModelScope.launch {
            println("🧪 [TEST] Probando token manual: ${testToken.take(30)}...")
            try {
                val response = RetrofitInstance.api.getUserDashboard("Bearer $testToken")
                println("🧪 [TEST] Response code: ${response.code()}")
                println("🧪 [TEST] Response body: ${response.body()}")
            } catch (e: Exception) {
                println("🧪 [TEST] Error: ${e.message}")
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun calculateLevel(totalXp: Int): Int = (totalXp / 100) + 1

    private fun calculateNextLevelXp(totalXp: Int): Int {
        val currentLevel = calculateLevel(totalXp)
        return currentLevel * 100
    }

    private fun calculateProgressPercentage(totalXp: Int): Double {
        val currentLevel = calculateLevel(totalXp)
        val xpInCurrentLevel = totalXp % 100
        return (xpInCurrentLevel / 100.0) * 100
    }

    private fun calculateCompletedLessons(coursesProgress: List<com.example.cyberlearnapp.network.CourseProgress>): Int {
        return coursesProgress.sumOf { it.completed_lessons ?: 0 }
    }

    private fun calculateCompletedCourses(coursesProgress: List<com.example.cyberlearnapp.network.CourseProgress>): Int {
        return coursesProgress.count { (it.progress_percent ?: 0.0) >= 100.0 }
    }
}