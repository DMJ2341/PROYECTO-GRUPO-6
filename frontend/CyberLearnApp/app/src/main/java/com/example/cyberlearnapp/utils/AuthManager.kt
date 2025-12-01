// app/src/main/java/com/example/cyberlearnapp/utils/AuthManager.kt

package com.example.cyberlearnapp.utils

import android.content.Context
import android.content.SharedPreferences

object AuthManager {
    private const val PREF_NAME = "CyberLearnPrefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"

    private var preferences: SharedPreferences? = null

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        preferences?.edit()?.putString(KEY_TOKEN, token)?.apply()
    }

    fun getToken(): String? = preferences?.getString(KEY_TOKEN, null)

    fun saveRefreshToken(token: String) {
        preferences?.edit()?.putString(KEY_REFRESH_TOKEN, token)?.apply()
    }

    fun getRefreshToken(): String? = preferences?.getString(KEY_REFRESH_TOKEN, null)


    fun clear() {
        preferences?.edit()?.clear()?.apply()
    }

    fun clearAuthData() {
        clear()  // Reutiliza la lógica existente
    }

    fun isLoggedIn(): Boolean = !getToken().isNullOrEmpty()
}