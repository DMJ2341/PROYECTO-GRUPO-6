package com.example.cyberlearnapp.repository

import android.util.Log
import com.example.cyberlearnapp.models.*
import com.example.cyberlearnapp.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestRepository @Inject constructor(
    private val apiService: ApiService
) {
    // ✅ NUEVA FUNCIÓN: Obtener resultado previo del usuario
    suspend fun getPreviousResult(token: String): UserTestResultResponse {
        Log.d("TestRepository", "🔍 Llamando a getUserTestResult...")

        val response = apiService.getUserTestResult("Bearer $token")

        Log.d("TestRepository", "📡 Response code: ${response.code()}")
        Log.d("TestRepository", "📡 Response successful: ${response.isSuccessful}")

        if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!
            Log.d("TestRepository", "✅ has_result: ${body.hasResult}")
            Log.d("TestRepository", "✅ result: ${body.result?.recommendedRole}")
            return body
        }

        Log.e("TestRepository", "❌ Error: ${response.code()} - ${response.errorBody()?.string()}")
        throw Exception("Error obteniendo resultado previo: ${response.code()}")
    }

    suspend fun getQuestions(token: String): TestQuestionsResponse {
        val response = apiService.getQuestions("Bearer $token")
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        }
        throw Exception("Error cargando preguntas: ${response.code()}")
    }

    suspend fun submitTest(token: String, submission: TestSubmission): TestSubmitResponse {
        val response = apiService.submitTest("Bearer $token", submission)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        }
        throw Exception("Error enviando test: ${response.code()}")
    }

    suspend fun getRecommendations(token: String, role: String): RecommendationsResponse {
        val response = apiService.getRecommendations("Bearer $token", role)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        }
        throw Exception("Error obteniendo recomendaciones")
    }

    suspend fun getTestHistory(token: String): TestHistoryResponse {
        val response = apiService.getTestHistory("Bearer $token")
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        }
        throw Exception("Error obteniendo historial")
    }
}