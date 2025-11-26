package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.models.LessonCompletionData
import com.example.cyberlearnapp.network.models.LessonResponse
import com.example.cyberlearnapp.repository.LessonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val repository: LessonRepository
) : ViewModel() {

    private val _lesson = MutableStateFlow<LessonResponse?>(null)
    val lesson: StateFlow<LessonResponse?> = _lesson

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ✅ Estado para notificar a la UI que se completó y ganaste XP
    private val _completionResult = MutableStateFlow<LessonCompletionData?>(null)
    val completionResult: StateFlow<LessonCompletionData?> = _completionResult

    fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.getLesson(lessonId)
                _lesson.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
                _lesson.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun completeLesson(lessonId: String) {
        viewModelScope.launch {
            try {
                // No ponemos loading aquí para no interrumpir la UI bruscamente, o usamos uno sutil
                val result = repository.markLessonComplete(lessonId)
                _completionResult.value = result // ✅ Dispara el diálogo en la UI
            } catch (e: Exception) {
                e.printStackTrace()
                // Si falla en red, podrías guardar localmente o reintentar
            }
        }
    }
}