// app/src/main/java/com/example/cyberlearnapp/utils/AuthManager.kt

package com.example.cyberlearnapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object AuthManager {
    private const val PREF_NAME = "CyberLearnPrefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_NAME = "user_name"

    private var preferences: SharedPreferences? = null

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        Log.d("AuthManager", "Inicializado - Token existe: ${!getToken().isNullOrEmpty()}")
    }

    // ==========================================
    // 🔑 GESTIÓN DE TOKENS
    // ==========================================

    fun saveToken(token: String) {
        preferences?.edit()?.putString(KEY_TOKEN, token)?.apply()
        Log.d("AuthManager", "Token guardado: ${token.take(20)}...")
    }

    fun getToken(): String? {
        val token = preferences?.getString(KEY_TOKEN, null)
        Log.d("AuthManager", "Token recuperado: ${token?.take(20) ?: "null"}")
        return token
    }

    fun saveRefreshToken(token: String) {
        preferences?.edit()?.putString(KEY_REFRESH_TOKEN, token)?.apply()
        Log.d("AuthManager", "Refresh token guardado: ${token.take(20)}...")
    }

    fun getRefreshToken(): String? {
        val refreshToken = preferences?.getString(KEY_REFRESH_TOKEN, null)
        Log.d("AuthManager", "Refresh token recuperado: ${refreshToken?.take(20) ?: "null"}")
        return refreshToken
    }

    // ==========================================
    // 👤 GESTIÓN DE DATOS DE USUARIO
    // ==========================================

    fun saveUserData(userId: Int, email: String, name: String) {
        preferences?.edit()?.apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            apply()
        }
        Log.d("AuthManager", "Datos de usuario guardados: $name ($email)")
    }

    fun getUserId(): Int = preferences?.getInt(KEY_USER_ID, -1) ?: -1
    fun getUserEmail(): String? = preferences?.getString(KEY_USER_EMAIL, null)
    fun getUserName(): String? = preferences?.getString(KEY_USER_NAME, null)

    // ==========================================
    // 🔓 LIMPIEZA Y VERIFICACIÓN
    // ==========================================

    fun clear() {
        preferences?.edit()?.clear()?.apply()
        Log.d("AuthManager", "Sesión limpiada completamente")
    }

    fun clearAuthData() {
        clear()  // Reutiliza la lógica existente
    }

    fun isLoggedIn(): Boolean {
        val hasToken = !getToken().isNullOrEmpty()
        val hasRefreshToken = !getRefreshToken().isNullOrEmpty()
        val isLogged = hasToken && hasRefreshToken
        Log.d("AuthManager", "¿Está logueado? $isLogged (Token: $hasToken, Refresh: $hasRefreshToken)")
        return isLogged
    }
}