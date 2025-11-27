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
    // Referencia a CourseProgress, que ahora está centralizada en DashboardModels.kt
    val course_progress: CourseProgress? = null
)

// --- CLASE CourseProgress ELIMINADA (Para evitar Redeclaration) ---