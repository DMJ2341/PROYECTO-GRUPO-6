package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val id: String, // String para soportar "1_1"
    @SerialName("course_id") val courseId: Int,
    val title: String,
    val description: String? = null,
    val type: String,
    @SerialName("duration_minutes") val durationMinutes: Int,
    @SerialName("xp_reward") val xpReward: Int,
    @SerialName("order_index") val orderIndex: Int,
    // Estos campos suelen venir de la lógica de negocio o un join en el backend
    @SerialName("is_completed") var isCompleted: Boolean = false,
    // Si el backend no envía is_locked, lo calculamos en el ViewModel, así que default false
    var isLocked: Boolean = false
)