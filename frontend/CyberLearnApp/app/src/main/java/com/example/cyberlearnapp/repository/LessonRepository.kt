package com.example.cyberlearnapp.repository

import android.util.Log
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
            if (response.code() == 403) {
                throw Exception("Lección bloqueada (403)")
            }
            throw Exception("Error ${response.code()}")
        }
    }

    suspend fun markLessonComplete(lessonId: String): LessonCompletionData? {
        val token = AuthManager.getToken()

        Log.d("LessonRepo", "🎯 Completando lección: $lessonId")
        Log.d("LessonRepo", "🔑 Token: ${token?.take(30)}...")

        if (token == null) {
            Log.e("LessonRepo", "❌ Token es null!")
            return null
        }

        val response = apiService.completeLesson(token, lessonId)

        Log.d("LessonRepo", "📥 Response code: ${response.code()}")
        Log.d("LessonRepo", "📥 Response successful: ${response.isSuccessful}")
        Log.d("LessonRepo", "📥 Response body: ${response.body()}")

        if (response.isSuccessful && response.body()?.success == true) {
            val data = response.body()?.data
            Log.d("LessonRepo", "✅ XP ganado: ${data?.xp_earned}")
            Log.d("LessonRepo", "✅ Lección completada: ${data?.lesson_completed}")
            Log.d("LessonRepo", "🏆 Nuevas medallas: ${data?.course_progress}")
            return data
        }

        Log.e("LessonRepo", "❌ Error: ${response.code()}")
        if (!response.isSuccessful) {
            Log.e("LessonRepo", "❌ Error body: ${response.errorBody()?.string()}")
        }

        return null
    }
}