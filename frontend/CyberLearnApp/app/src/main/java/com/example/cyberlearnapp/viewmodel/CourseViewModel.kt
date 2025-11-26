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

    // Lista de cursos (Pantalla principal)
    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    // Lista de lecciones (Pantalla de detalle)
    private val _lessons = MutableStateFlow<List<Lesson>>(emptyList())
    val lessons: StateFlow<List<Lesson>> = _lessons.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadCourses()
    }

    fun loadCourses() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.getCourses()
<<<<<<< HEAD
                if (result.isNotEmpty()) {
                    _courses.value = result
                } else {
                    // Opcional: _error.value = "No se encontraron cursos."
                    // Dejamos la lista vacía si el backend devuelve []
                    _courses.value = emptyList()
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar cursos: ${e.message}"
=======
                // repository.getCourses() devuelve List<Course> directamente (según tu código)
                if (result.isNotEmpty()) {
                    _courses.value = result
                } else {
                    _error.value = "No se encontraron cursos."
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
>>>>>>> main
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadLessons(courseId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
<<<<<<< HEAD
            _lessons.value = emptyList() // Limpiamos la lista anterior para que no se vea la del curso previo
            _error.value = null

            try {
                // Llamamos al repositorio
                val result = repository.getCourseLessons(courseId)

=======
            _lessons.value = emptyList()
            try {
                // ✅ CORRECCIÓN AQUÍ: Usamos el nombre correcto 'getCourseLessons'
                val result = repository.getCourseLessons(courseId)

>>>>>>> main
                if (result.isNotEmpty()) {
                    _lessons.value = result
                } else {
                    _error.value = "Este curso no tiene lecciones disponibles aún."
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar lecciones: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}