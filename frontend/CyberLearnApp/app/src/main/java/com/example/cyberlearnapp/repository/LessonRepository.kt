package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.LessonCompletionData
import com.example.cyberlearnapp.network.models.LessonResponse
import com.example.cyberlearnapp.utils.AuthManager
import javax.inject.Inject

class LessonRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getLesson(lessonId: String): LessonResponse? {
        val token = AuthManager.getToken() ?: return null
        val response = apiService.getLessonDetail(token, lessonId)
        if (response.isSuccessful) {
            return response.body()
        } else {
            // Manejo básico de errores (puedes mejorarlo)
            if (response.code() == 403) {
                throw Exception("Lección bloqueada (403)")
            }
            throw Exception("Error ${response.code()}")
        }
    }

    // ✅ Devuelve los datos de XP
    suspend fun markLessonComplete(lessonId: String): LessonCompletionData? {
        val token = AuthManager.getToken() ?: return null
        val response = apiService.completeLesson(token, lessonId)
        if (response.isSuccessful && response.body()?.success == true) {
            return response.body()?.data
        }
        return null
    }
}