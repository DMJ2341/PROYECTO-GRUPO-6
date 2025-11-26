package com.example.cyberlearnapp.network

import com.example.cyberlearnapp.utils.AuthManager
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory // ✅ Importante: Usar este convertidor
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    // Asegúrate de que esta IP es accesible desde tu dispositivo/emulador
    private const val BASE_URL = "http://172.232.188.183:8000/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Interceptor para inyectar el Token automáticamente
    private val authInterceptor = okhttp3.Interceptor { chain ->
        val original = chain.request()
        val token = AuthManager.getToken()

        val requestBuilder = original.newBuilder()

        // Si tenemos token, lo añadimos siempre con el prefijo "Bearer"
        if (!token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // ✅ Configuración de JSON para Kotlin Serialization
    // ignoreUnknownKeys = true evita que la app explote si el backend añade campos nuevos que la app no conoce
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            // ✅ CAMBIO: Usamos Kotlin Serialization en lugar de Gson
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}