package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActivityResult(
    @SerialName("xp_earned") val xpEarned: Int,
    @SerialName("new_total_xp") val newTotalXp: Int,
    @SerialName("new_level") val newLevel: Int,
    @SerialName("streak") val streak: Int
)

@Serializable
data class ActivityLog(
    val id: Int,
    val type: String,
    val description: String,
    @SerialName("xp_gained") val xpGained: Int,
    @SerialName("created_at") val createdAt: String
)