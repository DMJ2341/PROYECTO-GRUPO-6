package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Badge(
    val id: Int,
    val name: String,
    val description: String,
    val icon: String,

    @SerialName("xp_required")
    val xpRequired: Int? = null,

    @SerialName("earned_at")
    val earnedAt: String? = null,

    @SerialName("earned_value")
    val earnedValue: Int? = null
)

@Serializable
data class BadgesResponse(
    val success: Boolean,
    val badges: List<Badge>
)