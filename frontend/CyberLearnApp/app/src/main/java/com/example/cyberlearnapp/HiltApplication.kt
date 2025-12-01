package com.example.cyberlearnapp

import android.app.Application
import com.example.cyberlearnapp.utils.AuthManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // ✅ Inicializamos el AuthManager con el contexto de la aplicación
        AuthManager.init(this)
    }
}