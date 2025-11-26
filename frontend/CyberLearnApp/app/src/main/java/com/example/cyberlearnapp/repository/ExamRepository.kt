package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.assessments.ExamResultResponse
import com.example.cyberlearnapp.network.models.assessments.ExamStartResponse
import com.example.cyberlearnapp.network.models.assessments.ExamSubmitRequest
import com.example.cyberlearnapp.utils.AuthManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun startExam(): ExamStartResponse {
        val token = "Bearer ${AuthManager.getToken() ?: ""}"
        val response = apiService.startFinalExam(token)

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception("Error al iniciar examen: ${response.message()}")
        }
    }

    suspend fun submitExam(answers: Map<String, String>): ExamResultResponse {
        val token = "Bearer ${AuthManager.getToken() ?: ""}"
        val request = ExamSubmitRequest(answers)
        val response = apiService.submitFinalExam(token, request)

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception("Error al enviar examen: ${response.message()}")
        }
    }
}