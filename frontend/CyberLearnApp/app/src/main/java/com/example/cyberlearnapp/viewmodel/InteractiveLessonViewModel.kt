package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.models.InteractiveLesson
import com.example.cyberlearnapp.network.models.Screen
import com.example.cyberlearnapp.repository.LessonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para lecciones interactivas
 * Maneja el estado, navegación y progreso del usuario
 */
@HiltViewModel
class InteractiveLessonViewModel @Inject constructor(
    private val repository: LessonRepository
) : ViewModel() {

    // Estado de la lección
    private val _lessonState = MutableStateFlow<LessonState>(LessonState.Loading)
    val lessonState: StateFlow<LessonState> = _lessonState.asStateFlow()

    // Pantalla actual
    private val _currentScreenIndex = MutableStateFlow(0)
    val currentScreenIndex: StateFlow<Int> = _currentScreenIndex.asStateFlow()

    // Lista de pantallas
    private val _screens = MutableStateFlow<List<Screen>>(emptyList())
    val screens: StateFlow<List<Screen>> = _screens.asStateFlow()

    // Respuestas del usuario (para tracking)
    private val _userAnswers = MutableStateFlow<MutableMap<Int, Boolean>>(mutableMapOf())
    val userAnswers: StateFlow<Map<Int, Boolean>> = _userAnswers.asStateFlow()

    // XP ganado en la lección
    private val _xpEarned = MutableStateFlow(0)
    val xpEarned: StateFlow<Int> = _xpEarned.asStateFlow()

    /**
     * Cargar lección desde el backend
     */
    fun loadLesson(lessonId: Int) {
        viewModelScope.launch {
            _lessonState.value = LessonState.Loading

            try {
                val lesson = repository.getInteractiveLesson(lessonId)
                _screens.value = lesson.screens
                _lessonState.value = LessonState.Success(lesson)
                _currentScreenIndex.value = 0
                _userAnswers.value.clear()
                _xpEarned.value = 0
            } catch (e: Exception) {
                _lessonState.value = LessonState.Error(
                    e.message ?: "Error al cargar la lección"
                )
            }
        }
    }

    /**
     * Navegar a la siguiente pantalla
     */
    fun nextScreen() {
        val currentIndex = _currentScreenIndex.value
        val totalScreens = _screens.value.size

        if (currentIndex < totalScreens - 1) {
            _currentScreenIndex.value = currentIndex + 1
        } else {
            // Última pantalla alcanzada - completar lección
            completeLesson()
        }
    }

    /**
     * Navegar a pantalla anterior
     */
    fun previousScreen() {
        val currentIndex = _currentScreenIndex.value
        if (currentIndex > 0) {
            _currentScreenIndex.value = currentIndex - 1
        }
    }

    /**
     * Ir a una pantalla específica
     */
    fun goToScreen(index: Int) {
        if (index in _screens.value.indices) {
            _currentScreenIndex.value = index
        }
    }

    /**
     * Registrar respuesta del usuario
     */
    fun recordAnswer(screenIndex: Int, isCorrect: Boolean) {
        _userAnswers.value[screenIndex] = isCorrect

        // Otorgar XP si es correcto
        if (isCorrect) {
            _xpEarned.value += 10  // 10 XP por respuesta correcta
        }
    }

    /**
     * Completar la lección y enviar datos al backend
     */
    private fun completeLesson() {
        viewModelScope.launch {
            val lessonState = _lessonState.value
            if (lessonState is LessonState.Success) {
                try {
                    // Calcular score
                    val correctAnswers = _userAnswers.value.count { it.value }
                    val totalQuestions = _userAnswers.value.size
                    val score = if (totalQuestions > 0) {
                        (correctAnswers.toFloat() / totalQuestions * 100).toInt()
                    } else 100

                    // Enviar al backend
                    repository.completeLesson(
                        lessonId = lessonState.lesson.lessonId,
                        score = score,
                        xpEarned = _xpEarned.value
                    )

                    _lessonState.value = LessonState.Completed(
                        score = score,
                        xpEarned = _xpEarned.value
                    )
                } catch (e: Exception) {
                    // Aunque falle el envío, mostrar como completada localmente
                    _lessonState.value = LessonState.Completed(
                        score = 100,
                        xpEarned = _xpEarned.value
                    )
                }
            }
        }
    }

    /**
     * Reiniciar la lección
     */
    fun resetLesson() {
        _currentScreenIndex.value = 0
        _userAnswers.value.clear()
        _xpEarned.value = 0
    }

    /**
     * Obtener progreso actual (0-100%)
     */
    fun getProgress(): Float {
        val totalScreens = _screens.value.size
        if (totalScreens == 0) return 0f
        return (_currentScreenIndex.value + 1).toFloat() / totalScreens
    }
}

/**
 * Estados posibles de la lección
 */
sealed class LessonState {
    object Loading : LessonState()
    data class Success(val lesson: InteractiveLesson) : LessonState()
    data class Error(val message: String) : LessonState()
    data class Completed(val score: Int, val xpEarned: Int) : LessonState()
}