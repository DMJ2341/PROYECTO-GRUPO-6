package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.models.GlossaryStats
import com.example.cyberlearnapp.network.models.GlossaryTerm
import com.example.cyberlearnapp.repository.GlossaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ==========================================
// ENUMS Y DATA CLASSES PARA PRÁCTICA
// ==========================================

enum class PracticeMode {
    FLASHCARD,  // Mostrar término → Recordar definición
    QUIZ        // Mostrar definición → Elegir término correcto
}

data class PracticeSession(
    val terms: List<GlossaryTerm>,
    val currentIndex: Int = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val mode: PracticeMode = PracticeMode.FLASHCARD,
    val isFinished: Boolean = false
) {
    val currentTerm: GlossaryTerm?
        get() = terms.getOrNull(currentIndex)

    val progress: Float
        get() = if (terms.isEmpty()) 0f else (currentIndex.toFloat() / terms.size)

    val totalAnswered: Int
        get() = correctCount + incorrectCount

    val accuracy: Float
        get() = if (totalAnswered == 0) 0f else (correctCount.toFloat() / totalAnswered * 100)
}

@HiltViewModel
class GlossaryViewModel @Inject constructor(
    private val repository: GlossaryRepository
) : ViewModel() {

    // ==========================================
    // ESTADO EXISTENTE
    // ==========================================

    private val _terms = MutableStateFlow<List<GlossaryTerm>>(emptyList())
    val terms: StateFlow<List<GlossaryTerm>> = _terms

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _stats = MutableStateFlow<GlossaryStats?>(null)
    val stats: StateFlow<GlossaryStats?> = _stats

    private val _mode = MutableStateFlow("learn")
    val mode: StateFlow<String> = _mode

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    // ==========================================
    // ✨ NUEVO: ESTADO DE PRÁCTICA
    // ==========================================

    private val _practiceSession = MutableStateFlow<PracticeSession?>(null)
    val practiceSession: StateFlow<PracticeSession?> = _practiceSession

    // Lista completa para generar opciones de quiz
    private val _allTerms = MutableStateFlow<List<GlossaryTerm>>(emptyList())

    init {
        loadTerms()
        loadStats()
    }

    // ==========================================
    // FUNCIONES EXISTENTES
    // ==========================================

    fun loadTerms(query: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getTerms(query)
                _terms.value = filterTerms(result)
                _allTerms.value = result // Guardar para quiz
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadStats() {
        viewModelScope.launch {
            try {
                val result = repository.getStats()
                _stats.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setMode(newMode: String) {
        _mode.value = newMode

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = if (newMode == "practice") {
                    repository.getLearnedTerms()
                } else {
                    repository.getTerms()
                }
                _terms.value = filterTerms(result)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
        loadTerms()
    }

    private fun filterTerms(terms: List<GlossaryTerm>): List<GlossaryTerm> {
        val category = _selectedCategory.value
        return if (category != null) {
            terms.filter { it.category == category }
        } else {
            terms
        }
    }

    fun toggleLearned(termId: Int, currentState: Boolean) {
        viewModelScope.launch {
            val newState = !currentState
            val success = repository.markAsLearned(termId, newState)

            if (success) {
                _terms.value = _terms.value.map { term ->
                    if (term.id == termId) {
                        term.copy(isLearned = newState)
                    } else {
                        term
                    }
                }
                loadStats()
            }
        }
    }

    fun recordQuizAttempt(termId: Int, isCorrect: Boolean, onResult: (Double) -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.recordQuizAttempt(termId, isCorrect)
                if (result != null) {
                    onResult(result.accuracy)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ==========================================
    // ✨ NUEVAS FUNCIONES DE PRÁCTICA
    // ==========================================

    /**
     * Inicia una sesión de práctica con los términos aprendidos
     */
    fun startPracticeSession(mode: PracticeMode, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Obtener términos aprendidos
                val learnedTerms = repository.getLearnedTerms()

                if (learnedTerms.isEmpty()) {
                    _practiceSession.value = null
                } else {
                    // Mezclar para que no sea predecible
                    val shuffledTerms = learnedTerms.shuffled().take(15)

                    _practiceSession.value = PracticeSession(
                        terms = shuffledTerms,
                        mode = mode
                    )

                    // ✅ AVISAR A LA UI QUE YA PUEDE NAVEGAR
                    onSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _practiceSession.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
    /**
     * Avanza a la siguiente pregunta
     */
    fun nextQuestion() {
        val currentSession = _practiceSession.value ?: return

        val nextIndex = currentSession.currentIndex + 1

        if (nextIndex >= currentSession.terms.size) {
            // Sesión terminada
            _practiceSession.value = currentSession.copy(
                isFinished = true
            )
        } else {
            _practiceSession.value = currentSession.copy(
                currentIndex = nextIndex
            )
        }
    }

    /**
     * Registra una respuesta en la sesión de práctica
     */
    fun recordAnswer(isCorrect: Boolean) {
        val currentSession = _practiceSession.value ?: return
        val currentTerm = currentSession.currentTerm ?: return

        // Actualizar contadores locales
        _practiceSession.value = currentSession.copy(
            correctCount = if (isCorrect) currentSession.correctCount + 1 else currentSession.correctCount,
            incorrectCount = if (!isCorrect) currentSession.incorrectCount + 1 else currentSession.incorrectCount
        )

        // Registrar en el backend
        recordQuizAttempt(currentTerm.id, isCorrect) { _ -> }
    }

    /**
     * Finaliza la sesión de práctica actual
     */
    fun endPracticeSession() {
        _practiceSession.value = null
    }

    /**
     * Genera opciones para un quiz de opción múltiple
     * Devuelve 4 términos: el correcto + 3 incorrectos
     */
    fun generateQuizOptions(correctTerm: GlossaryTerm): List<GlossaryTerm> {
        val allAvailable = _allTerms.value.ifEmpty { _terms.value }

        // Filtrar términos de la misma categoría (más realista)
        val sameCategory = allAvailable.filter {
            it.id != correctTerm.id && it.category == correctTerm.category
        }

        // Si no hay suficientes de la misma categoría, usar cualquiera
        val candidates = if (sameCategory.size >= 3) sameCategory else allAvailable.filter { it.id != correctTerm.id }

        // Tomar 3 incorrectos al azar
        val incorrectTerms = candidates.shuffled().take(3)

        // Combinar con el correcto y mezclar
        return (incorrectTerms + correctTerm).shuffled()
    }
}