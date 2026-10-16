package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.api.GeminiResult
import com.example.data.local.AutomationTask
import com.example.data.local.ConversationMessage
import com.example.data.local.UserMemory
import com.example.data.local.ZaraDatabase
import com.example.data.local.ZaraPreferences
import com.example.device.ActionResult
import com.example.device.DeviceActionManager
import com.example.service.ZaraAccessibilityService
import com.example.voice.VoiceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AiCoreState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING,
    EXECUTING
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ZaraDatabase.getDatabase(application)
    private val conversationDao = database.conversationDao()
    private val memoryDao = database.memoryDao()
    private val taskDao = database.taskDao()

    val preferences = ZaraPreferences(application)
    val deviceActionManager = DeviceActionManager(application)

    private val geminiClient = GeminiClient(
        getCustomApiKey = { preferences.customApiKey.value }
    )

    private val _aiState = MutableStateFlow(AiCoreState.IDLE)
    val aiState: StateFlow<AiCoreState> = _aiState.asStateFlow()

    private val _statusText = MutableStateFlow("CORE ONLINE // STANDBY")
    val statusText: StateFlow<String> = _statusText.asStateFlow()

    private val _lastActivity = MutableStateFlow("Zara AI initialized. Ready for voice or touch commands.")
    val lastActivity: StateFlow<String> = _lastActivity.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _liveScreenAnalysis = MutableStateFlow<String?>(null)
    val liveScreenAnalysis: StateFlow<String?> = _liveScreenAnalysis.asStateFlow()

    // Voice manager
    lateinit var voiceManager: VoiceManager
        private set

    val messages = conversationDao.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val memories = memoryDao.getAllMemories()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val tasks = taskDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val recentPreviewMessage = conversationDao.getRecentMessages(1)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    init {
        voiceManager = VoiceManager(
            context = application,
            onSpeechRecognized = { spokenText ->
                handleUserInput(spokenText)
            },
            onListeningStateChanged = { isListening ->
                if (isListening) {
                    _aiState.value = AiCoreState.LISTENING
                    _statusText.value = "LISTENING TO VOICE..."
                    vibrateQuick()
                } else if (_aiState.value == AiCoreState.LISTENING) {
                    _aiState.value = AiCoreState.IDLE
                    _statusText.value = "CORE ONLINE // STANDBY"
                }
            }
        )

        // Monitor speech playback state to update AI state
        viewModelScope.launch {
            voiceManager.isSpeaking.collect { isSpeaking ->
                if (isSpeaking) {
                    _aiState.value = AiCoreState.SPEAKING
                    _statusText.value = "ZARA IS SPEAKING..."
                } else if (_aiState.value == AiCoreState.SPEAKING) {
                    _aiState.value = AiCoreState.IDLE
                    _statusText.value = "CORE ONLINE // STANDBY"
                }
            }
        }

        // Prepopulate warm memory if empty
        viewModelScope.launch {
            if (memoryDao.getAllMemories().firstOrNull()?.isEmpty() == true) {
                memoryDao.insertMemory(
                    UserMemory(
                        category = "nickname",
                        key = "callsign",
                        value = "Prefers to be called Jaan",
                        isPinned = true
                    )
                )
                memoryDao.insertMemory(
                    UserMemory(
                        category = "preference",
                        key = "language",
                        value = "Understands Roman Urdu, Urdu and English natively",
                        isPinned = true
                    )
                )
            }
        }
    }

    fun setInputText(text: String) {
        _inputText.value = text
    }

    fun submitCurrentInput() {
        val text = _inputText.value.trim()
        if (text.isNotEmpty()) {
            _inputText.value = ""
            handleUserInput(text)
        }
    }

    fun toggleVoiceListening() {
        if (voiceManager.isListening.value) {
            voiceManager.stopListening()
            _aiState.value = AiCoreState.IDLE
            _statusText.value = "CORE ONLINE // STANDBY"
        } else {
            voiceManager.stopSpeaking()
            voiceManager.startListening()
        }
    }

    fun stopSpeaking() {
        voiceManager.stopSpeaking()
        _aiState.value = AiCoreState.IDLE
        _statusText.value = "CORE ONLINE // STANDBY"
    }

    fun handleUserInput(prompt: String) {
        if (prompt.isBlank()) return
        vibrateQuick()

        viewModelScope.launch {
            // Save user message
            conversationDao.insertMessage(
                ConversationMessage(
                    role = "user",
                    content = prompt,
                    mode = preferences.personalityMode.value
                )
            )

            _aiState.value = AiCoreState.THINKING
            _statusText.value = "ANALYZING INTENT..."
            _lastActivity.value = "Processing: \"$prompt\""

            val history = messages.value.takeLast(6).map {
                it.role to it.content
            }
            val memoryList = memories.value.map { "${it.key}: ${it.value}" }
            val mode = preferences.personalityMode.value
            val nickname = preferences.userNickname.value

            val result = geminiClient.generateResponse(
                userPrompt = prompt,
                conversationHistory = history,
                userMemories = memoryList,
                mode = mode,
                userNickname = nickname
            )

            when (result) {
                is GeminiResult.Success -> {
                    var responseText = result.fallbackText
                    var executedActionName: String? = null
                    var actionStatus: String? = null

                    // Action tag regex
                    val actionMatch = Regex("\\[ACTION:([A-Z_]+)(?::([^\\]]+))?\\]").find(responseText)
                    if (actionMatch != null) {
                        val fullTag = actionMatch.value
                        val actionType = actionMatch.groupValues[1]
                        executedActionName = actionType

                        _aiState.value = AiCoreState.EXECUTING
                        _statusText.value = "EXECUTING: $actionType"

                        // Record task in database
                        val taskId = taskDao.insertTask(
                            AutomationTask(
                                originalCommand = prompt,
                                actionType = actionType,
                                parameters = actionMatch.groupValues.getOrNull(2) ?: "",
                                status = "EXECUTING"
                            )
                        )

                        // Execute device action
                        val actionResult = deviceActionManager.executeAction(fullTag)
                        when (actionResult) {
                            is ActionResult.Success -> {
                                actionStatus = "SUCCESS"
                                taskDao.updateTaskStatus(taskId, "COMPLETED", actionResult.message)
                                _lastActivity.value = "Executed: ${actionResult.message}"
                            }
                            is ActionResult.Failure -> {
                                actionStatus = "FAILED"
                                taskDao.updateTaskStatus(taskId, "FAILED", actionResult.error)
                                _lastActivity.value = "Action failed: ${actionResult.error}"
                            }
                        }

                        // Special case: Screen reading
                        if (actionType == "READ_SCREEN") {
                            analyzeActiveScreen()
                        }
                    }

                    // Clean out action tag for display and speech
                    val cleanText = responseText.replace(Regex("\\[ACTION:[^\\]]+\\]"), "").trim()

                    // Insert Zara message
                    conversationDao.insertMessage(
                        ConversationMessage(
                            role = "zara",
                            content = cleanText.ifEmpty { "Action processed successfully!" },
                            mode = mode,
                            actionExecuted = executedActionName,
                            actionStatus = actionStatus
                        )
                    )

                    // Speak aloud if TTS enabled
                    if (preferences.ttsEnabled.value && cleanText.isNotEmpty()) {
                        voiceManager.speak(
                            text = cleanText,
                            pitch = preferences.voicePitch.value,
                            rate = preferences.speechRate.value
                        )
                    } else {
                        _aiState.value = AiCoreState.IDLE
                        _statusText.value = "CORE ONLINE // STANDBY"
                    }
                }
                is GeminiResult.Error -> {
                    val errorReply = "Main thoda sa disconnect ho gayi thi jaan, dobara boliye na?"
                    conversationDao.insertMessage(
                        ConversationMessage(
                            role = "zara",
                            content = errorReply,
                            mode = mode
                        )
                    )
                    _aiState.value = AiCoreState.IDLE
                    _statusText.value = "CORE ONLINE // STANDBY"
                    _lastActivity.value = "Network error: ${result.message}"
                }
            }
        }
    }

    fun analyzeActiveScreen() {
        viewModelScope.launch {
            _aiState.value = AiCoreState.THINKING
            _statusText.value = "READING ACTIVE SCREEN..."
            val screenContext = ZaraAccessibilityService.getScreenContext()
            _liveScreenAnalysis.value = screenContext

            val prompt = "Here is the active screen context from the user's phone:\n$screenContext\n\nExplain clearly to the user in a friendly girlfriend assistant tone what is on their screen and what actions they can take."
            val result = geminiClient.generateResponse(
                userPrompt = prompt,
                conversationHistory = emptyList(),
                userMemories = emptyList(),
                mode = preferences.personalityMode.value,
                userNickname = preferences.userNickname.value
            )

            if (result is GeminiResult.Success) {
                val clean = result.fallbackText.replace(Regex("\\[ACTION:[^\\]]+\\]"), "").trim()
                conversationDao.insertMessage(
                    ConversationMessage(
                        role = "zara",
                        content = clean,
                        mode = preferences.personalityMode.value,
                        actionExecuted = "SCREEN_UNDERSTANDING",
                        actionStatus = "SUCCESS"
                    )
                )
                if (preferences.ttsEnabled.value) {
                    voiceManager.speak(clean, preferences.voicePitch.value, preferences.speechRate.value)
                } else {
                    _aiState.value = AiCoreState.IDLE
                    _statusText.value = "CORE ONLINE // STANDBY"
                }
            } else {
                _aiState.value = AiCoreState.IDLE
                _statusText.value = "CORE ONLINE // STANDBY"
            }
        }
    }

    fun addMemory(category: String, key: String, value: String) {
        viewModelScope.launch {
            memoryDao.insertMemory(
                UserMemory(
                    category = category,
                    key = key,
                    value = value,
                    isPinned = false
                )
            )
        }
    }

    fun deleteMemory(memory: UserMemory) {
        viewModelScope.launch {
            memoryDao.deleteMemory(memory)
        }
    }

    fun clearAllConversations() {
        viewModelScope.launch {
            conversationDao.clearAllMessages()
            _lastActivity.value = "Conversation history cleared."
        }
    }

    fun clearAllTasks() {
        viewModelScope.launch {
            taskDao.clearAllTasks()
            _lastActivity.value = "Automation task history cleared."
        }
    }

    private fun vibrateQuick() {
        if (!preferences.hapticsEnabled.value) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(40)
            }
        } catch (e: Exception) {
            // Ignore vibration errors
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.release()
    }
}
