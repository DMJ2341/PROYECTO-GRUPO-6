package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Badge(
    val id: Int,
    val name: String,
    val description: String,
    val icon: String,
    @SerialName("xp_required") val xpRequired: Int,
    // Estos campos pueden venir si es una lista de badges del usuario
    @SerialName("earned_at") val earnedAt: String? = null,
    @SerialName("earned_value") val earnedValue: Boolean? = null
)