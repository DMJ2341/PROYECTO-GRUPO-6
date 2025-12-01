package com.example.cyberlearnapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.models.*
import com.example.cyberlearnapp.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _questions = MutableStateFlow<List<TestQuestion>>(emptyList())
    val questions: StateFlow<List<TestQuestion>> = _questions.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _answers = MutableStateFlow<Map<String, Int>>(emptyMap())
    val answers: StateFlow<Map<String, Int>> = _answers.asStateFlow()

    private val _result = MutableStateFlow<TestResult?>(null)
    val result: StateFlow<TestResult?> = _result.asStateFlow()

    private val _recommendations = MutableStateFlow<Recommendations?>(null)
    val recommendations: StateFlow<Recommendations?> = _recommendations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isCompleted = MutableStateFlow(false)
    val isCompleted: StateFlow<Boolean> = _isCompleted.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var startTime: Long = 0

    fun loadQuestions(token: String) {
        // Evitar recargar si ya tenemos preguntas
        if (_questions.value.isNotEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getQuestions("Bearer $token")
                if (response.isSuccessful && response.body()?.success == true) {
                    _questions.value = response.body()!!.questions
                    startTime = System.currentTimeMillis()
                    Log.d("TestViewModel", "✅ Preguntas cargadas: ${_questions.value.size}")
                } else {
                    _error.value = "Error cargando preguntas: ${response.code()}"
                    Log.e("TestViewModel", "❌ Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
                Log.e("TestViewModel", "❌ Exception: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun answerQuestion(questionId: Int, rating: Int) {
        _answers.value = _answers.value + (questionId.toString() to rating)
        Log.d("TestViewModel", "📝 Respuesta guardada - Q$questionId: $rating")

        if (_currentIndex.value < _questions.value.size - 1) {
            _currentIndex.value += 1
        } else {
            // Al responder la última, marcamos completado localmente.
            // La UI observará esto para disparar el submitTest.
            Log.d("TestViewModel", "🏁 Todas las preguntas respondidas.")
            _isCompleted.value = true
        }
    }

    fun previousQuestion() {
        if (_currentIndex.value > 0) {
            _currentIndex.value -= 1
        }
    }

    fun submitTest(token: String) {
        // Evitar doble envío si ya estamos cargando o ya tenemos resultado
        if (_isLoading.value || _result.value != null) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val timeTaken = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                val submission = TestSubmission(
                    answers = _answers.value,
                    timeTaken = timeTaken
                )

                Log.d("TestViewModel", "📤 Enviando ${_answers.value.size} respuestas al servidor...")

                val response = apiService.submitTest("Bearer $token", submission)

                if (response.isSuccessful && response.body()?.success == true) {
                    val responseResult = response.body()!!.result
                    // Guardamos el resultado. Al dejar de ser null, la UI navegará.
                    _result.value = responseResult
                    Log.d("TestViewModel", "✅ Resultado recibido: ${responseResult.recommendedRole}")

                    // Cargar recomendaciones en segundo plano
                    loadRecommendations(token, responseResult.recommendedRole)
                } else {
                    _error.value = "Error enviando test: ${response.code()}"
                    Log.e("TestViewModel", "❌ Error API: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error de conexión al enviar"
                Log.e("TestViewModel", "❌ Exception submit: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadRecommendations(token: String, role: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getRecommendations("Bearer $token", role)
                if (response.isSuccessful && response.body()?.success == true) {
                    _recommendations.value = response.body()!!.recommendations
                }
            } catch (e: Exception) {
                Log.e("TestViewModel", "❌ Error recomendaciones: ${e.message}")
            }
        }
    }

    fun resetTest() {
        _currentIndex.value = 0
        _answers.value = emptyMap()
        _result.value = null
        _recommendations.value = null
        _isCompleted.value = false
        _error.value = null
        startTime = System.currentTimeMillis()
        Log.d("TestViewModel", "🔄 Test reiniciado")
    }

    fun getProgress(): Float {
        if (_questions.value.isEmpty()) return 0f
        return (_currentIndex.value + 1) / _questions.value.size.toFloat()
    }

    fun canGoBack(): Boolean = _currentIndex.value > 0

    fun clearError() {
        _error.value = null
    }
}