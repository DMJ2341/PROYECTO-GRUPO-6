package com.example.cyberlearnapp.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // ✅ ENDPOINTS CORREGIDOS según tu backend real
    @GET("user/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<UserProfileResponse>

    @GET("courses")
    suspend fun getAllCourses(): Response<List<Course>>

    @GET("courses/{courseId}/lessons")
    suspend fun getCourseLessons(
        @Path("courseId") courseId: String
    ): Response<List<Lesson>>

    @GET("lessons/{lessonId}")
    suspend fun getLessonContent(
        @Path("lessonId") lessonId: String
    ): Response<Lesson>

    @POST("lessons/{lessonId}/progress")
    suspend fun saveLessonProgress(
        @Path("lessonId") lessonId: String,
        @Header("Authorization") token: String,
        @Body progress: LessonProgressRequest
    ): Response<SuccessResponse>

    @GET("user/badges")
    suspend fun getUserBadges(
        @Header("Authorization") token: String
    ): Response<BadgeResponse>

    @GET("user/dashboard")
    suspend fun getUserDashboard(@Header("Authorization") token: String): Response<DashboardResponse>
}

// MODELOS ACTUALIZADOS
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: User?
)

data class User(
    val id: Int,
    val email: String,
    val name: String
)

data class Course(
    val id: String,
    val title: String,
    val description: String,
    val level: String,
    val xp_reward: Int,
    val image_url: String,
    val category: String? = null,
    val duration_hours: Int? = null
)

data class Lesson(
    val id: String,
    val course_id: String,
    val title: String,
    val description: String?,
    val content: String?,
    val order_index: Int,
    val type: String,
    val duration_minutes: Int?,
    val xp_reward: Int? = 0,
    val total_screens: Int? = 1,
    val screens: String? = null
)

data class UserProfileResponse(
    val success: Boolean,
    val user: UserProfile?
)

data class UserProfile(
    val id: Int,
    val email: String,
    val name: String,
    val created_at: String?
)

data class DashboardResponse(
    val success: Boolean,
    val dashboard: DashboardData?
)

data class DashboardData(
    val total_xp: Int,
    val current_streak: Int,
    val streak_bonus: Int,
    val badges_count: Int,
    val courses_progress: List<CourseProgress>
)

data class CourseProgress(
    val course_id: String,
    val course_title: String,
    val completed_lessons: Int,
    val total_lessons: Int,
    val progress_percent: Double
)

data class BadgeResponse(
    val success: Boolean,
    val badges: List<Badge>
)

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val condition: String?,
    val points_required: Int,
    val earned_at: String? = null
)

data class LessonProgressRequest(
    val completed: Boolean = true
)

data class SuccessResponse(
    val success: Boolean,
    val message: String
)