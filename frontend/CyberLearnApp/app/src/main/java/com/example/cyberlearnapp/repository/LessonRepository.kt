package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.LessonResponse
import com.example.cyberlearnapp.utils.AuthManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LessonRepository @Inject constructor(
    private val apiService: ApiService
) {
    // Obtiene la lección completa (con su JSON de pantallas)
    suspend fun getLesson(lessonId: String): LessonResponse {
        val token = "Bearer ${AuthManager.getToken() ?: ""}"
        val response = apiService.getLesson(token, lessonId)

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception("Error ${response.code()}: No se pudo cargar la lección")
        }
    }

    // Marca la lección como terminada
    suspend fun markLessonComplete(lessonId: String) {
        val token = "Bearer ${AuthManager.getToken() ?: ""}"
        val response = apiService.completeLesson(token, lessonId)

        if (!response.isSuccessful) {
            throw Exception("Error al guardar el progreso: ${response.message()}")
        }
    }
}