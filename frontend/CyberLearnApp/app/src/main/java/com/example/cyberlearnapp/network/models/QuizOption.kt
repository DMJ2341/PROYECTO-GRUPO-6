package com.example.cyberlearnapp.network.models

import com.google.gson.annotations.SerializedName

/**
 * Representa una opción de respuesta en un quiz
 */
data class QuizOption(
    @SerializedName("id")
    val id: String,

    @SerializedName("text")
    val text: String,

    @SerializedName("is_correct")
    val isCorrect: Boolean,

    @SerializedName("feedback")
    val feedback: String,

    @SerializedName("icon")
    val icon: String? = null,

    @SerializedName("explanation")
    val explanation: String? = null,

    @SerializedName("xp_value")
    val xpValue: Int = 10
)

/**
 * Representa una pregunta de quiz completa
 */
data class QuizQuestion(
    @SerializedName("id")
    val id: String,

    @SerializedName("question")
    val question: String,

    @SerializedName("type")
    val type: QuizType,

    @SerializedName("options")
    val options: List<QuizOption>,

    @SerializedName("multiple_answers")
    val multipleAnswers: Boolean = false,

    @SerializedName("time_limit")
    val timeLimit: Int? = null,  // En segundos

    @SerializedName("hint")
    val hint: String? = null
)

/**
 * Tipos de quiz
 */
enum class QuizType {
    @SerializedName("single_choice")
    SINGLE_CHOICE,        // Una sola respuesta correcta

    @SerializedName("multiple_choice")
    MULTIPLE_CHOICE,      // Múltiples respuestas correctas

    @SerializedName("true_false")
    TRUE_FALSE,           // Verdadero/Falso

    @SerializedName("ordering")
    ORDERING,             // Ordenar elementos

    @SerializedName("matching")
    MATCHING              // Emparejar elementos
}

/**
 * Respuesta del usuario a un quiz
 */
data class QuizAnswer(
    val questionId: String,
    val selectedOptionIds: List<String>,
    val isCorrect: Boolean,
    val timeSpent: Int,      // En segundos
    val attempts: Int = 1
)

/**
 * Resultado completo de un quiz
 */
data class QuizResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val score: Int,              // Porcentaje 0-100
    val xpEarned: Int,
    val timeSpent: Int,          // Tiempo total en segundos
    val answers: List<QuizAnswer>
) {
    val percentage: Float
        get() = if (totalQuestions > 0) {
            (correctAnswers.toFloat() / totalQuestions) * 100
        } else 0f
}