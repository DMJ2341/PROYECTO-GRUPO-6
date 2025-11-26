package com.example.cyberlearnapp

import android.app.Application
import com.example.cyberlearnapp.utils.AuthManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // ✅ Inicializamos AuthManager aquí para que esté listo antes que nada
        AuthManager.init(this)
    }
}