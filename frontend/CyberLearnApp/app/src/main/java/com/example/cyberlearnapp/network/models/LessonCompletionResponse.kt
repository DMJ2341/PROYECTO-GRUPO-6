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
    val course_progress: CourseProgress? = null
)

@Serializable
data class CourseProgress(
    val course_id: Int,
    val percentage: Int,
    val completed: Int,
    val total: Int,
    val course_completed: Boolean
)