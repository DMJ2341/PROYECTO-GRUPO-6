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


data class InteractiveScreen(
    val screenNumber: Int,
    val type: String,
    val title: String,
    val content: Map<String, Any> = emptyMap()
)

data class InteractiveLesson(
    val id: String,
    val title: String,
    val totalScreens: Int,
    val screens: List<InteractiveScreen>
)

data class InteractiveLessonState(
    val lesson: InteractiveLesson? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class InteractiveLessonViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InteractiveLessonState())
    val state: StateFlow<InteractiveLessonState> = _state.asStateFlow()

    fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, errorMessage = null)

                val response = RetrofitInstance.api.getLessonContent(lessonId)

                if (response.isSuccessful && response.body() != null) {
                    val lesson = response.body()!!

                    val interactiveLesson = InteractiveLesson(
                        id = lesson.id,
                        title = lesson.title,
                        totalScreens = lesson.total_screens ?: 1,
                        screens = parseScreensFromJson(lesson.screens)
                    )

                    _state.value = _state.value.copy(
                        lesson = interactiveLesson,
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    _state.value = _state.value.copy(
                        errorMessage = "Error: ${response.code()} - ${response.message()}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Error cargando lección: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private fun parseScreensFromJson(screensJson: String?): List<InteractiveScreen> {
        if (screensJson.isNullOrEmpty()) {
            return listOf(
                InteractiveScreen(
                    screenNumber = 1,
                    type = "fallback",
                    title = "Contenido no disponible",
                    content = mapOf("message" to "Esta lección no tiene contenido interactivo")
                )
            )
        }

        // Por ahora retornamos una lista vacía - luego implementamos el parseo real
        return emptyList()
    }

    suspend fun getToken(): String {
        return userRepository.getToken().first() ?: ""
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}