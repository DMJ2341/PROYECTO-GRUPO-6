package com.example.cyberlearnapp.network.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo para lección interactiva completa
 * ACTUALIZADO: Incluye List<Screen> para pantallas interactivas
 */
data class InteractiveLesson(
    @SerializedName("lesson_id")
    val lessonId: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("screens")
    val screens: List<Screen>, // 🆕 AGREGADO

    @SerializedName("total_xp")
    val totalXP: Int = 0,

    @SerializedName("estimated_time_minutes")
    val estimatedTimeMinutes: Int? = null
)