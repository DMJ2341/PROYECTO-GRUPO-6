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
    // Ahora llamamos explícitamente a getLessonDetail
    suspend fun getLesson(lessonId: String): LessonResponse {
        val token = "Bearer ${AuthManager.getToken() ?: ""}"

        // ✅ CAMBIO CLAVE: Llamada al método renombrado
        val response = apiService.getLessonDetail(token, lessonId)

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            // Si falla, lanzamos error con código para debug
            throw Exception("Error ${response.code()}: ${response.message()}")
        }
    }

    suspend fun markLessonComplete(lessonId: String) {
        val token = "Bearer ${AuthManager.getToken() ?: ""}"
        val response = apiService.completeLesson(token, lessonId)

        if (!response.isSuccessful) {
            throw Exception("Error al completar lección: ${response.code()}")
        }
    }
}