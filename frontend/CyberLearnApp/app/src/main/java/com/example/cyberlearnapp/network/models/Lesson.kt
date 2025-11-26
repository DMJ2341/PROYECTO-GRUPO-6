package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val id: String,
    @SerialName("course_id") val courseId: Int,
    val title: String,
    val description: String,
    val content: String? = null,
    @SerialName("order_index") val orderIndex: Int,
    @SerialName("duration_minutes") val durationMinutes: Int,
    @SerialName("xp_reward") val xpReward: Int,
    // ✅ Importante: valor por defecto false
    @SerialName("is_completed") val isCompleted: Boolean = false,
    val type: String
)