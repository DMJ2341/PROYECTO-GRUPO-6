package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- GLOSARIO BASE (GlossaryTerm) ---
@Serializable
data class GlossaryTerm(
    val id: Int,
    val term: String,
    val acronym: String? = null,
    val definition: String,
    val category: String? = null,
    val difficulty: String? = null,
    val example: String? = null // Incluido si el backend lo proporciona
)

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