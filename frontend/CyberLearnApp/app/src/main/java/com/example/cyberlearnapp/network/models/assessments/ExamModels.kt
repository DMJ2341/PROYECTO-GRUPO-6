package com.example.cyberlearnapp.network.models.assessments

import kotlinx.serialization.Serializable

// --- REQUESTS & RESPONSES ---

@Serializable
data class ExamStartResponse(
    val questions: List<FinalExamQuestion>,
    val attempt_number: Int,
    val max_attempts: Int = 3
)

@Serializable
data class ExamSubmitRequest(
    val answers: Map<String, String>
)

@Serializable
data class ExamResultResponse(
    val score: Float,
    val max_score: Float,
    val percentage: Float,
    val passed: Boolean,
    val grade: String,
    val new_badge: String? = null
)

// --- MODELOS DE DATOS ---

@Serializable
data class FinalExamQuestion(
    val id: Int,
    val section: String,
    val question_text: String,
    val options: List<ExamOption>? = null,
    val correct_answer: List<String>? = null,
    val points: Float = 1f
)

@Serializable
data class ExamOption(
    val id: String,
    val text: String
)