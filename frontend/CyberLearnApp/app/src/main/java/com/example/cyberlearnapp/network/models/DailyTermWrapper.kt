package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- CLASE GlossaryTerm ELIMINADA (Para evitar Redeclaration) ---

// --- WRAPPER DEL TÉRMINO DIARIO ---
@Serializable
data class DailyTermWrapper(
    @SerialName("term") val term: GlossaryTerm,
    @SerialName("already_viewed_today") val alreadyViewedToday: Boolean,
    @SerialName("xp_reward") val xpReward: Int
)

// --- MODELOS DE REQUEST/RESPONSE PARA GANAR XP ---
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