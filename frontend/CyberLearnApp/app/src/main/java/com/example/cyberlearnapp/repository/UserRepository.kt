package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.DashboardData
import com.example.cyberlearnapp.network.models.User
import com.example.cyberlearnapp.utils.AuthManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService
) {
    private fun getToken(): String = "Bearer ${AuthManager.getToken() ?: ""}"

    // Obtener perfil de usuario
    suspend fun getUserProfile(): User {
        val response = apiService.getUserProfile(getToken())
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!.user
        } else {
            throw Exception("Error al obtener perfil")
        }
    }

    // Obtener datos del Dashboard (XP, Nivel, Badges)
    suspend fun getDashboard(): DashboardData {
        val response = apiService.getDashboard(getToken())
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!.dashboard
        } else {
            throw Exception("Error al cargar dashboard")
        }
    }

    // El AuthManager maneja el logout local, aquí podríamos llamar al backend si existiera endpoint de logout
    fun logout() {
        AuthManager.clear()
    }
}