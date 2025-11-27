package com.example.cyberlearnapp.network.models

import kotlinx.serialization.Serializable

@Serializable
data class LessonCompletionResponse(
    val success: Boolean,
    val data: LessonCompletionData? = null
)

@Serializable
data class LessonCompletionData(
    val lesson_completed: Boolean,
    val xp_earned: Int,
    // ✅ Ahora hace referencia al modelo CourseProgress que está en DashboardModels.kt
    val course_progress: CourseProgress? = null
)

// La clase CourseProgress ha sido ELIMINADA de este archivo.