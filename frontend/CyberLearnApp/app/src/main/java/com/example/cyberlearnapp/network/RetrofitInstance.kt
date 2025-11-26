package com.example.cyberlearnapp.network

import com.example.cyberlearnapp.utils.AuthManager
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol // ✅ Importante
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    // Tu IP del servidor
    private const val BASE_URL = "http://172.232.188.183:8000/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Interceptor para inyectar el Token automáticamente
    private val authInterceptor = okhttp3.Interceptor { chain ->
        val original = chain.request()
        val token = AuthManager.getToken()

        val requestBuilder = original.newBuilder()

        // Si tenemos token, lo añadimos siempre
        if (!token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        // Opcional: Añadir header para cerrar conexión explícitamente si el servidor lo pide
        // requestBuilder.header("Connection", "close")

        val request = requestBuilder.build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        // ✅ Aumentamos un poco los tiempos por si la red es lenta
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        // ✅ SOLUCIÓN CLAVE: Reintentar y forzar protocolo estable
        .retryOnConnectionFailure(true)
        .protocols(listOf(Protocol.HTTP_1_1))
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        // isLenient = true // Descomenta si el servidor a veces envía JSON malformado
    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}