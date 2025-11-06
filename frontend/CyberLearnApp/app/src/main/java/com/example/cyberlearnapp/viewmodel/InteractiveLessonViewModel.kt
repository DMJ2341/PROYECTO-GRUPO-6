package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.RetrofitInstance
import com.example.cyberlearnapp.network.models.InteractiveLesson
import com.example.cyberlearnapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                val token = userRepository.getToken().first() ?: ""
                if (token.isEmpty()) {
                    _state.value = _state.value.copy(
                        errorMessage = "No autenticado",
                        isLoading = false
                    )
                    return@launch
                }

                val response = RetrofitInstance.api.getInteractiveLesson(
                    lessonId = lessonId,
                    token = "Bearer $token"
                )

                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        lesson = response.body()!!.lesson,
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    _state.value = _state.value.copy(
                        errorMessage = "Error: ${response.code()}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = e.message ?: "Error cargando lección",
                    isLoading = false
                )
            }
        }
    }

    suspend fun getToken(): String {
        return userRepository.getToken().first() ?: ""
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}

data class InteractiveLessonState(
    val lesson: InteractiveLesson? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)