package com.example.cyberlearnapp.network.models

import com.google.gson.annotations.SerializedName

/**
 * Representa una pantalla dentro de una lección interactiva
 */
data class Screen(
    @SerializedName("screen_number")
    val screenNumber: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("type")
    val type: ScreenType,

    @SerializedName("content")
    val content: ScreenContent
)

/**
 * Tipos de pantallas interactivas
 */
enum class ScreenType {
    @SerializedName("story_hook")
    STORY_HOOK,           // Caso real con impacto

    @SerializedName("infographic")
    INFOGRAPHIC,          // Mapa/Estadísticas

    @SerializedName("classifier")
    CLASSIFIER,           // Drag & Drop

    @SerializedName("simulator")
    SIMULATOR,            // Simulación interactiva

    @SerializedName("quiz")
    QUIZ,                 // Pregunta con opciones

    @SerializedName("summary")
    SUMMARY               // Resumen y recompensas
}

/**
 * Contenido flexible según el tipo de pantalla
 */
data class ScreenContent(
    // Para STORY_HOOK
    @SerializedName("case_title")
    val caseTitle: String? = null,

    @SerializedName("date")
    val date: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("impact_cards")
    val impactCards: List<ImpactCard>? = null,

    // Para CLASSIFIER
    @SerializedName("scenario")
    val scenario: String? = null,

    @SerializedName("categories")
    val categories: List<Category>? = null,

    // Para SIMULATOR
    @SerializedName("simulator_type")
    val simulatorType: String? = null,  // "phishing", "ddos", "wifi", etc.

    @SerializedName("initial_state")
    val initialState: Map<String, Any>? = null,

    // Para QUIZ
    @SerializedName("question")
    val question: String? = null,

    @SerializedName("options")
    val options: List<QuizOption>? = null,

    // Para SUMMARY
    @SerializedName("achievements")
    val achievements: List<String>? = null,

    @SerializedName("xp_reward")
    val xpReward: Int? = null,

    @SerializedName("badge_id")
    val badgeId: Int? = null,

    // General
    @SerializedName("key_message")
    val keyMessage: String? = null
)

/**
 * Tarjeta de impacto (Story Hook)
 */
data class ImpactCard(
    @SerializedName("icon")
    val icon: String,           // "💰", "👥", "🏥"

    @SerializedName("value")
    val value: String,          // "40M", "$4.4M"

    @SerializedName("label")
    val label: String,          // "Tarjetas Robadas"

    @SerializedName("detail")
    val detail: String? = null  // Texto expandible
)

/**
 * Categoría para clasificador
 */
data class Category(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("icon")
    val icon: String,

    @SerializedName("color")
    val color: String
)

/**
 * Opción de quiz
 */
data class QuizOption(
    @SerializedName("id")
    val id: String,

    @SerializedName("text")
    val text: String,

    @SerializedName("is_correct")
    val isCorrect: Boolean,

    @SerializedName("feedback")
    val feedback: String
)