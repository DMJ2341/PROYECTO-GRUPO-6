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

    // --- DASHBOARD ---
    @GET("user/dashboard")
    suspend fun getDashboard(@Header("Authorization") token: String): Response<DashboardResponse>

    @GET("user/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<UserResponse>

    // --- DAILY TERM ---
    @GET("daily-term")
    suspend fun getDailyTerm(@Header("Authorization") token: String): Response<DailyTermWrapper>

    // --- PREFERENCE TEST (Rutas corregidas: sin "api/" al inicio) ---
    @GET("preference-test/questions")
    suspend fun getPreferenceQuestions(@Header("Authorization") token: String): Response<PreferenceTestResponse>

    @POST("preference-test/submit")
    suspend fun submitPreferenceTest(
        @Header("Authorization") token: String,
        @Body body: SubmitPreferenceRequest
    ): Response<SubmitPreferenceResponse>

    @GET("preference-test/result")
    suspend fun getPreferenceResult(@Header("Authorization") token: String): Response<PreferenceResultWrapper>
}

// Wrapper necesario para la respuesta del término del día
data class DailyTermWrapper(
    val success: Boolean,
    val daily_term: DailyTerm,
    val xp_earned: Int,
    val already_viewed_today: Boolean
)

// Modelo simple del término (coincide con Glossary del backend)
data class DailyTerm(
    val id: Int,
    val term: String,
    val definition: String,
    val category: String,
    val difficulty: String
)