package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.Course
import com.example.cyberlearnapp.network.models.Lesson
import com.example.cyberlearnapp.utils.AuthManager
import javax.inject.Inject

class CourseRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllCourses(): List<Course> {
        // ✅ CORREGIDO: getCourses() NO recibe parámetros según tu ApiService
        val response = apiService.getCourses()

        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            throw Exception("Error ${response.code()}: ${response.message()}")
        }
    }

    suspend fun getCourseDetail(courseId: Int): Course {
        // ✅ WORKAROUND: Como no tienes endpoint getCourseDetail en ApiService,
        // buscamos el curso en la lista completa
        val allCourses = getAllCourses()
        return allCourses.find { it.id == courseId }
            ?: throw Exception("Curso no encontrado")
    }

    suspend fun getCourseLessons(courseId: Int): List<Lesson> {
        val token = AuthManager.getToken() ?: throw Exception("No hay sesión activa")

        // ✅ CORREGIDO: Usa "Bearer $token" según tu patrón
        val response = apiService.getCourseLessons("Bearer $token", courseId)

        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            throw Exception("Error ${response.code()}: ${response.message()}")
        }
    }
}