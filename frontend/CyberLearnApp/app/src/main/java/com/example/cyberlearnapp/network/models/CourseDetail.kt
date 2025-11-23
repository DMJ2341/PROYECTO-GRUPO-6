package com.example.cyberlearnapp.network.models

// ✅ SOLUCIÓN: Se importa la clase Lesson que ahora sí existe.
import com.example.cyberlearnapp.network.models.Lesson
import com.google.gson.annotations.SerializedName

/**
 * Modelo para el detalle de un curso.
 * Incluye la información básica del curso y la lista de sus lecciones.
 */
data class CourseDetail(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("lessons")
    val lessons: List<Lesson>
)
