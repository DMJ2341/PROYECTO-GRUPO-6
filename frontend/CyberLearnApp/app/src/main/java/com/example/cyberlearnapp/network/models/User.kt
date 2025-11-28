package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val email: String,
    val name: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    // ✅ AGREGADO: Campos de gamificación que devuelve el backend
    @SerialName("total_xp")
    val totalXp: Int = 0,

    @SerialName("level")
    val level: Int = 1,

    // ✅ AGREGADO: Si el backend devuelve estos campos en /api/user/profile
    @SerialName("completed_courses")
    val completedCourses: Int? = null,

    @SerialName("badges_count")
    val badgesCount: Int? = null
)

// Respuesta del endpoint /api/user/profile
@Serializable
data class UserProfileResponse(
    val success: Boolean,
    val user: User
)

@Serializable
data class AuthResponse(
    val success: Boolean,
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    val user: User
)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(val email: String, val password: String, val name: String? = null)