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
    private fun getToken(): String = "Bearer ${AuthManager.getToken() ?: ""}"

    suspend fun getCourses(): List<Course> {
        val response = apiService.getCourses(getToken())
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception("Error cargando cursos")
        }
    }

    suspend fun getCourseLessons(courseId: Int): List<Lesson> {
        val response = apiService.getCourseLessons(getToken(), courseId)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception("Error cargando lecciones")
        }
    }
}