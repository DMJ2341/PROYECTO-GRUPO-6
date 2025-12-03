package com.example.cyberlearnapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.models.*
import com.example.cyberlearnapp.repository.TestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val testRepository: TestRepository
) : ViewModel() {

    private val _questions = MutableStateFlow<List<TestQuestion>>(emptyList())
    val questions: StateFlow<List<TestQuestion>> = _questions.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _answers = MutableStateFlow<Map<String, Int>>(emptyMap())
    val answers: StateFlow<Map<String, Int>> = _answers.asStateFlow()

    private val _result = MutableStateFlow<TestResult?>(null)
    val result: StateFlow<TestResult?> = _result.asStateFlow()

    // ✅ NUEVO: Para saber si hay resultado previo
    private val _currentResult = MutableStateFlow<TestResult?>(null)
    val currentResult: StateFlow<TestResult?> = _currentResult.asStateFlow()

    private val _recommendations = MutableStateFlow<Recommendations?>(null)
    val recommendations: StateFlow<Recommendations?> = _recommendations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isCompleted = MutableStateFlow(false)
    val isCompleted: StateFlow<Boolean> = _isCompleted.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var startTime: Long = 0

    // ✅ NUEVA FUNCIÓN: Verificar resultado previo
    fun checkExistingResult(token: String) {
        viewModelScope.launch {
            try {
                Log.d("TestViewModel", "🔍 checkExistingResult - INICIO")
                val response = testRepository.getPreviousResult(token)

                Log.d("TestViewModel", "📦 Response recibida: hasResult=${response.hasResult}, result=${response.result}")

                if (response.hasResult && response.result != null) {
                    _currentResult.value = response.result
                    _result.value = response.result // También lo guardamos en result
                    Log.d("TestViewModel", "✅ _currentResult.value actualizado a: ${_currentResult.value?.recommendedRole}")
                    Log.d("TestViewModel", "✅ _result.value actualizado a: ${_result.value?.recommendedRole}")

                    // Cargar recomendaciones automáticamente
                    loadRecommendations(token, response.result.recommendedRole)
                } else {
                    _currentResult.value = null
                    _result.value = null
                    Log.d("TestViewModel", "ℹ️ No hay resultado previo")
                }
            } catch (e: Exception) {
                Log.e("TestViewModel", "❌ Error verificando resultado: ${e.message}")
                _currentResult.value = null
                _result.value = null
            }
        }
    }

    fun loadQuestions(token: String) {
        if (_questions.value.isNotEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = testRepository.getQuestions(token)
                _questions.value = response.questions
                startTime = System.currentTimeMillis()
                Log.d("TestViewModel", "✅ Preguntas cargadas: ${_questions.value.size}")
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

                val response = testRepository.submitTest(token, submission)
                _result.value = response.result
                _currentResult.value = response.result // ✅ También actualizamos currentResult
                Log.d("TestViewModel", "✅ Resultado recibido: ${response.result.recommendedRole}")

                loadRecommendations(token, response.result.recommendedRole)
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
                val response = testRepository.getRecommendations(token, role)
                _recommendations.value = response.recommendations
            } catch (e: Exception) {
                Log.e("TestViewModel", "❌ Error recomendaciones: ${e.message}")
            }
        }
    }

    // ✅ NUEVA FUNCIÓN: Reset completo (borra resultado local)
    fun resetTest() {
        _currentIndex.value = 0
        _answers.value = emptyMap()
        _result.value = null
        _currentResult.value = null // ✅ También limpiamos el resultado previo
        _recommendations.value = null
        _isCompleted.value = false
        _error.value = null
        _questions.value = emptyList() // ✅ Forzar recarga de preguntas
        startTime = System.currentTimeMillis()
        Log.d("TestViewModel", "🔄 Test reiniciado completamente")
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