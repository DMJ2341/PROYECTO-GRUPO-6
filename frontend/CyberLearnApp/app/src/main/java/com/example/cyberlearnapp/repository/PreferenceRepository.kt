package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.assessments.*
import com.example.cyberlearnapp.utils.safeApiCall
import com.example.cyberlearnapp.utils.AuthManager // Asegúrate de importar AuthManager
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepository @Inject constructor(
    private val apiService: ApiService
) {
    // ... (El resto del código es idéntico al que me pasaste, es correcto) ...
    // Solo asegúrate de que getAuthToken() llame a AuthManager.getToken()

    suspend fun getQuestions(): List<PreferenceQuestion> {
        return safeApiCall(Dispatchers.IO) {
            apiService.getPreferenceQuestions("Bearer ${getAuthToken()}")
        }
    }

    suspend fun submitTest(answers: Map<String, String>, timeTakenSeconds: Int? = null): UserPreferenceResult {
        val request = SubmitPreferenceRequest(answers = answers, time_taken = timeTakenSeconds)
        val response = safeApiCall(Dispatchers.IO) {
            apiService.submitPreferenceTest("Bearer ${getAuthToken()}", request)
        }
        return response.result
    }

    suspend fun getSavedResult(): UserPreferenceResult? {
        val response = safeApiCall(Dispatchers.IO) {
            apiService.getPreferenceResult("Bearer ${getAuthToken()}")
        }
        return if (response.has_result) response.result else null
    }

    private fun getAuthToken(): String {
        return AuthManager.getToken() ?: ""
    }
}