package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.Lesson
import com.example.cyberlearnapp.network.LessonProgressRequest
import com.example.cyberlearnapp.network.RetrofitInstance
import com.example.cyberlearnapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonDetail(
    val id: String,
    val courseId: Int,
    val title: String,
    val description: String,
    val content: String,
    val type: String,
    val durationMinutes: Int,
    val xpReward: Int,
    val isCompleted: Boolean = false
)

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentLesson = MutableStateFlow<LessonDetail?>(null)
    val currentLesson: StateFlow<LessonDetail?> = _currentLesson.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _lessonCompleted = MutableStateFlow(false)
    val lessonCompleted: StateFlow<Boolean> = _lessonCompleted.asStateFlow()

    fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                println("🔍 [LESSON-VM] Cargando lección: $lessonId")

                val response = RetrofitInstance.api.getLessonContent(lessonId)
                println("📡 [LESSON-VM] Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val lessonData = response.body()!!

                    _currentLesson.value = LessonDetail(
                        id = lessonData.id,
                        courseId = 1, // Puedes obtenerlo del response si está disponible
                        title = lessonData.title,
                        description = lessonData.description ?: "",
                        content = lessonData.content ?: "",
                        type = lessonData.type,
                        durationMinutes = lessonData.duration_minutes,
                        xpReward = lessonData.xp_reward,
                        isCompleted = false
                    )

                    println("✅ [LESSON-VM] Lección cargada: ${lessonData.title}")
                } else {
                    val errorMsg = "Error ${response.code()}: ${response.message()}"
                    println("❌ [LESSON-VM] $errorMsg")
                    _errorMessage.value = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = "Error cargando lección: ${e.message}"
                println("💥 [LESSON-VM] $errorMsg")
                e.printStackTrace()
                _errorMessage.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun completeLesson(lessonId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                println("✅ [LESSON-VM] Completando lección: $lessonId")

                val token = userRepository.getToken().first()
                if (token.isNullOrEmpty()) {
                    println("❌ [LESSON-VM] Token no disponible")
                    _errorMessage.value = "No autenticado"
                    return@launch
                }

                val response = RetrofitInstance.api.updateLessonProgress(
                    lessonId = lessonId,
                    token = "Bearer $token",
                    request = LessonProgressRequest(completed = true)
                )

                println("📡 [LESSON-VM] Progress response: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val progressResponse = response.body()!!
                    if (progressResponse.success) {
                        _lessonCompleted.value = true
                        println("🎉 [LESSON-VM] Lección completada exitosamente. XP ganado: ${progressResponse.points_earned}")
                        onSuccess()
                    } else {
                        _errorMessage.value = "Error al completar lección"
                    }
                } else {
                    val errorMsg = "Error ${response.code()}: ${response.message()}"
                    println("❌ [LESSON-VM] $errorMsg")
                    _errorMessage.value = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = "Error completando lección: ${e.message}"
                println("💥 [LESSON-VM] $errorMsg")
                e.printStackTrace()
                _errorMessage.value = errorMsg
            }
        }
    }

    fun resetCompletionStatus() {
        _lessonCompleted.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }
}