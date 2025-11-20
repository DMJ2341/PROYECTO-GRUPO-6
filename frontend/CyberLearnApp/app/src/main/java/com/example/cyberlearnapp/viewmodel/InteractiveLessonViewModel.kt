package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.RetrofitInstance
import com.example.cyberlearnapp.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InteractiveScreen(
    val screenNumber: Int,
    val type: String,
    val title: String,
    val subtitle: String? = null,
    val content: Map<String, Any> = emptyMap(),
    val ctaButton: String? = null,
    val emailData: Any? = null,
    val signals: Any? = null,
    val hint: String? = null,
    val items: Any? = null,
    val tip: String? = null,
    val steps: Any? = null,
    val reminder: String? = null,
    val questions: Any? = null
)

data class InteractiveLesson(
    val id: String,
    val title: String,
    val totalScreens: Int,
    val screens: List<InteractiveScreen>
)

data class InteractiveLessonState(
    val lesson: InteractiveLesson? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class InteractiveLessonViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InteractiveLessonState())
    val state: StateFlow<InteractiveLessonState> = _state.asStateFlow()

    fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            try {
                println("🔍 [LESSON-VM] Iniciando carga de lección: $lessonId")
                _state.value = _state.value.copy(isLoading = true, errorMessage = null)

                val response = RetrofitInstance.api.getLessonContent(lessonId)
                println("📡 [LESSON-VM] Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val lesson = response.body()!!
                    println("✅ [LESSON-VM] Lección recibida: ${lesson.title}")
                    println("📊 [LESSON-VM] Total screens: ${lesson.total_screens}")
                    println("📝 [LESSON-VM] Screens JSON length: ${lesson.screens?.length ?: 0}")

                    val parsedScreens = parseScreensFromJson(lesson.screens)
                    println("🎯 [LESSON-VM] Screens parseadas: ${parsedScreens.size}")

                    if (parsedScreens.isEmpty()) {
                        _state.value = _state.value.copy(
                            errorMessage = "No se pudieron cargar las pantallas de la lección",
                            isLoading = false
                        )
                        return@launch
                    }

                    val interactiveLesson = InteractiveLesson(
                        id = lesson.id,
                        title = lesson.title,
                        totalScreens = parsedScreens.size,
                        screens = parsedScreens
                    )

                    _state.value = _state.value.copy(
                        lesson = interactiveLesson,
                        isLoading = false,
                        errorMessage = null
                    )

                    println("🎉 [LESSON-VM] Lección cargada exitosamente con ${parsedScreens.size} pantallas")
                } else {
                    val errorMsg = "Error ${response.code()}: ${response.message()}"
                    println("❌ [LESSON-VM] $errorMsg")
                    _state.value = _state.value.copy(
                        errorMessage = errorMsg,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                val errorMsg = "Error cargando lección: ${e.message}"
                println("💥 [LESSON-VM] $errorMsg")
                e.printStackTrace()
                _state.value = _state.value.copy(
                    errorMessage = errorMsg,
                    isLoading = false
                )
            }
        }
    }

    private fun parseScreensFromJson(screensJson: String?): List<InteractiveScreen> {
        println("🔧 [PARSER] Iniciando parseo de screens")

        if (screensJson.isNullOrEmpty()) {
            println("⚠️ [PARSER] Screens JSON vacío o nulo")
            return listOf(
                InteractiveScreen(
                    screenNumber = 1,
                    type = "fallback",
                    title = "Contenido no disponible",
                    content = mapOf("message" to "Esta lección no tiene contenido interactivo")
                )
            )
        }

        println("📏 [PARSER] JSON length: ${screensJson.length}")
        println("📄 [PARSER] JSON preview: ${screensJson.take(200)}...")

        return try {
            val gson = Gson()
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            val screensData: List<Map<String, Any>> = gson.fromJson(screensJson, type)

            println("📊 [PARSER] Raw screens count: ${screensData.size}")

            val parsedScreens = screensData.mapNotNull { screenMap ->
                try {
                    val screenNumber = when (val num = screenMap["screen_number"]) {
                        is Double -> num.toInt()
                        is Int -> num
                        is String -> num.toIntOrNull() ?: 0
                        else -> 0
                    }

                    val screen = InteractiveScreen(
                        screenNumber = screenNumber,
                        type = screenMap["type"] as? String ?: "default",
                        title = screenMap["title"] as? String ?: "",
                        subtitle = screenMap["subtitle"] as? String,
                        content = screenMap["content"] as? Map<String, Any> ?: emptyMap(),
                        ctaButton = screenMap["cta_button"] as? String,
                        emailData = screenMap["email_data"],
                        signals = screenMap["signals"],
                        hint = screenMap["hint"] as? String,
                        items = screenMap["items"],
                        tip = screenMap["tip"] as? String,
                        steps = screenMap["steps"],
                        reminder = screenMap["reminder"] as? String,
                        questions = screenMap["questions"]
                    )

                    println("✅ [PARSER] Screen ${screen.screenNumber} parseada: ${screen.type} - ${screen.title}")
                    screen
                } catch (e: Exception) {
                    println("❌ [PARSER] Error parseando pantalla individual: ${e.message}")
                    e.printStackTrace()
                    null
                }
            }

            println("🎯 [PARSER] Total screens parseadas exitosamente: ${parsedScreens.size}")
            parsedScreens

        } catch (e: Exception) {
            println("💥 [PARSER] Error parseando JSON completo: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getToken(): String {
        return userRepository.getToken().first() ?: ""
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}