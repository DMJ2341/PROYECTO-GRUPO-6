package com.example.cyberlearnapp.viewmodel

import android.util.Log
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
                Log.d("LessonViewModel", "🎯 Iniciando completeLesson para: $lessonId")

                val result = repository.markLessonComplete(lessonId)

                Log.d("LessonViewModel", "📦 Resultado recibido: $result")

                if (result != null) {
                    Log.d("LessonViewModel", "✅ XP ganado: ${result.xp_earned}")
                    Log.d("LessonViewModel", "✅ Lección completada: ${result.lesson_completed}")
                    _completionResult.value = result
                } else {
                    Log.e("LessonViewModel", "❌ Resultado es null")
                }
            } catch (e: Exception) {
                Log.e("LessonViewModel", "❌ Error completando lección: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }

    fun resetCompletionResult() {
        _completionResult.value = null
    }
}