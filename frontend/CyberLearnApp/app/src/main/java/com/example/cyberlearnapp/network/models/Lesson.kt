package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    @SerialName("id") val id: String,
    @SerialName("course_id") val courseId: Int,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("type") val type: String = "lesson",
    @SerialName("duration_minutes") val durationMinutes: Int = 0,
    @SerialName("xp_reward") val xpReward: Int = 10,
    @SerialName("order_index") val orderIndex: Int = 0,

    // ✅ NUEVOS CAMPOS para estado de la lección
    @SerialName("is_completed") val isCompleted: Boolean = false,
    @SerialName("is_locked") val isLocked: Boolean = false
)



