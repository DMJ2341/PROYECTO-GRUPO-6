package com.example.cyberlearnapp.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ========== AUTH ==========
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // ========== COURSES ==========
    @GET("courses")
    suspend fun getCourses(): Response<List<Course>>

    @GET("courses/{courseId}/lessons")
    suspend fun getCourseLessons(@Path("courseId") courseId: Int): Response<List<Lesson>>

    // ========== LESSONS ==========
    // ✅ CORREGIDO: lesson_id es String
    @GET("lessons/{lessonId}")
    suspend fun getLessonContent(@Path("lessonId") lessonId: String): Response<LessonDetailResponse>

    @GET("courses/{courseId}/lessons/{lessonId}")
    suspend fun getCourseLessonDetail(
        @Path("courseId") courseId: Int,
        @Path("lessonId") lessonId: String  // ✅ String
    ): Response<LessonDetailResponse>

    // ========== PROGRESS ==========
    @POST("lessons/{lessonId}/progress")
    suspend fun updateLessonProgress(
        @Path("lessonId") lessonId: String,  // ✅ String
        @Header("Authorization") token: String,
        @Body request: LessonProgressRequest
    ): Response<LessonProgressResponse>

    // ========== USER ==========
    @GET("user/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<UserProfileResponse>

    @GET("user/dashboard")
    suspend fun getDashboard(@Header("Authorization") token: String): Response<DashboardResponse>

    @GET("user/badges")
    suspend fun getUserBadges(@Header("Authorization") token: String): Response<UserBadgesResponse>

    @GET("user/streak")
    suspend fun getUserStreak(@Header("Authorization") token: String): Response<StreakResponse>

    // ========== BADGES ==========
    @GET("badges/available")
    suspend fun getAvailableBadges(): Response<BadgesListResponse>
}

// ========== REQUEST MODELS ==========
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LessonProgressRequest(
    val completed: Boolean
)

// ========== RESPONSE MODELS ==========
data class AuthResponse(
    val success: Boolean,
    val token: String?,
    val user: User?,
    val message: String?,
    val error: String?
)

data class User(
    val id: Int,
    val email: String,
    val name: String
)

data class Course(
    val id: Int,
    val title: String,
    val description: String,
    val level: String,
    val xp_reward: Int,
    val image_url: String
)

// ✅ NUEVO: Modelo para lista de lecciones
data class Lesson(
    val id: String,  // ✅ String ahora
    val course_id: Int,
    val title: String,
    val description: String?,
    val type: String,
    val duration_minutes: Int,
    val xp_reward: Int,
    val order_index: Int,
    val is_completed: Boolean
)

// ✅ NUEVO: Modelo para detalle de lección
data class LessonDetailResponse(
    val success: Boolean,
    val id: String,  // ✅ String
    val title: String,
    val description: String?,
    val content: String?,
    val type: String,
    val screens: String?,
    val total_screens: Int,
    val duration_minutes: Int,
    val xp_reward: Int,
    val order_index: Int,
    val created_at: String?
)

data class LessonProgressResponse(
    val success: Boolean,
    val lesson_id: String,  // ✅ String
    val completed: Boolean,
    val points_earned: Int
)

data class UserProfileResponse(
    val success: Boolean,
    val user: User
)

data class DashboardResponse(
    val success: Boolean,
    val dashboard: Dashboard
)

data class Dashboard(
    val total_xp: Int,
    val current_streak: Int,
    val streak_bonus: Int,
    val badges_count: Int,
    val courses_progress: List<CourseProgress>,
    val next_badge: NextBadge?
)

data class CourseProgress(
    val course_id: Int,
    val course_title: String,
    val completed_lessons: Int,
    val total_lessons: Int,
    val progress_percent: Double
)

data class NextBadge(
    val name: String,
    val description: String,
    val icon: String,
    val condition: String
)

data class UserBadgesResponse(
    val success: Boolean,
    val badges: List<Badge>
)

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val xp_required: Int,
    val earned_at: String?,
    val earned_value: Int?
)

data class BadgesListResponse(
    val success: Boolean,
    val badges: List<Badge>
)

data class StreakResponse(
    val success: Boolean,
    val streak: Streak
)

data class Streak(
    val current_days: Int,
    val bonus_xp: Int,
    val next_milestone: Int,
    val progress_to_next: Int
)