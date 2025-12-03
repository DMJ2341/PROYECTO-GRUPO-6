// app/src/main/java/com/example/cyberlearnapp/network/ApiService.kt

package com.example.cyberlearnapp.network

import com.example.cyberlearnapp.models.*
import com.example.cyberlearnapp.network.models.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==========================================
    // 🔒 AUTH & PERFIL
    // ==========================================

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<AuthResponse>

    @POST("api/auth/resend-code")
    suspend fun resendVerificationCode(@Body request: ResendCodeRequest): Response<MessageResponse>

    // ✅ ESTE ES EL ÚNICO refreshToken - Tipo Call<> para uso síncrono en Authenticator
    @POST("api/auth/refresh")
    fun refreshToken(@Body request: Map<String, String>): Call<AuthResponse>

    @POST("api/auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String,
        @Body request: Map<String, String>
    ): Response<Unit>

    @GET("api/user/dashboard")
    suspend fun getDashboard(@Header("Authorization") token: String): Response<DashboardResponse>

    @GET("api/user/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<UserProfileResponse>

    @GET("api/user/badges")
    suspend fun getUserBadges(@Header("Authorization") token: String): Response<UserBadgesResponse>

    // ==========================================
    // 📚 CURSOS Y LECCIONES
    // ==========================================

    @GET("api/courses")
    suspend fun getCourses(): Response<List<Course>>

    @GET("api/courses/{courseId}/lessons")
    suspend fun getCourseLessons(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int
    ): Response<List<Lesson>>

    @GET("api/lessons/{lessonId}")
    suspend fun getLessonDetail(
        @Header("Authorization") token: String,
        @Path("lessonId") lessonId: String
    ): Response<LessonDetailResponse>

    @POST("api/progress/lesson/{lessonId}")
    suspend fun completeLesson(
        @Header("Authorization") token: String,
        @Path("lessonId") lessonId: String
    ): Response<LessonCompletionResponse>

    @GET("api/progress/course/{courseId}")
    suspend fun getCourseProgress(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int
    ): Response<Map<String, Any>>

    @GET("api/progress/all")
    suspend fun getAllProgress(@Header("Authorization") token: String): Response<Map<String, Any>>

    // ==========================================
    // 📖 GLOSARIO
    // ==========================================

    @GET("api/glossary")
    suspend fun getGlossaryTerms(
        @Header("Authorization") token: String
    ): Response<GlossaryResponse>

    @GET("api/glossary/search")
    suspend fun searchGlossaryTerms(
        @Header("Authorization") token: String,
        @Query("q") query: String? = null
    ): Response<GlossaryResponse>

    @POST("api/glossary/{glossaryId}/mark-learned")
    suspend fun markTermAsLearned(
        @Header("Authorization") token: String,
        @Path("glossaryId") glossaryId: Int,
        @Body request: MarkLearnedRequest
    ): Response<MarkLearnedResponse>

    @GET("api/glossary/learned")
    suspend fun getLearnedTerms(
        @Header("Authorization") token: String
    ): Response<GlossaryResponse>

    @GET("api/glossary/stats")
    suspend fun getGlossaryStats(
        @Header("Authorization") token: String
    ): Response<GlossaryStatsResponse>

    @POST("api/glossary/{glossaryId}/quiz-attempt")
    suspend fun recordQuizAttempt(
        @Header("Authorization") token: String,
        @Path("glossaryId") glossaryId: Int,
        @Body request: QuizAttemptRequest
    ): Response<QuizAttemptResponse>

    @GET("api/daily-term")
    suspend fun getDailyTerm(@Header("Authorization") token: String): Response<DailyTermWrapper>

    @POST("api/daily-term/complete")
    suspend fun completeDailyTerm(
        @Header("Authorization") token: String,
        @Body request: CompleteDailyTermRequest
    ): Response<CompleteDailyTermResponse>


    // ==========================================
    // 🎯 TEST DE PREFERENCIAS
    // ==========================================

    @GET("api/test/questions")
    suspend fun getQuestions(
        @Header("Authorization") token: String
    ): Response<TestQuestionsResponse>

    @POST("api/test/submit")
    suspend fun submitTest(
        @Header("Authorization") token: String,
        @Body submission: TestSubmission
    ): Response<TestSubmitResponse>

    @GET("api/test/recommendations/{role}")
    suspend fun getRecommendations(
        @Header("Authorization") token: String,
        @Path("role") role: String
    ): Response<RecommendationsResponse>

    @GET("api/test/result")
    suspend fun getUserResult(
        @Header("Authorization") token: String
    ): Response<UserTestResultResponse>

    @GET("api/test/history")
    suspend fun getHistory(
        @Header("Authorization") token: String
    ): Response<TestHistoryResponse>

    @POST("api/test/retake")
    suspend fun retakeTest(
        @Header("Authorization") token: String
    ): Response<BasicResponse>
}

data class BasicResponse(val success: Boolean, val message: String? = null)