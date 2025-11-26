package com.example.cyberlearnapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberlearnapp.network.models.User
import com.example.cyberlearnapp.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userProfile = userRepository.getUserProfile()
                _user.value = userProfile
            } catch (e: Exception) {
                // Manejar error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        userRepository.logout()
        _user.value = null
    }
}