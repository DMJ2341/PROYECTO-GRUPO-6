package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    // El log muestra "c1_l1", así que debe ser String, no Int
    val id: String,

    @SerialName("course_id")
    val courseId: Int,

    val title: String,

    val description: String,

    @SerialName("content") // A veces el backend no lo manda en la lista, lo hacemos nullable
    val content: String? = null,

    @SerialName("order_index")
    val orderIndex: Int,

    @SerialName("duration_minutes")
    val durationMinutes: Int,

    @SerialName("xp_reward")
    val xpReward: Int,

    @SerialName("is_completed")
    val isCompleted: Boolean = false,

    val type: String // "interactive", "video", etc.
)