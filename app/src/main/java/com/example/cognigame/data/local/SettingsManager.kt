package com.example.cognigame.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cognigame_settings", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(prefs.getString("language", "English") ?: "English")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _textScale = MutableStateFlow(prefs.getFloat("text_scale", 1.0f))
    val textScale: StateFlow<Float> = _textScale.asStateFlow()

    private val _highContrast = MutableStateFlow(prefs.getBoolean("high_contrast", false))
    val highContrast: StateFlow<Boolean> = _highContrast.asStateFlow()

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean("sound_enabled", true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(prefs.getBoolean("vibration_enabled", true))
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    fun setLanguage(language: String) {
        prefs.edit().putString("language", language).apply()
        _language.value = language
    }

    fun setTextScale(scale: Float) {
        prefs.edit().putFloat("text_scale", scale).apply()
        _textScale.value = scale
    }

    fun setHighContrast(enabled: Boolean) {
        prefs.edit().putBoolean("high_contrast", enabled).apply()
        _highContrast.value = enabled
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
        _soundEnabled.value = enabled
    }

    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("vibration_enabled", enabled).apply()
        _vibrationEnabled.value = enabled
    }
}
