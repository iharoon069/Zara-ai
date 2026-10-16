package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceManager(
    private val context: Context,
    private val onSpeechRecognized: (String) -> Unit,
    private val onListeningStateChanged: (Boolean) -> Unit
) : TextToSpeech.OnInitListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _audioAmplitude = MutableStateFlow(0f)
    val audioAmplitude: StateFlow<Float> = _audioAmplitude.asStateFlow()

    private val _spokenTextLive = MutableStateFlow("")
    val spokenTextLive: StateFlow<String> = _spokenTextLive.asStateFlow()

    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        mainHandler.post {
            initSpeechRecognizer()
            textToSpeech = TextToSpeech(context, this)
        }
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                        onListeningStateChanged(true)
                    }

                    override fun onBeginningOfSpeech() {
                        _isListening.value = true
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Normalize -2dB..10dB to 0..1
                        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                        _audioAmplitude.value = normalized
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                        _audioAmplitude.value = 0f
                        onListeningStateChanged(false)
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        _audioAmplitude.value = 0f
                        onListeningStateChanged(false)
                        Log.d("VoiceManager", "Speech recognition error: $error")
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        _audioAmplitude.value = 0f
                        onListeningStateChanged(false)
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim()
                        if (!text.isNullOrBlank()) {
                            _spokenTextLive.value = text
                            onSpeechRecognized(text)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let {
                            _spokenTextLive.value = it
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        } else {
            Log.w("VoiceManager", "Speech recognition not available on device")
        }
    }

    fun startListening() {
        // Cancel any active speech immediately when user starts talking
        stopSpeaking()

        mainHandler.post {
            try {
                if (speechRecognizer == null) {
                    initSpeechRecognizer()
                }
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                }
                _spokenTextLive.value = ""
                speechRecognizer?.startListening(intent)
                _isListening.value = true
                onListeningStateChanged(true)
            } catch (e: Exception) {
                Log.e("VoiceManager", "Error starting listening", e)
                _isListening.value = false
                onListeningStateChanged(false)
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
                _isListening.value = false
                _audioAmplitude.value = 0f
                onListeningStateChanged(false)
            } catch (e: Exception) {
                Log.e("VoiceManager", "Error stopping listening", e)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            textToSpeech?.let { tts ->
                // Try selecting available English or localized voice
                val result = tts.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.setLanguage(Locale.getDefault())
                }
                // Try to set a feminine voice if available in the voice set
                try {
                    val voices = tts.voices
                    val feminineVoice = voices?.firstOrNull { voice ->
                        voice.name.contains("female", ignoreCase = true) ||
                                voice.name.contains("en-us-x-sfg#female", ignoreCase = true) ||
                                voice.name.contains("f00", ignoreCase = true)
                    }
                    if (feminineVoice != null) {
                        tts.voice = feminineVoice
                    }
                } catch (e: Exception) {
                    Log.d("VoiceManager", "Default voice maintained")
                }

                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        _audioAmplitude.value = 0f
                    }

                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        _audioAmplitude.value = 0f
                    }
                })
            }
        }
    }

    fun speak(text: String, pitch: Float = 1.15f, rate: Float = 1.02f) {
        if (!isTtsInitialized || text.isBlank()) return
        stopListening()
        mainHandler.post {
            try {
                // Strip out action tags so Zara doesn't read "[ACTION:...]" aloud
                val cleanSpeech = text
                    .replace(Regex("\\[ACTION:[^\\]]+\\]"), "")
                    .trim()

                if (cleanSpeech.isNotEmpty()) {
                    textToSpeech?.setPitch(pitch)
                    textToSpeech?.setSpeechRate(rate)
                    val utteranceId = "zara_utterance_${System.currentTimeMillis()}"
                    textToSpeech?.speak(cleanSpeech, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
                    _isSpeaking.value = true
                }
            } catch (e: Exception) {
                Log.e("VoiceManager", "TTS speak error", e)
                _isSpeaking.value = false
            }
        }
    }

    fun stopSpeaking() {
        mainHandler.post {
            try {
                textToSpeech?.stop()
                _isSpeaking.value = false
                _audioAmplitude.value = 0f
            } catch (e: Exception) {
                Log.e("VoiceManager", "TTS stop error", e)
            }
        }
    }

    fun release() {
        try {
            speechRecognizer?.destroy()
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        } catch (e: Exception) {
            Log.e("VoiceManager", "Release error", e)
        }
    }
}
