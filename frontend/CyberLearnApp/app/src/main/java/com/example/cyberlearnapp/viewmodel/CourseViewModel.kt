package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.Course
import com.example.cyberlearnapp.network.models.Lesson
import com.example.cyberlearnapp.ui.screens.LessonsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _coursesState = MutableStateFlow<CoursesState>(CoursesState.Loading)
    val coursesState: StateFlow<CoursesState> = _coursesState.asStateFlow()

    private val _lessonsState = MutableStateFlow<LessonsState>(LessonsState.Loading)
    val lessonsState: StateFlow<LessonsState> = _lessonsState.asStateFlow()

    private val _currentCourseTitle = MutableStateFlow("")
    val currentCourseTitle: StateFlow<String> = _currentCourseTitle.asStateFlow()

    init {
        loadCourses()
    }

    fun loadCourses() {
        viewModelScope.launch {
            _coursesState.value = CoursesState.Loading

            try {
                // Por ahora, usar datos mock hasta que backend esté listo
                val courses = getMockCourses()
                _coursesState.value = CoursesState.Success(courses)
            } catch (e: Exception) {
                _coursesState.value = CoursesState.Error(
                    e.message ?: "Error al cargar cursos"
                )
            }
        }
    }

    fun loadCourseLessons(courseId: Int) {
        viewModelScope.launch {
            _lessonsState.value = LessonsState.Loading

            try {
                // Obtener título del curso
                val course = getMockCourses().find { it.id == courseId }
                _currentCourseTitle.value = course?.title ?: "Curso"

                // Por ahora, usar datos mock
                val lessons = getMockLessons(courseId)
                _lessonsState.value = LessonsState.Success(lessons)
            } catch (e: Exception) {
                _lessonsState.value = LessonsState.Error(
                    e.message ?: "Error al cargar lecciones"
                )
            }
        }
    }

    private fun getMockCourses(): List<Course> {
        return listOf(
            Course(
                id = 1,
                title = "Fundamentos de Ciberseguridad",
                description = "Aprende los conceptos básicos de ciberseguridad",
                icon = "🛡️",
                totalLessons = 6,
                completedLessons = 0,
                progress = 0
            ),
            Course(
                id = 2,
                title = "Seguridad de Redes y Comunicaciones",
                description = "Protege tus redes y comunicaciones",
                icon = "🌐",
                totalLessons = 6,
                completedLessons = 0,
                progress = 0
            ),
            Course(
                id = 3,
                title = "Seguridad de Sistemas Operativos",
                description = "Asegura tu sistema operativo",
                icon = "💻",
                totalLessons = 6,
                completedLessons = 0,
                progress = 0
            ),
            Course(
                id = 4,
                title = "Ciberseguridad Avanzada y Cloud",
                description = "Nivel avanzado de seguridad en la nube",
                icon = "☁️",
                totalLessons = 6,
                completedLessons = 0,
                progress = 0
            ),
            Course(
                id = 5,
                title = "Operaciones de Ciberseguridad",
                description = "Aprende operaciones de seguridad",
                icon = "🕵️",
                totalLessons = 6,
                completedLessons = 0,
                progress = 0
            )
        )
    }

    private fun getMockLessons(courseId: Int): List<Lesson> {
        return when (courseId) {
            1 -> listOf(
                Lesson(1, 1, "Introducción a las Amenazas Cibernéticas", "Aprende sobre WannaCry y tipos de amenazas", false),
                Lesson(2, 2, "Ingeniería Social y Engaño", "Caso Equifax y técnicas de phishing", false),
                Lesson(3, 3, "Ataques Cibernéticos Básicos", "Ransomware, DDoS y malware", false),
                Lesson(4, 4, "Dispositivos Móviles e Inalámbricos", "Evil Twin, SMiShing y seguridad móvil", false),
                Lesson(5, 5, "Principios de la Ciberseguridad", "Tríada CIA y principios fundamentales", false),
                Lesson(6, 6, "Evaluación Final - Operación Escudo Ciudadano", "Pon a prueba todo lo aprendido", false)
            )
            2 -> listOf(
                Lesson(7, 1, "Lección 1 - Redes", "Próximamente", false),
                Lesson(8, 2, "Lección 2 - Redes", "Próximamente", false),
                Lesson(9, 3, "Lección 3 - Redes", "Próximamente", false),
                Lesson(10, 4, "Lección 4 - Redes", "Próximamente", false),
                Lesson(11, 5, "Lección 5 - Redes", "Próximamente", false),
                Lesson(12, 6, "Lección 6 - Redes", "Próximamente", false)
            )
            else -> emptyList()
        }
    }
}

sealed class CoursesState {
    object Loading : CoursesState()
    data class Success(val courses: List<Course>) : CoursesState()
    data class Error(val message: String) : CoursesState()
}