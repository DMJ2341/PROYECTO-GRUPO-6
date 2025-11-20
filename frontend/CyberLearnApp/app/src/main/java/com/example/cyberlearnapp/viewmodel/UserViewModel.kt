package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.Badge
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
    val courseId: Int,  // ✅ Cambiado de String a Int
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

    private val _userBadges = MutableStateFlow<List<Badge>>(emptyList())
    val userBadges: StateFlow<List<Badge>> = _userBadges.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadUserProgress() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                println("🔑 [USER-VM] Cargando progreso del usuario...")
                val token = userRepository.getToken().first()

                if (token == null || token.isEmpty()) {
                    _errorMessage.value = "No autenticado - Token vacío o nulo"
                    _isLoading.value = false
                    return@launch
                }

                val response = RetrofitInstance.api.getDashboard("Bearer $token")
                println("📥 [USER-VM] Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val dashboardResponse = response.body()!!
                    val dashboard = dashboardResponse.dashboard

                    if (dashboardResponse.success) {
                        _userProgress.value = UserProgress(
                            totalXp = dashboard.total_xp,
                            currentStreak = dashboard.current_streak,
                            streakBonus = dashboard.streak_bonus,
                            badgesCount = dashboard.badges_count,
                            coursesProgress = dashboard.courses_progress.map { courseProgress ->
                                CourseProgress(
                                    courseId = courseProgress.course_id,
                                    courseTitle = courseProgress.course_title,
                                    completedLessons = courseProgress.completed_lessons,
                                    totalLessons = courseProgress.total_lessons,
                                    progressPercent = courseProgress.progress_percent
                                )
                            }
                        )
                        println("✅ [USER-VM] Progreso cargado: ${_userProgress.value}")
                    } else {
                        _errorMessage.value = "Error en la respuesta del servidor"
                    }
                } else {
                    _errorMessage.value = "Error HTTP ${response.code()}: ${response.message()}"
                }

            } catch (e: Exception) {
                println("💥 [USER-VM] Error: ${e.message}")
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserBadges() {
        viewModelScope.launch {
            println("🛡️ [USER-VM] Cargando badges...")
            try {
                val token = userRepository.getToken().first()

                if (token == null || token.isEmpty()) {
                    println("❌ [USER-VM] Token vacío")
                    return@launch
                }

                val response = RetrofitInstance.api.getUserBadges("Bearer $token")
                println("📥 [USER-VM] Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val badgeResponse = response.body()!!
                    if (badgeResponse.success) {
                        val badges = badgeResponse.badges
                        println("✅ [USER-VM] Badges cargados: ${badges.size}")
                        _userBadges.value = badges
                    }
                } else {
                    println("❌ [USER-VM] Error: ${response.code()}")
                }
            } catch (e: Exception) {
                println("💥 [USER-VM] Error: ${e.message}")
            }
        }
    }

    fun loadUserDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                println("🔑 [USER-VM] Cargando dashboard completo...")
                val token = userRepository.getToken().first()
                val userData = userRepository.getUserData().first()

                if (token == null || token.isEmpty()) {
                    _errorMessage.value = "No autenticado - Token vacío o nulo"
                    _isLoading.value = false
                    return@launch
                }

                val response = RetrofitInstance.api.getDashboard("Bearer $token")
                println("📥 [USER-VM] Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val dashboardResponse = response.body()!!
                    val dashboard = dashboardResponse.dashboard

                    if (dashboardResponse.success) {
                        // ✅ CALCULAR CAMPOS FALTANTES
                        val totalXp = dashboard.total_xp
                        val level = calculateLevel(totalXp)
                        val nextLevelXp = calculateNextLevelXp(totalXp)
                        val progressPercentage = calculateProgressPercentage(totalXp)
                        val completedLessons = calculateCompletedLessons(dashboard.courses_progress)
                        val completedCourses = calculateCompletedCourses(dashboard.courses_progress)

                        _userProgress.value = UserProgress(
                            userName = userData?.name ?: "Estudiante",
                            userEmail = userData?.email ?: "",
                            totalXp = totalXp,
                            currentStreak = dashboard.current_streak,
                            streakBonus = dashboard.streak_bonus,
                            badgesCount = dashboard.badges_count,
                            level = level,
                            completedLessons = completedLessons,
                            completedCourses = completedCourses,
                            nextLevelXp = nextLevelXp,
                            progressPercentage = progressPercentage,
                            coursesProgress = dashboard.courses_progress.map { courseProgress ->
                                CourseProgress(
                                    courseId = courseProgress.course_id,
                                    courseTitle = courseProgress.course_title,
                                    completedLessons = courseProgress.completed_lessons,
                                    totalLessons = courseProgress.total_lessons,
                                    progressPercent = courseProgress.progress_percent
                                )
                            }
                        )
                        println("✅ [USER-VM] Dashboard cargado completamente")
                    } else {
                        _errorMessage.value = "Error en la respuesta del servidor"
                    }
                } else {
                    _errorMessage.value = "Error HTTP ${response.code()}: ${response.message()}"
                }

            } catch (e: Exception) {
                println("💥 [USER-VM] Error: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

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

    fun testTokenManually(testToken: String) {
        viewModelScope.launch {
            println("🧪 [TEST] Probando token manual: ${testToken.take(30)}...")
            try {
                val response = RetrofitInstance.api.getDashboard("Bearer $testToken")
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

    // ========== FUNCIONES DE CÁLCULO ==========

    private fun calculateLevel(totalXp: Int): Int = (totalXp / 100) + 1

    private fun calculateNextLevelXp(totalXp: Int): Int {
        val currentLevel = calculateLevel(totalXp)
        return currentLevel * 100
    }

    private fun calculateProgressPercentage(totalXp: Int): Double {
        val xpInCurrentLevel = totalXp % 100
        return (xpInCurrentLevel / 100.0) * 100
    }

    private fun calculateCompletedLessons(
        coursesProgress: List<com.example.cyberlearnapp.network.CourseProgress>
    ): Int {
        return coursesProgress.sumOf { it.completed_lessons }
    }

    private fun calculateCompletedCourses(
        coursesProgress: List<com.example.cyberlearnapp.network.CourseProgress>
    ): Int {
        return coursesProgress.count { it.progress_percent >= 100.0 }
    }
}