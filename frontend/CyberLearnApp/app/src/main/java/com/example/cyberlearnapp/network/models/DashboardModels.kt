package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
// Importar JsonElement si preference_result puede ser cualquier cosa
import kotlinx.serialization.json.JsonElement

@Serializable
data class DashboardResponse(
    val success: Boolean,
    val dashboard: DashboardData
)

@Serializable
data class DashboardData(
    @SerialName("total_xp") val totalXp: Int,
    val level: Int,
    @SerialName("badges_count") val badgesCount: Int,
    @SerialName("courses_completed") val coursesCompleted: Int,
    val badges: List<Badge> = emptyList(),
    // Usamos JsonElement para campos genéricos que podrían ser null o un objeto complejo
    @SerialName("preference_result") val preferenceResult: JsonElement? = null
)