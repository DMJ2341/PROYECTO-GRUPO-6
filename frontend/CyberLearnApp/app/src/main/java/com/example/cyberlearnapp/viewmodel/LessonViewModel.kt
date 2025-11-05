package com.example.cyberlearnapp.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.CompleteActivityRequest
import com.example.cyberlearnapp.repository.UserRepository // <-- 1. IMPORTAR UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first // <-- 2. IMPORTAR flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonUiState(
    val lessonTitle: String = "",
    val lessonContent: String = "",
    val isCompleting: Boolean = false,
    val xpEarned: Int = 0,
    val isLoading: Boolean = false, // <-- 3. Añadir estado de carga
    val errorMessage: String? = null
)

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val apiService: ApiService,
    private val userRepository: UserRepository // <-- 4. Inyectar UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    fun loadLessonContent(lessonId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                // 5. OBTENER EL TOKEN REAL
                val token = userRepository.getToken().first()
                if (token == null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Usuario no autenticado")
                    return@launch
                }

                // 6. LLAMAR A LA API REAL
                val response = apiService.getLessonContent(lessonId, "Bearer $token")

                if (response.isSuccessful && response.body() != null) {
                    val lesson = response.body()!!
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        lessonTitle = lesson.title,
                        lessonContent = lesson.content
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Error al cargar contenido")
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Error de red: ${e.message}")
            }
        }
    }

    fun completeLesson(lessonId: String, difficulty: Int = 1, onCompleted: (Int) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCompleting = true, errorMessage = null)

            // 7. OBTENER EL TOKEN REAL
            val token = userRepository.getToken().first()
            if (token == null) {
                _uiState.value = _uiState.value.copy(isCompleting = false, errorMessage = "Usuario no autenticado")
                return@launch
            }

            try {
                val response = apiService.completeActivity(
                    token = "Bearer $token", // <-- 8. USAR EL TOKEN REAL
                    request = CompleteActivityRequest(
                        type = "lesson_completed",
                        lessonId = lessonId,
                        difficulty = difficulty
                    )
                )

                if (response.isSuccessful) {
                    val xpEarned = response.body()?.activityResult?.xpEarned ?: 0
                    _uiState.value = _uiState.value.copy(xpEarned = xpEarned, isCompleting = false)
                    onCompleted(xpEarned)
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Error al completar lección: ${response.code()}",
                        isCompleting = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error de conexión: ${e.message}",
                    isCompleting = false
                )
            }
        }
    }

    // 9. Eliminar la función private getToken() que estaba hardcodeada
}