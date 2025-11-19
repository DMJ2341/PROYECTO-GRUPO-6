package com.example.cyberlearnapp.viewmodel


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.RetrofitInstance
import com.example.cyberlearnapp.network.LessonProgressRequest
import com.example.cyberlearnapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonUiState(
    val lessonTitle: String = "",
    val lessonContent: String = "",
    val isCompleting: Boolean = false,
    val xpEarned: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    fun loadLessonContent(lessonId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitInstance.api.getLessonContent(lessonId)

                if (response.isSuccessful && response.body() != null) {
                    val lesson = response.body()!!
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        lessonTitle = lesson.title,
                        lessonContent = lesson.content ?: "Contenido no disponible" // ✅ Manejar null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error: ${response.code()} - ${response.message()}"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de red: ${e.message}"
                )
            }
        }
    }

    fun completeLesson(lessonId: String, difficulty: Int = 1, onCompleted: (Int) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCompleting = true, errorMessage = null)

            val token = userRepository.getToken().first()
            if (token == null) {
                _uiState.value = _uiState.value.copy(
                    isCompleting = false,
                    errorMessage = "Usuario no autenticado"
                )
                return@launch
            }

            try {
                val response = RetrofitInstance.api.saveLessonProgress(
                    lessonId = lessonId,
                    token = "Bearer $token",
                    progress = LessonProgressRequest(completed = true)
                )

                if (response.isSuccessful) {
                    val xpEarned = 25
                    _uiState.value = _uiState.value.copy(
                        xpEarned = xpEarned,
                        isCompleting = false
                    )
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
}