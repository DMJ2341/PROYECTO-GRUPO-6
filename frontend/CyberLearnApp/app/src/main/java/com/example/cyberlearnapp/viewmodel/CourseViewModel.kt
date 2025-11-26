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
                // 1. Cargar las lecciones de este curso (siempre necesario)
                val lessons = repository.getCourseLessons(courseId)

                // 2. Buscar la información del curso (Título, Descripción, etc.)
                var course = _state.value.courses.find { it.id == courseId }

                // ✅ CORRECCIÓN: Si 'course' es null (porque el VM es nuevo),
                // recargamos la lista completa de cursos desde la API.
                if (course == null) {
                    val allCourses = repository.getCourses()
                    course = allCourses.find { it.id == courseId }

                    // Actualizamos la lista general en el estado también
                    _state.value = _state.value.copy(courses = allCourses)
                }

                // Ahora sí actualizamos el estado con el curso encontrado
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

    // Helper para saber si una lección está completada
    fun isLessonCompleted(lessonId: String): Boolean {
        return _state.value.lessons.find { it.id == lessonId }?.isCompleted == true
    }
}

data class CourseState(
    val isLoading: Boolean = false,
    val courses: List<Course> = emptyList(),
    val selectedCourse: Course? = null,
    val lessons: List<Lesson> = emptyList(),
    val courseProgress: Float = 0f,
    val error: String? = null
)