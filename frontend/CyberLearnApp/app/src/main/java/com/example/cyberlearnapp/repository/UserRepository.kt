// app/src/main/java/com/example/cyberlearnapp/repository/UserRepository.kt

package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.*
import com.example.cyberlearnapp.utils.AuthManager
import com.example.cyberlearnapp.utils.safeApiCall
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    // ✅ Helper consistente para obtener token con prefijo Bearer
    private fun getToken(): String {
        val token = authManager.getToken() ?: throw IllegalStateException("No hay sesión activa")
        return "Bearer $token"
    }

    suspend fun getDashboard(): DashboardResponse = safeApiCall {
        apiService.getDashboard(getToken()).body()!!
    }

    suspend fun getDailyTerm(): DailyTermWrapper = safeApiCall {
        apiService.getDailyTerm(getToken()).body()!!
    }

    suspend fun completeDailyTerm(termId: Int): CompleteDailyTermResponse = safeApiCall {
        apiService.completeDailyTerm(
            getToken(),
            CompleteDailyTermRequest(termId = termId)
        ).body()!!
    }

    suspend fun getUserProfile(): UserProfileResponse = safeApiCall {
        apiService.getUserProfile(getToken()).body()!!
    }

    suspend fun getUserBadges(): List<Badge> = safeApiCall {
        val response = apiService.getUserBadges(getToken())
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.badges ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun logout() {
        authManager.clear()
    }
}
