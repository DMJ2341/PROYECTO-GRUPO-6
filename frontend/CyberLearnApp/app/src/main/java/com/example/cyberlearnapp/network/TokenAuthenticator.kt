// app/src/main/java/com/example/cyberlearnapp/network/TokenAuthenticator.kt

package com.example.cyberlearnapp.network

import android.util.Log
import com.example.cyberlearnapp.utils.AuthManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * ✅ TokenAuthenticator - Renovación Automática de Tokens
 *
 * Este clase intercepta las respuestas 401 (No Autorizado) y automáticamente
 * intenta renovar el access_token usando el refresh_token almacenado.
 *
 * Flujo:
 * 1. La API responde 401 (token expirado)
 * 2. El Authenticator toma el refresh_token
 * 3. Llama a /api/auth/refresh
 * 4. Si éxito: guarda nuevo token y reintenta la petición original
 * 5. Si fallo: retorna null (OkHttp enviará al usuario al login)
 */
class TokenAuthenticator : Authenticator {

    companion object {
        private const val TAG = "TokenAuthenticator"
        private const val MAX_RETRY_COUNT = 2  // Evitar bucles infinitos
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d(TAG, "🔄 Intentando renovar token (Código: ${response.code})")

        // ==========================================
        // 1️⃣ PREVENIR BUCLES INFINITOS
        // ==========================================
        val retryCount = response.request.header("X-Retry-Count")?.toIntOrNull() ?: 0
        if (retryCount >= MAX_RETRY_COUNT) {
            Log.e(TAG, "❌ Máximo de reintentos alcanzado ($retryCount)")
            AuthManager.clear()  // Limpiar sesión corrupta
            return null
        }

        // ==========================================
        // 2️⃣ OBTENER REFRESH TOKEN
        // ==========================================
        val refreshToken = AuthManager.getRefreshToken()
        if (refreshToken.isNullOrEmpty()) {
            Log.e(TAG, "❌ No hay refresh token disponible")
            AuthManager.clear()
            return null
        }

        // ==========================================
        // 3️⃣ LLAMAR AL ENDPOINT DE REFRESH
        // ==========================================
        try {
            Log.d(TAG, "📡 Llamando a /api/auth/refresh...")

            // ✅ USAR runBlocking PARA CONVERTIR SUSPEND A SÍNCRONO
            // (El Authenticator de OkHttp requiere ejecución síncrona)
            val newTokens = runBlocking {
                try {
                    val refreshResponse = RetrofitInstance.api.refreshToken(
                        mapOf("refresh_token" to refreshToken)
                    ).execute()  // Execute síncrono de Retrofit Call<>

                    if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                        refreshResponse.body()
                    } else {
                        Log.e(TAG, "❌ Error en refresh: ${refreshResponse.code()} - ${refreshResponse.message()}")
                        null
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Excepción en refresh: ${e.message}", e)
                    null
                }
            }

            // ==========================================
            // 4️⃣ PROCESAR RESPUESTA (CORREGIDO)
            // ==========================================
            // El error estaba aquí: newTokens.success podía ser null.
            // Usamos '== true' para asegurar que sea true y no null.
            if (newTokens != null && newTokens.success == true) { // ✅ CORREGIDO
                Log.d(TAG, "✅ Tokens renovados exitosamente")

                // Guardar nuevo access token
                AuthManager.saveToken(newTokens.accessToken)

                // Guardar nuevo refresh token si el servidor lo envió
                if (newTokens.refreshToken.isNotEmpty()) {
                    AuthManager.saveRefreshToken(newTokens.refreshToken)
                    Log.d(TAG, "✅ Nuevo refresh token guardado")
                }

                // ✅ REINTENTAR LA PETICIÓN ORIGINAL CON EL NUEVO TOKEN
                return response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens.accessToken}")
                    .header("X-Retry-Count", (retryCount + 1).toString())  // Contador de reintentos
                    .build()

            } else {
                Log.e(TAG, "❌ Refresh falló - Limpiando sesión")
                AuthManager.clear()
                return null
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error fatal en authenticator: ${e.message}", e)
            AuthManager.clear()
            return null
        }
    }
}