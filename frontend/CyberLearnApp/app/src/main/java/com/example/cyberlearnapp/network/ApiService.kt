package com.example.cyberlearnapp.network

import com.example.cyberlearnapp.network.models.InteractiveLessonResponse
import com.example.cyberlearnapp.network.models.LessonProgressRequest
import com.example.cyberlearnapp.network.models.SuccessResponse
import com.example.cyberlearnapp.network.models.Progress
import com.example.cyberlearnapp.network.models.User
import com.example.cyberlearnapp.network.models.CompleteActivityRequest
import com.example.cyberlearnapp.network.models.CompleteActivityResponse
import com.example.cyberlearnapp.network.models.BadgeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/user/progress")
    suspend fun getUserProgress(@Header("Authorization") token: String): Response<Progress>

    @POST("/api/user/complete-activity")
    suspend fun completeActivity(
        @Header("Authorization") token: String,
        @Body request: CompleteActivityRequest
    ): Response<CompleteActivityResponse>

    @GET("/api/user/badges")
    suspend fun getUserBadges(
        @Header("Authorization") token: String
    ): Response<BadgeResponse>

    @GET("api/courses/{courseId}/lessons")
    suspend fun getCourseLessons(
        @Path("courseId") courseId: String,
        @Header("Authorization") token: String
    ): Response<LessonsResponse>

    @GET("api/lessons/{lessonId}")
    suspend fun getLessonContent(
        @Path("lessonId") lessonId: String,
        @Header("Authorization") token: String
    ): Response<Lesson>

    @GET("lessons/{lessonId}/interactive")
    suspend fun getInteractiveLesson(
        @Path("lessonId") lessonId: String,
        @Header("Authorization") token: String
    ): Response<InteractiveLessonResponse>

    @POST("lessons/{lessonId}/progress")
    suspend fun saveLessonProgress(
        @Path("lessonId") lessonId: String,
        @Header("Authorization") token: String,
        @Body progress: LessonProgressRequest
    ): Response<SuccessResponse>
}

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

data class Lesson(
    val id: String,
    val title: String,
    val content: String = "",
    val order: Int = 0,
    val xp_reward: Int = 0,
    val duration_minutes: Int = 0,
    val course_id: String = ""
)

data class LessonsResponse(
    val lessons: List<Lesson>
)