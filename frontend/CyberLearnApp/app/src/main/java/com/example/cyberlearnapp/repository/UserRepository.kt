// app/src/main/java/com/example/cyberlearnapp/repository/UserRepository.kt

package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.CompleteDailyTermRequest
import com.example.cyberlearnapp.network.models.CompleteDailyTermResponse
import com.example.cyberlearnapp.network.models.DashboardResponse
import com.example.cyberlearnapp.utils.AuthManager
import com.example.cyberlearnapp.utils.safeApiCall
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager
) {

    suspend fun getDashboard() = safeApiCall {
        apiService.getDashboard().body()!!
    }

    suspend fun getDailyTerm() = safeApiCall {
        apiService.getDailyTerm().body()!!
    }

    // NUEVA FUNCIÓN
    suspend fun completeDailyTerm(termId: Int): CompleteDailyTermResponse = safeApiCall {
        apiService.completeDailyTerm(CompleteDailyTermRequest(termId)).body()!!
    }

    suspend fun getUserProfile() = safeApiCall {
        apiService.getProfile().body()!!
    }

    fun logout() {
        authManager.clearAuthData()
    }
}