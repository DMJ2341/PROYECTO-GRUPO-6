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

    // --- CAMPOS QUE VIENEN DEL JSON ---
    @SerialName("term_en") val termEn: String,
    @SerialName("term_es") val termEs: String,

    @SerialName("definition_en") val definitionEn: String,
    @SerialName("definition_es") val definitionEs: String,

    // Metadata opcional
    val acronym: String? = null,
    val category: String? = null,
    val difficulty: String? = null,

    @SerialName("example_en") val exampleEn: String? = null,
    @SerialName("example_es") val exampleEs: String? = null,

    // Progreso
    @SerialName("is_learned") val isLearned: Boolean = false,

    // CORRECCIÓN: El logcat mostraba "times_reviewed", no "times_practiced"
    @SerialName("times_reviewed") val timesReviewed: Int = 0
) {
    // --- PROPIEDADES COMPUTADAS (NO SERIALIZABLES) ---
    // Al estar fuera del constructor, la librería no las busca en el JSON.
    // Sirven para facilitar el uso en la UI sin cambiar todo tu código.

    val term: String
        get() = termEs // Retorna español por defecto

    val definition: String
        get() = definitionEs

    val example: String?
        get() = exampleEs

    // Alias por si alguna parte de tu código usa 'timesPracticed'
    val timesPracticed: Int
        get() = timesReviewed
}

// --- OTROS MODELOS ---

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
    // Aquí el servidor podría responder distinto, pero mantenemos tu estructura por si acaso
    @SerialName("times_practiced") val timesPracticed: Int = 0,
    @SerialName("times_correct") val timesCorrect: Int = 0,
    val accuracy: Double = 0.0
)