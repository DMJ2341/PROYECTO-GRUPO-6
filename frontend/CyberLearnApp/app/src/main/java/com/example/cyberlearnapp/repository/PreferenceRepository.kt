package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.assessments.PreferenceQuestion
import com.example.cyberlearnapp.network.models.assessments.SubmitPreferenceRequest
import com.example.cyberlearnapp.network.models.assessments.UserPreferenceResult
import com.example.cyberlearnapp.utils.AuthManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepository @Inject constructor(
    private val apiService: ApiService
) {
    private fun getToken(): String = "Bearer ${AuthManager.getToken() ?: ""}"

    suspend fun getQuestions(): List<PreferenceQuestion> {
        val response = apiService.getPreferenceQuestions(getToken())
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!.questions
        } else {
            throw Exception("Error cargando preguntas")
        }
    }

    suspend fun submitTest(answers: Map<String, String>, timeTaken: Int? = null): UserPreferenceResult {
        val request = SubmitPreferenceRequest(answers, timeTaken)
        val response = apiService.submitPreferenceTest(getToken(), request)

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!.result
        } else {
            throw Exception("Error enviando test")
        }
    }

    suspend fun getSavedResult(): UserPreferenceResult? {
        val response = apiService.getPreferenceResult(getToken())
        if (response.isSuccessful && response.body() != null) {
            val wrapper = response.body()!!
            return if (wrapper.hasResult) wrapper.result else null
        }
        return null
    }
}