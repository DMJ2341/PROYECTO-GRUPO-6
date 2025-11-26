package com.example.cyberlearnapp.di

import android.content.Context
import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.RetrofitInstance
import com.example.cyberlearnapp.utils.AuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        // Usamos la instancia Singleton que ya configuramos con Kotlinx Serialization
        return RetrofitInstance.api
    }

    // Inicializar AuthManager al arranque
    @Provides
    @Singleton
    fun provideAuthManager(@ApplicationContext context: Context): AuthManager {
        AuthManager.init(context)
        return AuthManager
    }

    // NOTA: No hace falta proveer los Repositorios (LessonRepository, etc.)
    // explícitamente aquí porque ya tienen @Inject constructor() y @Singleton
    // en sus propias clases. Hilt los encontrará automáticamente.
}