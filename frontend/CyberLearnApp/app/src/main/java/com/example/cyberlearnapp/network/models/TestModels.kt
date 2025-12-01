// app/src/main/java/com/example/cyberlearnapp/models/TestModels.kt
package com.example.cyberlearnapp.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Dimensiones de personalidad Holland Code (RIASEC)
 */
enum class PersonalityDimension(val displayName: String) {
    INVESTIGATIVE("Investigativo - Análisis y resolución"),
    REALISTIC("Realista - Técnico y práctico"),
    SOCIAL("Social - Colaboración y comunicación"),
    CONVENTIONAL("Convencional - Procesos y documentación"),
    ENTERPRISING("Emprendedor - Liderazgo y estrategia"),
    ARTISTIC("Artístico - Creatividad e innovación")
}

/**
 * Roles de ciberseguridad
 */
enum class CyberRole(
    val displayName: String,
    val emoji: String,
    val color: Long,
    val description: String
) {
    RED_TEAM(
        displayName = "Red Team",
        emoji = "⚔️",
        color = 0xFFE53935, // Rojo
        description = "Especialista en seguridad ofensiva - Pentesting y hacking ético"
    ),
    BLUE_TEAM(
        displayName = "Blue Team",
        emoji = "🛡️",
        color = 0xFF1E88E5, // Azul
        description = "Especialista en seguridad defensiva - SOC y respuesta a incidentes"
    ),
    PURPLE_TEAM(
        displayName = "Purple Team",
        emoji = "🔮",
        color = 0xFF8E24AA, // Púrpura
        description = "Puente entre ofensiva y defensiva - Colaboración y mejora continua"
    );

    companion object {
        fun fromString(value: String): CyberRole? {
            return when(value) {
                "RED_TEAM" -> RED_TEAM
                "BLUE_TEAM" -> BLUE_TEAM
                "PURPLE_TEAM" -> PURPLE_TEAM
                else -> null
            }
        }
    }
}

/**
 * Pregunta del test
 */
@Serializable
data class TestQuestion(
    val id: Int,
    val question: String,
    val emoji: String,
    val category: String,
    val order: Int
)

/**
 * Respuesta del endpoint /api/test/questions
 */
@Serializable
data class TestQuestionsResponse(
    val success: Boolean,
    val questions: List<TestQuestion>,
    val total: Int
)

/**
 * Body para enviar respuestas
 */
@Serializable
data class TestSubmission(
    val answers: Map<String, Int>, // question_id -> rating (1-5)
    @SerialName("time_taken")
    val timeTaken: Int? = null // segundos opcionales
)

/**
 * Resultado del test
 */
@Serializable
data class TestResult(
    val id: Int,
    @SerialName("recommended_role")
    val recommendedRole: String,
    val confidence: Float,
    val scores: Map<String, Int>,
    @SerialName("top_dimensions")
    val topDimensions: List<String>,
    @SerialName("created_at")
    val createdAt: String
)

/**
 * Response de submit test
 */
@Serializable
data class TestSubmitResponse(
    val success: Boolean,
    val result: TestResult
)

/**
 * Certificación
 */
@Serializable
data class Certification(
    val id: Int,
    val name: String,
    val provider: String,
    @SerialName("is_free")
    val isFree: Boolean,
    val url: String,
    val difficulty: String,
    val description: String,
    @SerialName("price_info")
    val priceInfo: String?
)

/**
 * Laboratorio
 */
@Serializable
data class Lab(
    val id: Int,
    val name: String,
    val platform: String,
    val url: String,
    @SerialName("is_free")
    val isFree: Boolean,
    val description: String,
    val difficulty: String?
)

/**
 * Learning Path
 */
@Serializable
data class LearningPath(
    val id: Int,
    val name: String,
    val platform: String,
    val url: String,
    @SerialName("estimated_hours")
    val estimatedHours: Int,
    val description: String,
    @SerialName("is_free")
    val isFree: Boolean
)

/**
 * Skill
 */
@Serializable
data class RoleSkill(
    val id: Int,
    val skill: String
)

/**
 * Referencia académica
 */
@Serializable
data class AcademicReference(
    val reference: String
)

/**
 * Recomendaciones completas
 */
@Serializable
data class Recommendations(
    val role: String,
    val certifications: List<Certification>,
    val labs: List<Lab>,
    @SerialName("learning_paths")
    val learningPaths: List<LearningPath>,
    val skills: List<RoleSkill>,
    @SerialName("academic_reference")
    val academicReference: AcademicReference?
)

/**
 * Response de recomendaciones
 */
@Serializable
data class RecommendationsResponse(
    val success: Boolean,
    val recommendations: Recommendations
)

/**
 * Response de resultado del usuario
 */
@Serializable
data class UserTestResultResponse(
    val success: Boolean,
    @SerialName("has_result")
    val hasResult: Boolean,
    val result: TestResult? = null
)

/**
 * Item de historial
 */
@Serializable
data class TestHistoryItem(
    val id: Int,
    @SerialName("recommended_role")
    val recommendedRole: String,
    val confidence: Float,
    @SerialName("created_at")
    val createdAt: String
)

/**
 * Response de historial
 */
@Serializable
data class TestHistoryResponse(
    val success: Boolean,
    val history: List<TestHistoryItem>,
    val total: Int
)