package com.example.cyberlearnapp.network.models.assessments

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- PREGUNTAS DEL TEST ---
@Serializable
data class PreferenceQuestion(
    val id: Int,
    val number: Int,
    val section: String,
    // Backend envía "text", no "question_text"
    @SerialName("text") val text: String,
    // Backend envía "subtext", no "question_subtext"
    @SerialName("subtext") val subtext: String? = null,
    val options: List<PreferenceOption>,
    @SerialName("image_url") val imageUrl: String? = null
)

@Serializable
data class PreferenceOption(
    val id: String,
    val text: String,
    val team: String, // "Red Team" | "Blue Team" | "Purple Team"
    val weight: Int
)

// --- RESULTADO DEL TEST ---
@Serializable
data class UserPreferenceResult(
    val profile: String, // "Red Team", etc.
    val confidence: String,
    @SerialName("secondary_profile") val secondaryProfile: String?,
    val scores: Scores,
    @SerialName("personality_traits") val personalityTraits: Map<String, String>,
    @SerialName("ui_data") val uiData: ProfileUiData? = null
)

@Serializable
data class Scores(
    val raw: Map<String, Int>,
    val normalized: Map<String, Float>
)

// --- UI DATA (Visuales) ---
@Serializable
data class ProfileUiData(
    val theme: Theme,
    val sections: List<Section>
)

@Serializable
data class Theme(
    val primary: String,
    val secondary: String,
    val bg: String,
    val gradient: String,
    val icon: String,
    @SerialName("badge_color") val badgeColor: String
)

// Usamos un modelo flexible para las secciones del JSON visual
@Serializable
data class Section(
    val type: String,
    val title: String? = null,
    val subtitle: String? = null,
    val description: String? = null,
    val bullets: List<String>? = null,
    val categories: List<Category>? = null,
    @SerialName("tool_categories") val toolCategories: List<ToolCategory>? = null,
    val roadmap: List<RoadmapLevel>? = null,
    val phases: List<Phase>? = null,
    // Para manejar objetos anidados genéricos si fuera necesario
    val stats: Map<String, String>? = null
)

@Serializable data class Category(val label: String, val skills: List<String>? = null)
@Serializable data class ToolCategory(val category: String, val tools: List<String>)
@Serializable data class RoadmapLevel(val level: String, val icon: String? = null, val certs: List<Cert>)
@Serializable data class Cert(val name: String, val cost: String? = null, val duration: String? = null, val priority: String? = null, val why: String? = null, val status: String? = null, val link: String? = null)
@Serializable data class Phase(val phase: String, val platforms: List<Platform>? = null, val activities: List<String>? = null)
@Serializable data class Platform(val name: String, val cost: String? = null, val link: String? = null, val description: String? = null, val rating: Int? = null, val paths: List<String>? = null)

// --- WRAPPERS DE RESPUESTA ---
@Serializable
data class PreferenceTestResponse(
    val success: Boolean,
    val total: Int,
    val questions: List<PreferenceQuestion>
)

@Serializable
data class SubmitPreferenceRequest(
    val answers: Map<String, String>,
    @SerialName("time_taken") val timeTaken: Int? = null
)

@Serializable
data class SubmitPreferenceResponse(
    val success: Boolean,
    val result: UserPreferenceResult
)

@Serializable
data class PreferenceResultWrapper(
    val success: Boolean,
    @SerialName("has_result") val hasResult: Boolean,
    val result: UserPreferenceResult? = null,
    val message: String? = null
)