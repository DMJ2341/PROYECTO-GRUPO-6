package com.example.cyberlearnapp.network.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo para una lección individual dentro de un curso.
 * Contiene la información básica para mostrar en una lista.
 */
data class Lesson(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("type")
    val type: String = "text", // Puede ser "text" o "interactive"

    @SerializedName("is_completed")
    val isCompleted: Boolean = false
)
