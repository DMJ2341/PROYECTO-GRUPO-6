package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    @SerialName("completed_courses") val completedCourses: Int,
    @SerialName("total_courses") val totalCourses: Int,
    @SerialName("has_preference_result") val hasPreferenceResult: Boolean,
    @SerialName("final_exam_passed") val finalExamPassed: Boolean,
    @SerialName("courses_progress") val coursesProgress: List<CourseProgress>,
    @SerialName("next_course") val nextCourse: NextCourse? = null,

    // ✅ AGREGAR ESTOS DOS CAMPOS NUEVOS
    @SerialName("is_academic") val isAcademic: Boolean = false,
    val institution: String? = null
)

@Serializable
data class CourseProgress(
    @SerialName("course_id") val courseId: Int,
    val title: String,
    val percentage: Int,
    @SerialName("completed_lessons") val completedLessons: Int,
    @SerialName("total_lessons") val totalLessons: Int,
    val completed: Boolean = false
)

@Serializable
data class NextCourse(
    val title: String,
    val level: String
)