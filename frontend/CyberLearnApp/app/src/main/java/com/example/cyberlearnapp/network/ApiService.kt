package com.example.cyberlearnapp.network

import com.example.cyberlearnapp.network.models.*
import retrofit2.http.*

/**
 * API Service - Endpoints del backend
 * ACTUALIZADO: Agregados endpoints para lecciones interactivas
 */
interface ApiService {

    // ========== AUTENTICACIÓN ==========
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    // ========== USUARIO ==========
    @GET("users/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): User

    @GET("users/dashboard")
    suspend fun getDashboard(@Header("Authorization") token: String): DashboardData

    // ========== CURSOS ==========
    @GET("courses")
    suspend fun getCourses(@Header("Authorization") token: String): List<Course>

    @GET("courses/{courseId}")
    suspend fun getCourseDetail(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int
    ): CourseDetail

    // 🆕 NUEVO: Obtener lecciones de un curso
    @GET("courses/{courseId}/lessons")
    suspend fun getCourseLessons(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int
    ): List<Lesson>

    // ========== LECCIONES ==========
    @GET("lessons/{lessonId}")
    suspend fun getLesson(
        @Header("Authorization") token: String,
        @Path("lessonId") lessonId: Int
    ): Lesson

    // 🆕 NUEVO: Obtener lección interactiva
    @GET("lessons/{lessonId}/interactive")
    suspend fun getInteractiveLesson(
        @Path("lessonId") lessonId: Int
    ): InteractiveLesson

    // ========== ACTIVIDADES ==========
    @POST("activities/start")
    suspend fun startActivity(
        @Header("Authorization") token: String,
        @Body data: Map<String, Any>
    ): ActivityResponse

    @POST("activities/complete")
    suspend fun completeActivity(
        @Header("Authorization") token: String,
        @Body data: Map<String, Any>
    ): ActivityResponse

    // 🆕 NUEVO: Completar lección interactiva
    @POST("lessons/complete")
    suspend fun completeLesson(
        @Body data: Map<String, Any>
    ): ActivityResponse

    // 🆕 NUEVO: Registrar actividad de lección
    @POST("lessons/activity")
    suspend fun recordLessonActivity(
        @Body data: Map<String, Any>
    ): ActivityResponse

    // ========== BADGES ==========
    @GET("badges")
    suspend fun getBadges(@Header("Authorization") token: String): List<Badge>

    @GET("badges/user")
    suspend fun getUserBadges(@Header("Authorization") token: String): List<UserBadge>
}

data class ActivityResponse(
    val success: Boolean,
    val message: String,
    val xp_earned: Int? = null,
    val new_badges: List<Badge>? = null
)

data class DashboardData(
    val user: User,
    val total_xp: Int,
    val current_streak: Int,
    val badges_count: Int,
    val courses_progress: List<CourseProgress>
)

data class CourseProgress(
    val course_id: Int,
    val course_title: String,
    val completed_lessons: Int,
    val total_lessons: Int,
    val progress_percentage: Int
)