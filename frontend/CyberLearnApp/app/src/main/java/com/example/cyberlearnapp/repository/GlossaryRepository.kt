package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.GlossaryTerm
import com.example.cyberlearnapp.utils.AuthManager
import javax.inject.Inject

class GlossaryRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getTerms(query: String = ""): List<GlossaryTerm> {
        val token = AuthManager.getToken() ?: return emptyList()
        try {
            val response = apiService.getGlossaryTerms(token, if (query.isNotEmpty()) query else null)
            if (response.isSuccessful && response.body()?.success == true) {
                return response.body()?.terms ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }
}