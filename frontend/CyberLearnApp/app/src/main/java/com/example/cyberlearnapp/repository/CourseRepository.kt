package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.Course
import com.example.cyberlearnapp.network.models.Lesson
import com.example.cyberlearnapp.utils.AuthManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCourses(): List<Course> {
        val response = apiService.getCourses()
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception("Error cargando cursos")
        }
    }

    // ✅ CORREGIDO: Ahora obtiene el token y lo pasa al API
    suspend fun getCourseLessons(courseId: Int): List<Lesson> {
        val token = AuthManager.getToken() ?: throw Exception("Usuario no autenticado")
        val response = apiService.getCourseLessons(token, courseId)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception("Error cargando lecciones")
        }
    }
}