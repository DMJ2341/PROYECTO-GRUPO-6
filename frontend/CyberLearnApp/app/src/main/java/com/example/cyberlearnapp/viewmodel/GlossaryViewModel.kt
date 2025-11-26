package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        loadTerms()
    }

    fun loadTerms(query: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getTerms(query)
            _terms.value = result
            _isLoading.value = false
        }
    }
}