package com.example.cyberlearnapp.network

import com.example.cyberlearnapp.utils.AuthManager
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    // ⚠️ IMPORTANTE: Usa la IP de tu máquina (si es emulador usa 10.0.2.2, si es físico usa tu IP local)
    // No uses "localhost" en Android.
    private const val BASE_URL = "http://172.232.188.183/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Configuración de JSON para ser tolerante a fallos
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }

    // Cliente HTTP con Interceptores
    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())     // Inyecta el token (Archivo AuthInterceptor.kt)
        .authenticator(TokenAuthenticator())   // Renueva el token (Archivo TokenAuthenticator.kt)
        .addInterceptor(loggingInterceptor)    // Logs para debug
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Inicialización Lazy de la API
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}