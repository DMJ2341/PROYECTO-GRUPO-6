package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val id: Int,
    val title: String,
    val description: String,
    val level: String,
    @SerialName("xp_reward") val xpReward: Int,
    @SerialName("image_url") val imageUrl: String? = null
)