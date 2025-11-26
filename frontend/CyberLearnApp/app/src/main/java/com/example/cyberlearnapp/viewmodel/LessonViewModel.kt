package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getLesson(lessonId)
                _lesson.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                _lesson.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun completeLesson(lessonId: String) {
        viewModelScope.launch {
            try {
                repository.markLessonComplete(lessonId)
                // Aquí podrías actualizar algún estado local o notificar éxito
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}