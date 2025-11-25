package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.assessments.ExamResultResponse
import com.example.cyberlearnapp.network.models.assessments.ExamStartResponse
import com.example.cyberlearnapp.network.models.assessments.ExamSubmitRequest
import com.example.cyberlearnapp.utils.AuthManager
import com.example.cyberlearnapp.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun startExam(): ExamStartResponse {
        return safeApiCall(Dispatchers.IO) {
            api.startFinalExam("Bearer ${AuthManager.getToken() ?: ""}")
        }
    }

    suspend fun submitExam(answers: Map<String, String>): ExamResultResponse {
        return safeApiCall(Dispatchers.IO) {
            api.submitFinalExam("Bearer ${AuthManager.getToken() ?: ""}", ExamSubmitRequest(answers))
        }
    }
}