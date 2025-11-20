package com.example.cyberlearnapp.network.models

import com.google.gson.annotations.SerializedName

/**
 * Representa un elemento interactivo dentro de una pantalla
 * Puede ser tocable, arrastrable, o tener estado
 */
data class InteractiveElement(
    @SerializedName("id")
    val id: String,

    @SerializedName("type")
    val type: ElementType,

    @SerializedName("content")
    val content: String,

    @SerializedName("is_correct")
    val isCorrect: Boolean? = null,

    @SerializedName("feedback")
    val feedback: String? = null,

    @SerializedName("icon")
    val icon: String? = null,

    @SerializedName("value")
    val value: Any? = null,

    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

/**
 * Tipos de elementos interactivos
 */
enum class ElementType {
    @SerializedName("button")
    BUTTON,           // Botón clickeable

    @SerializedName("card")
    CARD,             // Tarjeta tocable/expandible

    @SerializedName("draggable")
    DRAGGABLE,        // Elemento arrastrable

    @SerializedName("drop_zone")
    DROP_ZONE,        // Zona de destino para drag & drop

    @SerializedName("checkbox")
    CHECKBOX,         // Checkbox seleccionable

    @SerializedName("radio")
    RADIO,            // Radio button

    @SerializedName("slider")
    SLIDER,           // Control deslizante

    @SerializedName("input")
    INPUT             // Campo de texto
}

/**
 * Estado de un elemento interactivo
 */
data class ElementState(
    val id: String,
    val isSelected: Boolean = false,
    val isCorrect: Boolean? = null,
    val userValue: Any? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Resultado de interacción con un elemento
 */
data class InteractionResult(
    val elementId: String,
    val isCorrect: Boolean,
    val feedback: String,
    val xpEarned: Int = 0,
    val shouldAdvance: Boolean = false
)