plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose) // ✅ Correcto para Kotlin 2.0
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.cyberlearnapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.cyberlearnapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 12
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

    // ⚠️ CORRECCIÓN: Eliminado kotlinCompilerExtensionVersion porque usas Kotlin 2.0
    // El compilador de Compose ahora usa la misma versión que Kotlin automáticamente.

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // === Compose BOM ===
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.material.icons.extended)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)

    // === Lifecycle ===
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)

    // === Hilt ===
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // === Retrofit + Serialization ===
    implementation(libs.retrofit)
    // Asegúrate de actualizar el TOML como te indiqué arriba para usar el oficial
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)

    // === OkHttp ===
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // === Imágenes & Animaciones ===
    implementation(libs.coil.compose)
    implementation(libs.lottie.compose)

    // === Accompanist ===
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    // === Testing ===
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation("androidx.security:security-crypto:1.1.0-alpha06")
}