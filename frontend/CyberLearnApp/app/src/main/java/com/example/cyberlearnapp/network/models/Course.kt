package com.example.cyberlearnapp.network.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo para un curso
 */
data class Course(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("icon")
    val icon: String = "📚",

    @SerializedName("total_lessons")
    val totalLessons: Int,

    @SerializedName("completed_lessons")
    val completedLessons: Int = 0,

    @SerializedName("progress")
    val progress: Int = 0,

    @SerializedName("difficulty")
    val difficulty: String? = null, // "beginner", "intermediate", "advanced"

    @SerializedName("estimated_hours")
    val estimatedHours: Int? = null
)

/**
 * Modelo para detalle de curso con lecciones incluidas
 */
