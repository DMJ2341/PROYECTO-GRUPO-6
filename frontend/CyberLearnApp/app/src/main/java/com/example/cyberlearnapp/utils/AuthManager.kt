package com.example.cyberlearnapp.utils

import android.content.Context
import android.content.SharedPreferences

object AuthManager {
    private const val PREF_NAME = "CyberLearnPrefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token" // ✅ Nuevo

    private var preferences: SharedPreferences? = null

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // --- Access Token ---
    fun saveToken(token: String) {
        preferences?.edit()?.putString(KEY_TOKEN, token)?.apply()
    }

    fun getToken(): String? {
        return preferences?.getString(KEY_TOKEN, null)
    }

    // --- ✅ Refresh Token (Faltaba esto) ---
    fun saveRefreshToken(token: String) {
        preferences?.edit()?.putString(KEY_REFRESH_TOKEN, token)?.apply()
    }

    fun getRefreshToken(): String? {
        return preferences?.getString(KEY_REFRESH_TOKEN, null)
    }

    fun clear() {
        preferences?.edit()?.clear()?.apply()
    }

    // Helper
    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }
}