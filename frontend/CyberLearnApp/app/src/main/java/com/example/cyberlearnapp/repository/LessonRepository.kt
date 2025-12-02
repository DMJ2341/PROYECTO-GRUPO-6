package com.example.cyberlearnapp.repository

import android.util.Log
import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.LessonCompletionData
import com.example.cyberlearnapp.network.models.LessonDetailResponse
import com.example.cyberlearnapp.utils.AuthManager
import javax.inject.Inject

class LessonRepository @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * Obtiene los detalles completos de una lección
     * @param lessonId ID de la lección (ej: "c1_l1", "c3_l2")
     * @return LessonDetailResponse con todas las screens y metadatos
     */
    suspend fun getLesson(lessonId: String): LessonDetailResponse? {
        val token = AuthManager.getToken() ?: return null

        Log.d("LessonRepo", "📖 Cargando lección: $lessonId")

        val response = apiService.getLessonDetail("Bearer $token", lessonId)

        if (response.isSuccessful) {
            val lesson = response.body()
            Log.d("LessonRepo", "✅ Lección cargada: ${lesson?.title} (${lesson?.totalScreens} screens)")
            return lesson
        } else {
            when (response.code()) {
                403 -> {
                    Log.e("LessonRepo", "🔒 Lección bloqueada (403)")
                    throw Exception("Lección bloqueada. Completa las lecciones anteriores.")
                }
                404 -> {
                    Log.e("LessonRepo", "❌ Lección no encontrada (404)")
                    throw Exception("Lección no encontrada: $lessonId")
                }
                else -> {
                    Log.e("LessonRepo", "❌ Error ${response.code()}: ${response.errorBody()?.string()}")
                    throw Exception("Error al cargar la lección (${response.code()})")
                }
            }
        }
    }

    /**
     * Marca una lección como completada y otorga XP
     * @param lessonId ID de la lección completada
     * @return LessonCompletionData con XP ganado y progreso actualizado
     */
    suspend fun markLessonComplete(lessonId: String): LessonCompletionData? {
        val token = AuthManager.getToken()

        Log.d("LessonRepo", "🎯 Completando lección: $lessonId")
        Log.d("LessonRepo", "🔑 Token: ${token?.take(30)}...")

        if (token == null) {
            Log.e("LessonRepo", "❌ Token es null!")
            return null
        }

        val response = apiService.completeLesson("Bearer $token", lessonId)

        Log.d("LessonRepo", "📥 Response code: ${response.code()}")
        Log.d("LessonRepo", "📥 Response successful: ${response.isSuccessful}")
        Log.d("LessonRepo", "📥 Response body: ${response.body()}")

        if (response.isSuccessful && response.body()?.success == true) {
            val data = response.body()?.data
            Log.d("LessonRepo", "✅ XP ganado: ${data?.xp_earned}")
            Log.d("LessonRepo", "✅ Lección completada: ${data?.lesson_completed}")
            Log.d("LessonRepo", "🏆 Progreso del curso: ${data?.course_progress}")
            return data
        }

        Log.e("LessonRepo", "❌ Error: ${response.code()}")
        if (!response.isSuccessful) {
            Log.e("LessonRepo", "❌ Error body: ${response.errorBody()?.string()}")
        }

        return null
    }
}