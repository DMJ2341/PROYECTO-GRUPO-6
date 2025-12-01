package com.example.cyberlearnapp.network.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

// --- MODELOS DE LA API (Deben coincidir con el JSON del backend) ---

@Serializable
data class DashboardResponse(
    val success: Boolean,
    val dashboard: DashboardSummary
)

@Serializable
data class DashboardSummary(
    @SerialName("total_xp") val totalXp: Int,
    val level: Int,
    @SerialName("current_streak") val currentStreak: Int,
    @SerialName("badges_count") val badgesCount: Int,
    @SerialName("courses_progress") val coursesProgress: List<CourseProgress>,
    @SerialName("next_course") val nextCourse: NextCourse?,
    @SerialName("completed_courses") val completedCourses: Int,
    @SerialName("total_courses") val totalCourses: Int,

    // ✅ CAMPOS PARA LA GAMIFICACIÓN (ASUME QUE EL BACKEND LOS DEVUELVE)
    @SerialName("has_preference_result") val hasPreferenceResult: Boolean,
    @SerialName("final_exam_passed") val finalExamPassed: Boolean
)

@Serializable
data class CourseProgress(
    @SerialName("course_id") val courseId: Int,
    val title: String,
    @SerialName("completed_lessons") val completedLessons: Int,
    @SerialName("total_lessons") val totalLessons: Int,
    val percentage: Int
)

@Serializable
data class NextCourse(
    val title: String,
    val level: String
)

// --- ESTADO DEL VIEWMODEL (Frontend) ---
data class DashboardState(
    val isLoading: Boolean = true,
    val error: String? = null,

    val userXp: Int = 0,
    val userLevel: Int = 1,

    val dailyTerm: DailyTermWrapper? = null,

    val completedCourses: Int = 0,

    val hasPreferenceResult: Boolean = false,
    val finalExamPassed: Boolean = false,

    // Asume que Badge está definido en otro archivo (Badge.kt)
    val badges: List<Badge> = emptyList()
)