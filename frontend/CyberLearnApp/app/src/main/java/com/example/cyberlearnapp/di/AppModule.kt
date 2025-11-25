package com.example.cyberlearnapp.di

import com.example.cyberlearnapp.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ✅ CAMBIO CLAVE: Usamos la IP directa para evitar errores de DNS de DuckDNS.
    // Cuando el dominio funcione, solo cambias esta línea por "https://cyberlearn1.duckdns.org/api/"
    private const val BASE_URL = "http://172.232.188.183/api/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    // 🚀 MEJORA: No necesitamos proveer los Repositorios aquí (UserRepository, PreferenceRepository, etc.)
    // porque ya usan "@Inject constructor" en sus propias clases.
    // Hilt los encuentra y los inyecta automáticamente. ¡Menos código, más limpio!
}