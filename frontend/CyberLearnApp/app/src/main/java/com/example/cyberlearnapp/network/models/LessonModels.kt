package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class LessonResponse(
    val success: Boolean = true,
    val id: String,
    val title: String,
    val description: String? = null,
    // Recibimos la lista de pantallas
    val screens: List<LessonScreen> = emptyList(),
    @SerialName("xp_reward") val xpReward: Int = 10
)

@Serializable
data class LessonScreen(
    @SerialName("screen_id") val screenId: Int,
    val type: String, // "story_hook", "quiz", etc.
    val title: String,
    val duration: Int? = null,
    // 🔑 LA CLAVE: Todo el contenido específico (imágenes, preguntas, stats) viene aquí
    val content: JsonObject
)