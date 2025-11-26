package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyTermWrapper(
    val success: Boolean,
    @SerialName("daily_term") val dailyTerm: DailyTerm,
    @SerialName("xp_earned") val xpEarned: Int,
    @SerialName("already_viewed_today") val alreadyViewedToday: Boolean
)

@Serializable
data class DailyTerm(
    val id: Int,
    val term: String,
    val definition: String,
    val category: String,
    val difficulty: String
)