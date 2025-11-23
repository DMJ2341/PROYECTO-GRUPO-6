package com.example.cyberlearnapp.network.models

import com.google.gson.annotations.SerializedName

/**
 * Usuario
 */
data class User(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("total_xp")
    val totalXp: Int = 0,

    @SerializedName("current_streak")
    val currentStreak: Int = 0,

    @SerializedName("level")
    val level: Int = 1
)

/**
 * Curso
 */
data class Course(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("icon")
    val icon: String? = null,

    @SerializedName("total_lessons")
    val totalLessons: Int = 0,

    @SerializedName("completed_lessons")
    val completedLessons: Int = 0,

    @SerializedName("progress_percentage")
    val progressPercentage: Int = 0,

    @SerializedName("is_locked")
    val isLocked: Boolean = false
)

/**
 * Detalle de curso con lecciones
 */
data class CourseDetail(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("lessons")
    val lessons: List<Lesson>,

    @SerializedName("total_xp")
    val totalXp: Int = 0,

    @SerializedName("progress_percentage")
    val progressPercentage: Int = 0
)

/**
 * Lección
 */
data class Lesson(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("lesson_type")
    val lessonType: String = "interactive", // "text", "interactive", "quiz"

    @SerializedName("is_completed")
    val isCompleted: Boolean = false,

    @SerializedName("is_locked")
    val isLocked: Boolean = false,

    @SerializedName("xp_reward")
    val xpReward: Int = 0,

    @SerializedName("order")
    val order: Int = 0
)

/**
 * Insignia
 */
data class Badge(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("icon")
    val icon: String,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("xp_required")
    val xpRequired: Int = 0
)

/**
 * Insignia de usuario
 */
data class UserBadge(
    @SerializedName("id")
    val id: Int,

    @SerializedName("badge_id")
    val badgeId: Int,

    @SerializedName("badge")
    val badge: Badge,

    @SerializedName("earned_at")
    val earnedAt: String,

    @SerializedName("is_new")
    val isNew: Boolean = false
)