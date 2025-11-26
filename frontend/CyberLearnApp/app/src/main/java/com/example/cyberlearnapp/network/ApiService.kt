// app/src/main/java/com/example/cyberlearnapp/network/ApiService.kt

package com.example.cyberlearnapp.network

import com.example.cyberlearnapp.network.models.*
import com.example.cyberlearnapp.network.models.assessments.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>

    @POST("api/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<GeneralResponse>

    @GET("api/user/profile")
    suspend fun getProfile(): Response<UserProfileResponse>

    @GET("api/user/dashboard")
    suspend fun getDashboard(): Response<DashboardResponse>

    @GET("api/courses")
    suspend fun getCourses(): Response<List<Course>>

    @GET("api/courses/{courseId}/lessons")
    suspend fun getCourseLessons(@Path("courseId") courseId: Int): Response<List<Lesson>>

    @GET("api/lessons/{lessonId}")
    suspend fun getLessonDetail(@Path("lessonId") lessonId: String): Response<LessonResponse>

    @POST("api/progress/lesson/{lessonId}")
    suspend fun completeLesson(@Path("lessonId") lessonId: String): Response<LessonCompleteResponse>

    @GET("api/daily-term")
    suspend fun getDailyTerm(): Response<DailyTermWrapper>

    // NUEVO ENDPOINT PARA GANAR XP
    @POST("api/daily-term/complete")
    suspend fun completeDailyTerm(@Body request: CompleteDailyTermRequest): Response<CompleteDailyTermResponse>

    @GET("api/glossary")
    suspend fun getAllGlossaryTerms(): Response<List<GlossaryTerm>>

    @GET("api/glossary/search")
    suspend fun searchGlossaryTerms(@Query("q") query: String): Response<List<GlossaryTerm>>

<<<<<<< HEAD
    @GET("api/preference-test/questions")
    suspend fun getPreferenceQuestions(): Response<PreferenceQuestionResponse>

    @POST("api/preference-test/submit")
    suspend fun submitPreferenceTest(@Body request: SubmitPreferenceTestRequest): Response<PreferenceTestResponse>

    @GET("api/preference-test/result")
    suspend fun getPreferenceResult(): Response<PreferenceTestResultResponse>

    @POST("api/preference-test/retake")
    suspend fun retakePreferenceTest(): Response<GeneralResponse>

    @GET("api/exam/final")
    suspend fun getFinalExam(): Response<FinalExamQuestionsResponse>

    @POST("api/exam/final/submit")
    suspend fun submitFinalExam(@Body request: SubmitFinalExamRequest): Response<FinalExamResultResponse>
=======
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
>>>>>>> a214990271d474bb990db58170a8c35ed30d29c2
}