package com.example.cyberlearnapp.network.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class DailyTermWrapper(
    @SerialName("term") val term: GlossaryTerm,
    @SerialName("already_viewed_today") val alreadyViewedToday: Boolean,
    @SerialName("xp_reward") val xpReward: Int = 5
)

@Serializable
data class CompleteDailyTermRequest(
    @SerialName("term_id") val termId: Int
)

@Serializable
data class CompleteDailyTermResponse(
    val success: Boolean,
    @SerialName("xp_earned") val xpEarned: Int,
    val message: String
)