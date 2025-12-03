package com.example.cyberlearnapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.models.LessonCompletionData
import com.example.cyberlearnapp.network.models.LessonDetailResponse
import com.example.cyberlearnapp.repository.LessonRepository
import com.example.cyberlearnapp.utils.RefreshEvent
import com.example.cyberlearnapp.utils.RefreshEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val repository: LessonRepository,
    private val refreshEventBus: RefreshEventBus
) : ViewModel() {

    private val _lesson = MutableStateFlow<LessonDetailResponse?>(null)
    val lesson: StateFlow<LessonDetailResponse?> = _lesson

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
                Log.d("LessonViewModel", "📖 Cargando lección: $lessonId")
                val result = repository.getLesson(lessonId)
                _lesson.value = result

                if (result != null) {
                    Log.d("LessonViewModel", "✅ Lección cargada: ${result.title} (${result.totalScreens} screens)")
                } else {
                    Log.w("LessonViewModel", "⚠️ La lección llegó nula")
                }

            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
                _lesson.value = null
                Log.e("LessonViewModel", "❌ Error cargando lección: ${e.message}", e)
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

                    // ✅ EMITIR EVENTO PARA REFRESCAR DASHBOARD
                    refreshEventBus.emit(RefreshEvent.Dashboard)
                    Log.d("LessonViewModel", "📡 Evento de refresco emitido")
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

    fun clearError() {
        _error.value = null
    }
}