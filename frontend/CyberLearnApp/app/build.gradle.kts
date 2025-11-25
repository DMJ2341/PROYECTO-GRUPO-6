plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization) // <-- NUEVO: para JSON fácil
}

android {
    namespace = "com.example.cyberlearnapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cyberlearnapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 12   // <-- Sube uno por cada release
        versionName = "1.5.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.17" // ← Versión compatible con AGP 8.5+
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // === Compose BOM (recomendado 2025) ===
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.security.crypto)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // === ViewModel + Lifecycle ===
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)

    // === Hilt ===
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose) // ← CLAVE para @HiltViewModel en Compose

    // === Retrofit + Gson/Json Serialization ===
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.kotlinx.serialization.json) // ← Nuevo: para parsear JSON grandes (preference_results_visual.json)

    // === Coroutines ===
    implementation(libs.kotlinx.coroutines.android)

    // === Coil (imágenes) ===
    implementation(libs.coil.compose)

    // === Accompanist (opcional pero útil) ===
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    // === Lottie (animaciones confetti) ===
    implementation(libs.lottie.compose) // ← Para el confetti explosion

    // === Testing ===
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
}