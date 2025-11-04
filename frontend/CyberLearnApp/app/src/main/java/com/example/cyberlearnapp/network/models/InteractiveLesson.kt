package com.example.cyberlearnapp.network.models

import com.google.gson.annotations.SerializedName

data class InteractiveLessonResponse(
    val success: Boolean,
    val lesson: InteractiveLesson
)

data class InteractiveLesson(
    @SerializedName("lesson_id") val lessonId: String,
    @SerializedName("course_id") val courseId: String,
    val title: String,
    @SerializedName("lesson_order") val lessonOrder: Int,
    @SerializedName("xp_reward") val xpReward: Int,
    @SerializedName("duration_minutes") val durationMinutes: Int,
    @SerializedName("lesson_type") val lessonType: String,
    @SerializedName("total_screens") val totalScreens: Int,
    val screens: List<LessonScreen>
)

data class LessonScreen(
    @SerializedName("screen_number") val screenNumber: Int,
    val type: String,
    val title: String,
    val subtitle: String? = null,
    val content: Map<String, Any>? = null,
    @SerializedName("cta_button") val ctaButton: String? = null,
    @SerializedName("email_data") val emailData: EmailData? = null,
    val signals: List<Signal>? = null,
    val hint: String? = null,
    val items: List<ChecklistItem>? = null,
    val tip: String? = null,
    val steps: List<ActionStep>? = null,
    val reminder: String? = null,
    val questions: List<QuizQuestion>? = null
)

data class EmailData(
    val from: String,
    val to: String,
    val subject: String,
    val body: String
)

data class Signal(
    val id: Int,
    val name: String,
    val element: String,
    @SerializedName("correct_value") val correctValue: String? = null,
    val explanation: String,
    val xp: Int
)

data class ChecklistItem(
    val id: Int,
    val name: String,
    val description: String
)

data class ActionStep(
    val number: Int,
    val icon: String,
    val title: String,
    val actions: List<String>,
    val type: String
)

data class QuizQuestion(
    val id: Int,
    val scenario: String,
    val question: String,
    val options: List<QuizOption>,
    @SerializedName("correct_answer") val correctAnswer: String,
    val explanation: String,
    val xp: Int
)

data class QuizOption(
    val id: String,
    val text: String,
    val correct: Boolean
)

data class LessonProgressRequest(
    @SerializedName("current_screen") val currentScreen: Int,
    @SerializedName("completed_screens") val completedScreens: List<Int>,
    @SerializedName("signals_found") val signalsFound: List<Int>,
    @SerializedName("quiz_answers") val quizAnswers: Map<String, String>
)

data class SuccessResponse(
    val success: Boolean,
    val message: String? = null
)