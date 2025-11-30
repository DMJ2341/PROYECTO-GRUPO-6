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

@HiltViewModel
class GlossaryViewModel @Inject constructor(
    private val repository: GlossaryRepository
) : ViewModel() {

    private val _terms = MutableStateFlow<List<GlossaryTerm>>(emptyList())
    val terms: StateFlow<List<GlossaryTerm>> = _terms

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _stats = MutableStateFlow<GlossaryStats?>(null)
    val stats: StateFlow<GlossaryStats?> = _stats

    // Modo actual: "learn" o "practice"
    private val _mode = MutableStateFlow("learn")
    val mode: StateFlow<String> = _mode

    // Filtro de categoría
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    init {
        loadTerms()
        loadStats()
    }

    fun loadTerms(query: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getTerms(query)
                _terms.value = filterTerms(result)
            } catch (e: Exception) {
                // Manejo de errores básico
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
                    // Solo términos aprendidos para practicar
                    repository.getLearnedTerms()
                } else {
                    // Todos los términos para aprender
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
        // Re-filtrar términos actuales (esto requeriría recargar o filtrar la lista en memoria)
        // Para simplificar, recargamos con el filtro aplicado:
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
            // Llamada optimista a la API
            val success = repository.markAsLearned(termId, newState)

            if (success) {
                // Actualizar localmente usando el copy nativo de Kotlin
                _terms.value = _terms.value.map { term ->
                    if (term.id == termId) {
                        // ✅ CORREGIDO: Usamos el copy nativo de la data class
                        term.copy(isLearned = newState)
                    } else {
                        term
                    }
                }
                // Recargar estadísticas
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
}
