package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.Course
import com.example.cyberlearnapp.network.models.Lesson
// import com.example.cyberlearnapp.utils.AuthManager // Ya no es necesario importar si no se usa getToken()
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor(
    private val apiService: ApiService
) {
    // ❌ Se elimina la función getToken() ya que los endpoints llamados son públicos
    // private fun getToken(): String = "Bearer ${AuthManager.getToken() ?: ""}"

    // ✅ CORRECCIÓN 1: Se llama a getCourses() sin argumentos.
    suspend fun getCourses(): List<Course> {
        val response = apiService.getCourses()
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception("Error cargando cursos")
        }
    }

    // ✅ CORRECCIÓN 2: Se llama a getCourseLessons() solo con courseId.
    suspend fun getCourseLessons(courseId: Int): List<Lesson> {
        val response = apiService.getCourseLessons(courseId)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception("Error cargando lecciones")
        }
    }
}