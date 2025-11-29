package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.CompleteDailyTermRequest
import com.example.cyberlearnapp.network.models.CompleteDailyTermResponse
import com.example.cyberlearnapp.network.models.DashboardResponse
import com.example.cyberlearnapp.network.models.DailyTermWrapper
import com.example.cyberlearnapp.network.models.UserProfileResponse
import com.example.cyberlearnapp.network.models.Badge
import com.example.cyberlearnapp.network.models.UserBadgesResponse
import com.example.cyberlearnapp.network.models.AuthResponse
import com.example.cyberlearnapp.utils.AuthManager
import com.example.cyberlearnapp.utils.safeApiCall
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    // Helper para obtener el token con el prefijo "Bearer "
    private fun getToken(): String = "Bearer ${authManager.getToken() ?: ""}"

    suspend fun getDashboard(): DashboardResponse = safeApiCall {
        apiService.getDashboard(getToken()).body()!!
    }

    suspend fun getDailyTerm(): DailyTermWrapper = safeApiCall {
        apiService.getDailyTerm(getToken()).body()!!
    }

    suspend fun completeDailyTerm(termId: Int): CompleteDailyTermResponse =
        safeApiCall {
            apiService.completeDailyTerm(getToken(), CompleteDailyTermRequest(termId = termId)).body()!!
        }

    suspend fun getUserProfile(): UserProfileResponse = safeApiCall {
        apiService.getUserProfile(getToken()).body()!!
    }

    suspend fun getUserBadges(): List<Badge> {
        val token = AuthManager.getToken() ?: throw Exception("No hay sesión activa")
        val response = apiService.getUserBadges("Bearer $token")

        return if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.badges ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun logout() {
        authManager.clear()
    }
}