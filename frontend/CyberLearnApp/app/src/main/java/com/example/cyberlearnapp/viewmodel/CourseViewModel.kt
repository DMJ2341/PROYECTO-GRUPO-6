package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.Course
import com.example.cyberlearnapp.network.Lesson
import com.example.cyberlearnapp.network.RetrofitInstance
import com.example.cyberlearnapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _selectedCourse = MutableStateFlow<Course?>(null)
    val selectedCourse: StateFlow<Course?> = _selectedCourse.asStateFlow()

    private val _courseLessons = MutableStateFlow<List<Lesson>>(emptyList())
    val courseLessons: StateFlow<List<Lesson>> = _courseLessons.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ✅ CARGAR TODOS LOS CURSOS
    fun loadCourses() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                println("🔍 [COURSE-VM] Cargando cursos...")
                val response = RetrofitInstance.api.getCourses()
                println("📡 [COURSE-VM] Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val coursesList = response.body()!!
                    _courses.value = coursesList
                    println("✅ [COURSE-VM] ${coursesList.size} cursos cargados")
                } else {
                    val errorMsg = "Error ${response.code()}: ${response.message()}"
                    println("❌ [COURSE-VM] $errorMsg")
                    _errorMessage.value = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = "Error cargando cursos: ${e.message}"
                println("💥 [COURSE-VM] $errorMsg")
                e.printStackTrace()
                _errorMessage.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ SELECCIONAR UN CURSO
    fun selectCourse(course: Course) {
        _selectedCourse.value = course
        println("📚 [COURSE-VM] Curso seleccionado: ${course.title}")
    }

    // ✅ CARGAR LECCIONES DE UN CURSO
    fun loadCourseLessons(courseId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                println("🔍 [COURSE-VM] Cargando lecciones del curso $courseId...")
                val response = RetrofitInstance.api.getCourseLessons(courseId)
                println("📡 [COURSE-VM] Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val lessons = response.body()!!
                    _courseLessons.value = lessons
                    println("✅ [COURSE-VM] ${lessons.size} lecciones cargadas")

                    // Debug: Mostrar IDs de lecciones
                    lessons.forEach { lesson ->
                        println("   📖 Lección: ${lesson.id} - ${lesson.title}")
                    }
                } else {
                    val errorMsg = "Error ${response.code()}: ${response.message()}"
                    println("❌ [COURSE-VM] $errorMsg")
                    _errorMessage.value = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = "Error cargando lecciones: ${e.message}"
                println("💥 [COURSE-VM] $errorMsg")
                e.printStackTrace()
                _errorMessage.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ OBTENER LECCIÓN POR ID
    fun getLessonById(lessonId: String): Lesson? {
        return _courseLessons.value.find { it.id == lessonId }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSelectedCourse() {
        _selectedCourse.value = null
        _courseLessons.value = emptyList()
    }
}