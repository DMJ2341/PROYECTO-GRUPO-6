package com.example.cyberlearnapp.network

import com.example.cyberlearnapp.network.models.*
import com.example.cyberlearnapp.network.models.assessments.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTH ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/refresh")
    fun refreshToken(@Body request: Map<String, String>): Call<AuthResponse>

    // --- DASHBOARD & USER ---
    @GET("user/dashboard")
    suspend fun getDashboard(@Header("Authorization") token: String): Response<DashboardResponse>

    @GET("user/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<UserResponse>

    @GET("daily-term")
    suspend fun getDailyTerm(@Header("Authorization") token: String): Response<DailyTermWrapper>

    // --- CURSOS & LECCIONES ---
    @GET("courses")
    suspend fun getCourses(@Header("Authorization") token: String): Response<List<Course>>

    @GET("courses/{courseId}/lessons")
    suspend fun getCourseLessons(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int
    ): Response<List<Lesson>>

    @GET("lessons/{lessonId}")
    suspend fun getLessonDetail(
        @Header("Authorization") token: String,
        @Path("lessonId") lessonId: String
    ): Response<LessonResponse>

    // ✅ MODIFICADO: Ahora devuelve LessonCompletionResponse
    @POST("progress/lesson/{lessonId}")
    suspend fun completeLesson(
        @Header("Authorization") token: String,
        @Path("lessonId") lessonId: String
    ): Response<LessonCompletionResponse>

    // --- ✅ NUEVO: GLOSARIO ---
    @GET("glossary")
    suspend fun getGlossaryTerms(
        @Header("Authorization") token: String,
        @Query("q") query: String? = null
    ): Response<GlossaryResponse>

    // --- EXÁMENES ---
    @GET("preference-test/questions")
    suspend fun getPreferenceQuestions(@Header("Authorization") token: String): Response<PreferenceTestResponse>

    @POST("preference-test/submit")
    suspend fun submitPreferenceTest(
        @Header("Authorization") token: String,
        @Body body: SubmitPreferenceRequest
    ): Response<SubmitPreferenceResponse>

    @GET("preference-test/result")
    suspend fun getPreferenceResult(@Header("Authorization") token: String): Response<PreferenceResultWrapper>

    // ✅ CORREGIDO: Rutas alineadas con el backend (exam/final)
    @GET("exam/final")
    suspend fun getFinalExam(@Header("Authorization") token: String): Response<ExamStartResponse>

    @POST("exam/final/submit")
    suspend fun submitFinalExam(
        @Header("Authorization") token: String,
        @Body body: ExamSubmitRequest
    ): Response<ExamResultResponse>

    @GET("api/daily-term")
    suspend fun getDailyTerm(): Response<DailyTermWrapper>

    // ✅ NUEVO ENDPOINT PARA GANAR XP
    @POST("api/daily-term/complete")
    suspend fun completeDailyTerm(@Body request: CompleteDailyTermRequest): Response<CompleteDailyTermResponse>

    @GET("api/glossary")
    suspend fun getAllGlossaryTerms(): Response<List<GlossaryTerm>>
}