plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.hilt.android)
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
}

android {
    namespace = "com.example.cyberlearnapp"
    compileSdk = 34  // Corregido: usa = y versión más actual

    defaultConfig {
        applicationId = "com.example.cyberlearnapp"
        minSdk = 24
        targetSdk = 34  // Actualizado a versión más reciente
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // AGREGAR MATERIAL ICONS EXTENDED - SOLUCIÓN AL ERROR PlayCircle
    implementation("androidx.compose.material:material-icons-extended:1.6.4")

    // ARQUITECTURA DE RED Y NAVEGACIÓN

    // 1. Retrofit (HTTP Client) y GSON (JSON Converter)
    implementation(libs.retrofit.runtime)
    implementation(libs.retrofit.converter.gson)

    implementation(libs.logging.interceptor)

    // 2. Jetpack Navigation para Compose
    implementation(libs.navigation.compose)

    implementation(libs.lifecycle.viewmodel.compose)

    // 3. Hilt (Dependency Injection - Recomendado)
    implementation(libs.hilt.android)

    implementation(libs.hilt.navigation.compose)
    // El procesador de anotaciones debe usar 'kapt'
    kapt(libs.hilt.compiler)

    // COROUTINES
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.compose.runtime)

    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}