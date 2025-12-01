package com.example.cyberlearnapp.network.models.assessments

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- REQUESTS & RESPONSES (Aquí integramos lo que tenías suelto) ---

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
    val result: UserPreferenceResult? = null
)

// --- MODELOS DE DATOS ---

@Serializable
data class PreferenceQuestion(
    val id: Int,
    val number: Int,
    val section: String? = null,
    @SerialName("text") val questionText: String,
    @SerialName("subtext") val questionSubtext: String? = null,
    val options: List<PreferenceOption>,
    @SerialName("image_url") val imageUrl: String? = null
)

@Serializable
data class PreferenceOption(
    val id: String,
    val text: String,
    val team: String? = null
)

@Serializable
data class UserPreferenceResult(
    val profile: String, // "Red Team", etc.
    val confidence: String,
    @SerialName("secondary_profile") val secondaryProfile: String? = null,
    @SerialName("scores") val scores: Scores? = null,
    @SerialName("ui_data") val uiData: ProfileUiData? = null
)

@Serializable
data class Scores(
    val normalized: Map<String, Double>
)

// --- UI DATA (Server-Driven UI) ---

@Serializable
data class ProfileUiData(
    val theme: ThemeColors,
    val sections: List<Section>
)

@Serializable
data class ThemeColors(
    val primary: String,
    val secondary: String,
    val accent: String
)

@Serializable
data class Section(
    val type: String,
    val title: String? = null,
    val subtitle: String? = null,
    val description: String? = null,
    @SerialName("lottie_url") val lottieUrl: String? = null,
    val bullets: List<String>? = null,
    val categories: List<SkillCategory>? = null,
    @SerialName("tool_categories") val toolCategories: List<ToolCategory>? = null,
    val roadmap: List<RoadmapLevel>? = null
)

@Serializable
data class SkillCategory(val label: String, val skills: List<String>)
@Serializable
data class ToolCategory(val category: String, val tools: List<String>)
@Serializable
data class RoadmapLevel(val level: String, val certs: List<Certification>)
@Serializable
data class Certification(val name: String, val provider: String? = null)