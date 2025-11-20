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

@HiltViewModel
class InteractiveLessonViewModel @Inject constructor(
    private val repository: LessonRepository
) : ViewModel() {

    private val _lessonState = MutableStateFlow<LessonState>(LessonState.Loading)
    val lessonState: StateFlow<LessonState> = _lessonState.asStateFlow()

    private val _currentScreenIndex = MutableStateFlow(0)
    val currentScreenIndex: StateFlow<Int> = _currentScreenIndex.asStateFlow()

    private val _screens = MutableStateFlow<List<Screen>>(emptyList())
    val screens: StateFlow<List<Screen>> = _screens.asStateFlow()

    private val _userAnswers = MutableStateFlow<MutableMap<Int, Boolean>>(mutableMapOf())
    val userAnswers: StateFlow<Map<Int, Boolean>> = _userAnswers.asStateFlow()

    private val _xpEarned = MutableStateFlow(0)
    val xpEarned: StateFlow<Int> = _xpEarned.asStateFlow()

    private var currentLessonId: Int = 0

    fun loadLesson(lessonId: Int) {
        currentLessonId = lessonId
        viewModelScope.launch {
            _lessonState.value = LessonState.Loading

            try {
                // Por ahora, crear lección vacía (hasta que backend esté listo)
                val lesson = InteractiveLesson(
                    lessonId = lessonId,
                    title = "Lección $lessonId",
                    description = null,
                    screens = emptyList(),
                    totalXP = 100
                )
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

    fun nextScreen() {
        val currentIndex = _currentScreenIndex.value
        val totalScreens = getTotalScreens()

        if (currentIndex < totalScreens - 1) {
            _currentScreenIndex.value = currentIndex + 1
        } else {
            completeLesson()
        }
    }

    fun previousScreen() {
        val currentIndex = _currentScreenIndex.value
        if (currentIndex > 0) {
            _currentScreenIndex.value = currentIndex - 1
        }
    }

    fun goToScreen(index: Int) {
        val totalScreens = getTotalScreens()
        if (index in 0 until totalScreens) {
            _currentScreenIndex.value = index
        }
    }

    fun recordAnswer(screenIndex: Int, isCorrect: Boolean) {
        _userAnswers.value[screenIndex] = isCorrect

        if (isCorrect) {
            _xpEarned.value += 10
        }
    }

    private fun completeLesson() {
        viewModelScope.launch {
            val lessonState = _lessonState.value
            if (lessonState is LessonState.Success) {
                try {
                    val correctAnswers = _userAnswers.value.count { it.value }
                    val totalQuestions = _userAnswers.value.size
                    val score = if (totalQuestions > 0) {
                        (correctAnswers.toFloat() / totalQuestions * 100).toInt()
                    } else 100

                    repository.completeLesson(
                        lessonId = currentLessonId,
                        score = score,
                        xpEarned = _xpEarned.value
                    )

                    _lessonState.value = LessonState.Completed(
                        score = score,
                        xpEarned = _xpEarned.value
                    )
                } catch (e: Exception) {
                    _lessonState.value = LessonState.Completed(
                        score = 100,
                        xpEarned = _xpEarned.value
                    )
                }
            }
        }
    }

    fun resetLesson() {
        _currentScreenIndex.value = 0
        _userAnswers.value.clear()
        _xpEarned.value = 0
    }

    fun getProgress(): Float {
        val totalScreens = getTotalScreens()
        if (totalScreens == 0) return 0f
        return (_currentScreenIndex.value + 1).toFloat() / totalScreens
    }

    private fun getTotalScreens(): Int {
        // Cada lección tiene un número fijo de pantallas
        return when (currentLessonId) {
            1, 2, 3, 4, 5 -> 6  // Lecciones 1-5: 6 pantallas
            6 -> 8               // Lección 6: 8 pantallas
            else -> 6
        }
    }
}

sealed class LessonState {
    object Loading : LessonState()
    data class Success(val lesson: InteractiveLesson) : LessonState()
    data class Error(val message: String) : LessonState()
    data class Completed(val score: Int, val xpEarned: Int) : LessonState()
}