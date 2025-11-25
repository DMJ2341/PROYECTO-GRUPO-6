package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.models.assessments.PreferenceQuestion
import com.example.cyberlearnapp.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferenceTestViewModel @Inject constructor(
    private val repository: PreferenceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PreferenceTestState())
    val state: StateFlow<PreferenceTestState> = _state

    private val answers = mutableMapOf<Int, String>()

    fun loadQuestions() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val questions = repository.getQuestions()
                _state.value = _state.value.copy(
                    questions = questions,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido al cargar preguntas"
                )
            }
        }
    }

    fun selectAnswer(pageIndex: Int, optionId: String) {
        answers[pageIndex] = optionId
        _state.value = _state.value.copy(answers = answers.toMap())
    }

    fun submitTest() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSubmitting = true, error = null)
            try {
                // El backend espera las respuestas indexadas por ID de pregunta (no índice de página)
                // Asumimos que questions[pageIndex].id es el ID correcto
                val formattedAnswers = answers.mapKeys { entry ->
                    _state.value.questions[entry.key].id.toString()
                }

                val result = repository.submitTest(formattedAnswers)

                _state.value = _state.value.copy(
                    isSubmitting = false,
                    isSubmissionSuccess = true,
                    resultProfile = result.profile
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    error = "Error al enviar: ${e.message}"
                )
            }
        }
    }
}

data class PreferenceTestState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSubmissionSuccess: Boolean = false,
    val resultProfile: String? = null,
    val questions: List<PreferenceQuestion> = emptyList(),
    val answers: Map<Int, String> = emptyMap(), // Map<PageIndex, OptionId>
    val error: String? = null
)