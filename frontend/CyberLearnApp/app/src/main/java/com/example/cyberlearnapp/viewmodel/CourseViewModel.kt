package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.Course
import com.example.cyberlearnapp.network.Lesson
import com.example.cyberlearnapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseUiState(
    val courseList: List<Course> = emptyList(),
    val lessonList: List<Lesson> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val apiService: ApiService,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseUiState())
    val uiState = _uiState.asStateFlow()

    fun loadAllCourses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val token = userRepository.getToken().first() ?: ""
                val response = apiService.getAllCourses("Bearer $token")

                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        courseList = response.body() ?: emptyList()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Error al cargar cursos")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun loadLessonsForCourse(courseId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val token = userRepository.getToken().first() ?: ""
                val response = apiService.getCourseLessons(courseId, "Bearer $token")

                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        lessonList = response.body()?.lessons ?: emptyList()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Error al cargar lecciones")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}