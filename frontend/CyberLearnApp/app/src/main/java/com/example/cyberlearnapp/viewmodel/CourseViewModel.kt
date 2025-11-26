package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.models.Course
import com.example.cyberlearnapp.network.models.Lesson
import com.example.cyberlearnapp.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val repository: CourseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CourseState())
    val state: StateFlow<CourseState> = _state.asStateFlow()

    fun loadCourses() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val courses = repository.getCourses()
                _state.value = _state.value.copy(courses = courses, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun loadCourseDetails(courseId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // Cargamos lecciones
                val lessons = repository.getCourseLessons(courseId)
                // Buscamos el curso en la lista ya cargada para tener el título
                val course = _state.value.courses.find { it.id == courseId }

                _state.value = _state.value.copy(
                    isLoading = false,
                    selectedCourse = course,
                    lessons = lessons
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    // Helper para saber si una lección está completada (simple check en memoria por ahora)
    fun isLessonCompleted(lessonId: String): Boolean {
        return _state.value.lessons.find { it.id == lessonId }?.isCompleted == true
    }
}

data class CourseState(
    val isLoading: Boolean = false,
    val courses: List<Course> = emptyList(),
    val selectedCourse: Course? = null,
    val lessons: List<Lesson> = emptyList(),
    val courseProgress: Float = 0f, // Se podría calcular
    val error: String? = null
)