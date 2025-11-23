package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.InteractiveLesson
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para manejar lecciones interactivas
 */
@Singleton
class LessonRepository @Inject constructor(
    private val apiService: ApiService
) {

    /**
     * Obtener lección interactiva por ID
     */
    suspend fun getInteractiveLesson(lessonId: Int): InteractiveLesson {
        return apiService.getInteractiveLesson(lessonId)
    }

    /**
     * Completar una lección y enviar resultados
     */
    suspend fun completeLesson(
        lessonId: Int,
        score: Int,
        xpEarned: Int
    ) {
        val completionData = mapOf(
            "lesson_id" to lessonId,
            "score" to score,
            "xp_earned" to xpEarned,
            "completed_at" to System.currentTimeMillis()
        )

        apiService.completeLesson(completionData)
    }

    /**
     * Registrar actividad de usuario en una lección
     */
    suspend fun recordActivity(
        lessonId: Int,
        screenNumber: Int,
        isCorrect: Boolean
    ) {
        val activityData = mapOf(
            "lesson_id" to lessonId,
            "screen_number" to screenNumber,
            "is_correct" to isCorrect,
            "timestamp" to System.currentTimeMillis()
        )

        apiService.recordLessonActivity(activityData)
    }
}