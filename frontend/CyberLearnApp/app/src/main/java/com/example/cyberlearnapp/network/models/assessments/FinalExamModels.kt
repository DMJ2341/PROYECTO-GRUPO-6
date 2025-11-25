package com.example.cyberlearnapp.network.models.assessments

import kotlinx.serialization.Serializable

@Serializable
data class FinalExamQuestion(
    val id: Int,
    val section: String,           // "multiple_choice", "case_study", "design"
    val question_text: String,
    val options: List<ExamOption>? = null,
    val correct_answer: List<String>,
    val points: Float = 1f
)

@Serializable
data class ExamOption(
    val id: String,
    val text: String
)

@Serializable
data class ExamStartResponse(
    val questions: List<FinalExamQuestion>,
    val attempt_number: Int,
    val max_attempts: Int = 3
)

@Serializable
data class ExamSubmitRequest(
    val answers: Map<String, String>  // "1" -> "a", "2" -> "b,c"
)

@Serializable
data class ExamResultResponse(
    val score: Float,
    val max_score: Float,
    val percentage: Float,
    val passed: Boolean,           // >= 70%
    val grade: String,             // "Competent", "Proficient", "Expert"
    val new_badge: String?         // ej: "CyberLearn Expert"
)