package com.example.cyberlearnapp.network

import com.example.cyberlearnapp.network.models.*
import com.example.cyberlearnapp.network.models.assessments.*
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
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/refresh")
    fun refreshToken(@Body request: Map<String, String>): Call<AuthResponse>

    @POST("api/auth/logout")
    // ✅ CORREGIDO: Usamos Response<Unit> para la simplicidad de la respuesta 200 OK.
    suspend fun logout(@Header("Authorization") token: String, @Body request: Map<String, String>): Response<Unit>

    @GET("api/user/dashboard")
    suspend fun getDashboard(@Header("Authorization") token: String): Response<DashboardResponse>

    @GET("api/user/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<UserProfileResponse>

    // ==========================================
    // 📚 CURSOS Y LECCIONES
    // ==========================================

    @GET("api/courses")
    suspend fun getCourses(): Response<List<Course>>

    @GET("api/courses/{courseId}/lessons")
    suspend fun getCourseLessons(
        @Path("courseId") courseId: Int
    ): Response<List<Lesson>>

    @GET("api/lessons/{lessonId}")
    suspend fun getLessonDetail(
        @Header("Authorization") token: String,
        @Path("lessonId") lessonId: String
    ): Response<LessonResponse>

    @POST("api/progress/lesson/{lessonId}")
    suspend fun completeLesson(
        @Header("Authorization") token: String,
        @Path("lessonId") lessonId: String
    ): Response<LessonCompletionResponse>

    @GET("api/progress/course/{courseId}")
    // ✅ CORREGIDO: El backend devuelve un JSON de progreso. Usamos Map<String, Any> o un modelo simple.
    // Dado que el backend devuelve un objeto pequeño (percentage, completed_lessons, etc.),
    // asumimos que un modelo simple de progreso existe o usamos Map<String, Any> si no lo encontramos.
    // Usaremos Map<String, Any> para evitar errores de modelo no encontrado.
    suspend fun getCourseProgress(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: Int
    ): Response<Map<String, Any>>

    @GET("api/progress/all")
    // ✅ CORREGIDO: El backend devuelve un wrapper con una lista de progreso (List<CourseProgress>).
    // Usamos Map<String, Any> para capturar el wrapper {"success": true, "courses": [...]}
    suspend fun getAllProgress(@Header("Authorization") token: String): Response<Map<String, Any>>

    // ==========================================
    // 📖 GLOSARIO & TÉRMINO DIARIO
    // ==========================================

    @GET("api/daily-term")
    suspend fun getDailyTerm(@Header("Authorization") token: String): Response<DailyTermWrapper>

    @POST("api/daily-term/complete")
    suspend fun completeDailyTerm(
        @Header("Authorization") token: String,
        @Body request: CompleteDailyTermRequest
    ): Response<CompleteDailyTermResponse>

    @GET("api/glossary")
    // El backend devuelve {"success": true, "terms": [...]} (GlossaryResponse)
    suspend fun getGlossaryTerms(): Response<GlossaryResponse>

    @GET("api/glossary/search")
    suspend fun searchGlossaryTerms(
        @Query("q") query: String? = null
    ): Response<GlossaryResponse>

    @POST("api/glossary/{glossaryId}/favorite")
    // ✅ CORREGIDO: Respuesta personalizada del backend. Usamos Map<String, Any>
    suspend fun toggleGlossaryFavorite(
        @Header("Authorization") token: String,
        @Path("glossaryId") glossaryId: Int
    ): Response<Map<String, Any>> // Respuesta: {"success": true, "is_favorite": true, "action": "added"}

    // ==========================================
    // 🎓 EVALUACIONES
    // ==========================================

    @GET("api/exam/final")
    suspend fun getFinalExam(@Header("Authorization") token: String): Response<ExamStartResponse>

    @POST("api/exam/final/submit")
    suspend fun submitFinalExam(
        @Header("Authorization") token: String,
        @Body body: ExamSubmitRequest
    ): Response<ExamResultResponse>

    @GET("api/preference-test/questions")
    suspend fun getPreferenceQuestions(@Header("Authorization") token: String): Response<PreferenceTestResponse>

    @POST("api/preference-test/submit")
    suspend fun submitPreferenceTest(
        @Header("Authorization") token: String,
        @Body body: SubmitPreferenceRequest
    ): Response<SubmitPreferenceResponse>

    @GET("api/preference-test/result")
    suspend fun getPreferenceResult(@Header("Authorization") token: String): Response<PreferenceResultWrapper>

    @POST("api/preference-test/retake")
    // ✅ CORREGIDO: Usamos Response<Unit> para la simplicidad de la respuesta 200 OK.
    suspend fun retakePreferenceTest(@Header("Authorization") token: String): Response<Unit>
}