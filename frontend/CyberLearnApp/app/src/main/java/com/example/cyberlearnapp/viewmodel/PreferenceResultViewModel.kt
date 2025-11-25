// app/src/main/java/com/example/cyberlearnapp/viewmodel/PreferenceResultViewModel.kt
package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.models.assessments.ProfileUiData
import com.example.cyberlearnapp.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferenceResultViewModel @Inject constructor(
    private val repository: PreferenceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PreferenceResultState())
    val state: StateFlow<PreferenceResultState> = _state

    fun loadResult(profileSlug: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // Obtenemos el resultado guardado que incluye el ui_data visual
                val result = repository.getSavedResult()
                if (result != null && result.uiData != null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        uiData = result.uiData,
                        profileName = result.profile
                    )
                } else {
                    _state.value = _state.value.copy(isLoading = false, error = "No se encontraron resultados.")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}

data class PreferenceResultState(
    val isLoading: Boolean = false,
    val uiData: ProfileUiData? = null,
    val profileName: String = "",
    val error: String? = null
)