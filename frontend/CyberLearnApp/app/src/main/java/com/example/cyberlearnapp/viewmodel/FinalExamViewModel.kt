package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.cyberlearnapp.network.models.assessments.*
import com.example.cyberlearnapp.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinalExamViewModel @Inject constructor(
    private val repo: ExamRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ExamState())
    val state: StateFlow<ExamState> = _state

    fun startExam() {
        if (_state.value.questions.isNotEmpty()) return // Evitar recargar si rota la pantalla

        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val data = repo.startExam()
                _state.value = _state.value.copy(
                    questions = data.questions,
                    attempt = data.attempt_number,
                    loading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message ?: "Error desconocido", loading = false)
            }
        }
    }

    fun selectAnswer(qId: String, answer: String) {
        val current = _state.value.answers.toMutableMap()
        current[qId] = answer
        _state.value = _state.value.copy(answers = current)
    }

    fun submit(navController: NavController) {
        if (_state.value.submitting) return

        viewModelScope.launch {
            _state.value = _state.value.copy(submitting = true, error = null)
            try {
                val result = repo.submitExam(_state.value.answers)
                _state.value = _state.value.copy(
                    result = result,
                    submitting = false
                )
                // Navegación segura al hilo principal
                navController.navigate("final_exam/result") {
                    popUpTo("final_exam/take") { inclusive = true }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, submitting = false)
            }
        }
    }
}

data class ExamState(
    val loading: Boolean = false,
    val submitting: Boolean = false,
    val questions: List<FinalExamQuestion> = emptyList(),
    val answers: Map<String, String> = emptyMap(),
    val attempt: Int = 1,
    val result: ExamResultResponse? = null,
    val error: String? = null
)