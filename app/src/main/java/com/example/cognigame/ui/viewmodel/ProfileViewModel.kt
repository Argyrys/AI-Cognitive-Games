package com.example.cognigame.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognigame.data.model.UserProfile
import com.example.cognigame.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProfileRepository()

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadProfile("user_1")
    }

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getProfile(userId)
            result.onSuccess {
                _profile.value = it
            }
            result.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.saveProfile(profile)
            result.onSuccess {
                _saveSuccess.value = true
                loadProfile(profile.id)
            }
            result.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
}
