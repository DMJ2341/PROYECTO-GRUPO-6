package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ==========================================
// 👤 MODELO DE USUARIO
// ==========================================

@Serializable
data class User(
    val id: Int,
    val email: String,
    val name: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    // Campos de gamificación
    @SerialName("total_xp")
    val totalXp: Int = 0,

    val level: Int = 1,

    @SerialName("completed_courses")
    val completedCourses: Int? = null,

    @SerialName("badges_count")
    val badgesCount: Int? = null,

    // ✅ NUEVOS CAMPOS para verificación y correos académicos
    val institution: String? = null,

    @SerialName("is_academic")
    val isAcademic: Boolean = false
)

// ==========================================
// 🔐 RESPUESTAS DE AUTENTICACIÓN
// ==========================================

@Serializable
data class UserProfileResponse(
    val success: Boolean,
    val user: User
)

@Serializable
data class AuthResponse(
    val success: Boolean? = null,  // ✅ Opcional porque login/verify lo devuelven, register no
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    val user: User
)

// ==========================================
// 📧 RESPUESTA DE REGISTRO (con verificación)
// ==========================================

@Serializable
data class RegisterResponse(
    val success: Boolean,
    val message: String,
    @SerialName("user_id") val userId: Int? = null,
    val email: String? = null,
    @SerialName("requires_verification") val requiresVerification: Boolean = false,
    // Tokens opcionales (solo si el backend los envía)
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("refresh_token") val refreshToken: String? = null,
    val user: User? = null
)

// ==========================================
// 📨 REQUESTS
// ==========================================

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class VerifyEmailRequest(
    val email: String,
    val code: String
)

@Serializable
data class ResendCodeRequest(
    val email: String
)

// ==========================================
// 💬 RESPUESTAS GENÉRICAS
// ==========================================

@Serializable
data class MessageResponse(
    val success: Boolean,
    val message: String
)