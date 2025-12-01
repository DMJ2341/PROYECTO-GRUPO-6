package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GlossaryResponse(
    val success: Boolean,
    val terms: List<GlossaryTerm>
)

@Serializable
data class GlossaryTerm(
    val id: Int,

    @SerialName("term_en") val termEn: String,
    @SerialName("term_es") val termEs: String,

    @SerialName("definition_en") val definitionEn: String,
    @SerialName("definition_es") val definitionEs: String,

    val acronym: String? = null,
    val category: String? = null,
    val difficulty: String? = null,

    @SerialName("where_you_hear_it") val whereYouHearIt: String? = null,

    @SerialName("example_en") val exampleEn: String? = null,
    @SerialName("example_es") val exampleEs: String? = null,

    @SerialName("is_learned") val isLearned: Boolean = false,
    @SerialName("times_practiced") val timesPracticed: Int = 0,

    @SerialName("learned_at") val learnedAt: String? = null,
    val reference: String? = null
)

@Serializable
data class MarkLearnedRequest(
    @SerialName("is_learned") val isLearned: Boolean
)

@Serializable
data class MarkLearnedResponse(
    val success: Boolean,
    @SerialName("is_learned") val isLearned: Boolean
)

@Serializable
data class GlossaryStatsResponse(
    val success: Boolean,
    val stats: GlossaryStats
)

@Serializable
data class GlossaryStats(
    @SerialName("total_terms") val totalTerms: Int,
    @SerialName("learned_count") val learnedCount: Int = 0,
    @SerialName("progress_percentage") val progressPercentage: Double = 0.0,
    val categories: List<CategoryCount> = emptyList()
)

@Serializable
data class CategoryCount(
    val name: String,
    val count: Int
)

@Serializable
data class QuizAttemptRequest(
    @SerialName("is_correct") val isCorrect: Boolean
)

@Serializable
data class QuizAttemptResponse(
    val success: Boolean,
    @SerialName("times_practiced") val timesPracticed: Int = 0,
    @SerialName("times_correct") val timesCorrect: Int = 0,
    val accuracy: Double = 0.0
)