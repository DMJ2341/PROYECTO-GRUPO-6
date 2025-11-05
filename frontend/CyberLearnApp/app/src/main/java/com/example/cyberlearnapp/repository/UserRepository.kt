package com.example.cyberlearnapp.repository

// 1. IMPORTACIONES NECESARIAS (LIMPIADAS)
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// 2. EL REPOSITORIO AHORA RECIBE SUS DEPENDENCIAS
@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService, // <-- Inyectado por Hilt
    private val dataStore: DataStore<Preferences> // <-- Inyectado por Hilt
) {

    // 3. CLAVES PARA EL DATASTORE (Esto está bien)
    private object PreferencesKeys {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    // 4. FUNCIONES DEL REPOSITORIO (Usan el dataStore inyectado)
    suspend fun saveLoginData(token: String, user: User) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTH_TOKEN] = token
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
            preferences[PreferencesKeys.USER_NAME] = user.name ?: ""
            preferences[PreferencesKeys.USER_EMAIL] = user.email
        }
    }

    suspend fun clearLoginData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.AUTH_TOKEN]
        }
    }

    fun getUserData(): Flow<User?> {
        return dataStore.data.map { preferences ->
            val name = preferences[PreferencesKeys.USER_NAME]
            val email = preferences[PreferencesKeys.USER_EMAIL]
            if (name != null && email != null) {
                User(email = email, name = name)
            } else {
                null
            }
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
        }.firstOrNull() ?: false
    }
}