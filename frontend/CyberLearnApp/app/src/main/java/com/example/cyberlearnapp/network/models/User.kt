package com.example.cyberlearnapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val email: String,
    val name: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

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