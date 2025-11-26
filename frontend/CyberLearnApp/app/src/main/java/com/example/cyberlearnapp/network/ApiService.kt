package com.example.cyberlearnapp.network

import com.example.cyberlearnapp.network.models.*
import com.example.cyberlearnapp.network.models.assessments.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTH ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // --- DASHBOARD & USER ---
    @GET("user/dashboard")
    suspend fun getDashboard(@Header("Authorization") token: String): Response<DashboardResponse>

    @GET("user/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<UserResponse>

    @GET("daily-term")
    suspend fun getDailyTerm(@Header("Authorization") token: String): Response<DailyTermWrapper>

    // --- CURSOS ---
    @GET("courses")
    suspend fun getCourses(@Header("Authorization") token: String): Response<List<Course>>

    @GET("courses/{courseId}/lessons")
    suspend fun getCourseLessons(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int
    ): Response<List<Lesson>>

    // --- LECCIONES (NUEVO SISTEMA) ---
    @GET("lessons/{lessonId}")
    suspend fun getLesson(
        @Header("Authorization") token: String,
        @Path("lessonId") lessonId: String
    ): Response<LessonResponse>

    @POST("progress/lesson/{lessonId}")
    suspend fun completeLesson(
        @Header("Authorization") token: String,
        @Path("lessonId") lessonId: String
    ): Response<Unit>

    // --- ASSESSMENTS (Test Vocacional & Examen Final) ---
    @GET("preference-test/questions")
    suspend fun getPreferenceQuestions(@Header("Authorization") token: String): Response<PreferenceTestResponse>

    @POST("preference-test/submit")
    suspend fun submitPreferenceTest(
        @Header("Authorization") token: String,
        @Body body: SubmitPreferenceRequest
    ): Response<SubmitPreferenceResponse>

    @GET("preference-test/result")
    suspend fun getPreferenceResult(@Header("Authorization") token: String): Response<PreferenceResultWrapper>

    @POST("final-exam/start")
    suspend fun startFinalExam(@Header("Authorization") token: String): Response<ExamStartResponse>

    @POST("final-exam/submit")
    suspend fun submitFinalExam(
        @Header("Authorization") token: String,
        @Body body: ExamSubmitRequest
    ): Response<ExamResultResponse>
}