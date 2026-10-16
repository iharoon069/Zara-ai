package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ZaraPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("zara_ai_prefs", Context.MODE_PRIVATE)

    private val _userNickname = MutableStateFlow(prefs.getString("user_nickname", "Jaan") ?: "Jaan")
    val userNickname: StateFlow<String> = _userNickname.asStateFlow()

    private val _preferredLanguage = MutableStateFlow(
        prefs.getString("preferred_language", "Roman Urdu & English") ?: "Roman Urdu & English"
    )
    val preferredLanguage: StateFlow<String> = _preferredLanguage.asStateFlow()

    private val _personalityMode = MutableStateFlow(
        prefs.getString("personality_mode", "girlfriend") ?: "girlfriend"
    )
    val personalityMode: StateFlow<String> = _personalityMode.asStateFlow()

    private val _voicePitch = MutableStateFlow(prefs.getFloat("voice_pitch", 1.15f))
    val voicePitch: StateFlow<Float> = _voicePitch.asStateFlow()

    private val _speechRate = MutableStateFlow(prefs.getFloat("speech_rate", 1.02f))
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _ttsEnabled = MutableStateFlow(prefs.getBoolean("tts_enabled", true))
    val ttsEnabled: StateFlow<Boolean> = _ttsEnabled.asStateFlow()

    private val _continuousListening = MutableStateFlow(prefs.getBoolean("continuous_listening", false))
    val continuousListening: StateFlow<Boolean> = _continuousListening.asStateFlow()

    private val _customApiKey = MutableStateFlow(prefs.getString("custom_api_key", "") ?: "")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(
        prefs.getBoolean("onboarding_completed", false)
    )
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(prefs.getBoolean("haptics_enabled", true))
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    fun setUserNickname(value: String) {
        prefs.edit().putString("user_nickname", value).apply()
        _userNickname.value = value
    }

    fun setPreferredLanguage(value: String) {
        prefs.edit().putString("preferred_language", value).apply()
        _preferredLanguage.value = value
    }

    fun setPersonalityMode(value: String) {
        prefs.edit().putString("personality_mode", value).apply()
        _personalityMode.value = value
    }

    fun setVoicePitch(value: Float) {
        prefs.edit().putFloat("voice_pitch", value).apply()
        _voicePitch.value = value
    }

    fun setSpeechRate(value: Float) {
        prefs.edit().putFloat("speech_rate", value).apply()
        _speechRate.value = value
    }

    fun setTtsEnabled(value: Boolean) {
        prefs.edit().putBoolean("tts_enabled", value).apply()
        _ttsEnabled.value = value
    }

    fun setContinuousListening(value: Boolean) {
        prefs.edit().putBoolean("continuous_listening", value).apply()
        _continuousListening.value = value
    }

    fun setCustomApiKey(value: String) {
        prefs.edit().putString("custom_api_key", value).apply()
        _customApiKey.value = value
    }

    fun setOnboardingCompleted(value: Boolean) {
        prefs.edit().putBoolean("onboarding_completed", value).apply()
        _onboardingCompleted.value = value
    }

    fun setHapticsEnabled(value: Boolean) {
        prefs.edit().putBoolean("haptics_enabled", value).apply()
        _hapticsEnabled.value = value
    }
}
