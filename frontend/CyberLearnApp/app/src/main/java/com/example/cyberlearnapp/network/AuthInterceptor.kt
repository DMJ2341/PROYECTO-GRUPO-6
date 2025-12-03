// app/src/main/java/com/example/cyberlearnapp/network/AuthInterceptor.kt

package com.example.cyberlearnapp.network

import android.util.Log
import com.example.cyberlearnapp.utils.AuthManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    companion object {
        private const val TAG = "AuthInterceptor"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val TOKEN_PREFIX = "Bearer "
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // ==========================================
        // 1️⃣ VERIFICAR SI LA RUTA NECESITA AUTENTICACIÓN
        // ==========================================
        // Algunas rutas NO necesitan token (login, register, etc)
        val path = originalRequest.url.encodedPath
        val publicRoutes = listOf(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/verify-email",
            "/api/auth/resend-code",
            "/api/auth/refresh"
        )

        // Si es una ruta pública, no añadir token
        if (publicRoutes.any { path.contains(it) }) {
            Log.d(TAG, "📭 Ruta pública detectada: $path (sin token)")
            return chain.proceed(originalRequest)
        }

        // ==========================================
        // OBTENER TOKEN DEL ALMACENAMIENTO
        // ==========================================
        val token = AuthManager.getToken()

        // Si no hay token, enviar petición sin modificar
        // (Probablemente falle con 401, pero el TokenAuthenticator lo manejará)
        if (token.isNullOrEmpty()) {
            Log.w(TAG, "⚠️ No hay token disponible para: $path")
            return chain.proceed(originalRequest)
        }

        // ==========================================
        // AÑADIR TOKEN AL HEADER
        // ==========================================
        val authenticatedRequest = originalRequest.newBuilder()
            .header(HEADER_AUTHORIZATION, "$TOKEN_PREFIX$token")
            .build()

        Log.d(TAG, "✅ Token añadido a: $path (Token: ${token.take(20)}...)")

        // ==========================================
        // ENVIAR PETICIÓN CON TOKEN
        // ==========================================
        return chain.proceed(authenticatedRequest)
    }
}