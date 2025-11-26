package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.CompleteDailyTermRequest
import com.example.cyberlearnapp.network.models.CompleteDailyTermResponse
import com.example.cyberlearnapp.network.models.DashboardResponse
import com.example.cyberlearnapp.network.models.DailyTermWrapper
import com.example.cyberlearnapp.network.models.UserProfileResponse // ✅ CORRECCIÓN: Referencia al nuevo nombre
import com.example.cyberlearnapp.network.models.AuthResponse
import com.example.cyberlearnapp.utils.AuthManager
import com.example.cyberlearnapp.utils.safeApiCall
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    // ... (Otras funciones)

    suspend fun getDashboard(): DashboardResponse = safeApiCall {
        apiService.getDashboard().body()!!
    }

    suspend fun getDailyTerm(): DailyTermWrapper = safeApiCall {
        apiService.getDailyTerm().body()!!
    }

    suspend fun completeDailyTerm(termId: Int): CompleteDailyTermResponse =
        safeApiCall {
            apiService.completeDailyTerm(CompleteDailyTermRequest(termId = termId)).body()!!
        }

    // ✅ FUNCIÓN CORREGIDA
    suspend fun getUserProfile(): UserProfileResponse = safeApiCall {
        // Usa UserProfileResponse que ahora está definido en User.kt
        apiService.getProfile().body()!!
    }

    fun logout() {
        authManager.clearAuthData()
    }
}