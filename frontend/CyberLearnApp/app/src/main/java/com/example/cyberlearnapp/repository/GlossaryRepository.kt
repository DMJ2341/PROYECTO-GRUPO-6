package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.*
import com.example.cyberlearnapp.utils.AuthManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlossaryRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getTerms(query: String = ""): List<GlossaryTerm> {
        val token = "Bearer ${AuthManager.getToken()}"

        val response = if (query.isBlank()) {
            apiService.getGlossaryTerms(token)
        } else {
            apiService.searchGlossaryTerms(token, query)
        }

        return if (response.isSuccessful) {
            response.body()?.terms ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun markAsLearned(glossaryId: Int, isLearned: Boolean): Boolean {
        val token = "Bearer ${AuthManager.getToken()}"
        val response = apiService.markTermAsLearned(
            token,
            glossaryId,
            MarkLearnedRequest(isLearned)
        )
        return response.isSuccessful
    }

    suspend fun getLearnedTerms(): List<GlossaryTerm> {
        val token = "Bearer ${AuthManager.getToken()}"
        val response = apiService.getLearnedTerms(token)

        return if (response.isSuccessful) {
            response.body()?.terms ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun getStats(): GlossaryStats? {
        val token = "Bearer ${AuthManager.getToken()}"
        val response = apiService.getGlossaryStats(token)

        return if (response.isSuccessful) {
            response.body()?.stats
        } else {
            null
        }
    }

    suspend fun recordQuizAttempt(glossaryId: Int, isCorrect: Boolean): QuizAttemptResponse? {
        val token = "Bearer ${AuthManager.getToken()}"
        val response = apiService.recordQuizAttempt(
            token,
            glossaryId,
            QuizAttemptRequest(isCorrect)
        )

        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
}