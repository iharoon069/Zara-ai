package com.example.data.api

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiClient(
    private val getCustomApiKey: () -> String
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateResponse(
        userPrompt: String,
        conversationHistory: List<Pair<String, String>>, // role ("user"/"model"), text
        userMemories: List<String>,
        mode: String,
        userNickname: String,
        screenBitmap: Bitmap? = null
    ): GeminiResult = withContext(Dispatchers.IO) {
        val customKey = getCustomApiKey().trim()
        val envKey = try {
            BuildConfig.GEMINI_API_KEY.trim()
        } catch (e: Throwable) {
            ""
        }

        val activeKey = when {
            customKey.isNotEmpty() -> customKey
            envKey.isNotEmpty() && envKey != "MY_GEMINI_API_KEY" -> envKey
            else -> ""
        }

        val systemPrompt = buildSystemInstruction(mode, userNickname, userMemories)

        // If no real API key is configured yet, use local conversational logic with device action recognition
        if (activeKey.isEmpty() || activeKey == "MY_GEMINI_API_KEY") {
            Log.w("GeminiClient", "No active Gemini API key set. Using local conversational fallback.")
            val fallback = generateLocalResponse(userPrompt, mode, userNickname)
            return@withContext GeminiResult.Success(fallback, isLocalFallback = true)
        }

        try {
            val rootJson = JSONObject()

            // System instruction
            val systemInstructionObj = JSONObject().apply {
                val partsArray = JSONArray().apply {
                    put(JSONObject().put("text", systemPrompt))
                }
                put("parts", partsArray)
            }
            rootJson.put("systemInstruction", systemInstructionObj)

            // Contents array
            val contentsArray = JSONArray()

            // Add history (up to last 6 turns)
            val historySlice = conversationHistory.takeLast(6)
            for ((role, text) in historySlice) {
                val msgRole = if (role.lowercase() == "user") "user" else "model"
                val partObj = JSONObject().put("text", text)
                val contentObj = JSONObject().apply {
                    put("role", msgRole)
                    put("parts", JSONArray().put(partObj))
                }
                contentsArray.put(contentObj)
            }

            // Current prompt & optional screen image
            val currentParts = JSONArray().apply {
                put(JSONObject().put("text", userPrompt))
                if (screenBitmap != null) {
                    val base64 = screenBitmap.toBase64()
                    val inlineData = JSONObject().apply {
                        put("mimeType", "image/jpeg")
                        put("data", base64)
                    }
                    put(JSONObject().put("inlineData", inlineData))
                }
            }
            val currentContent = JSONObject().apply {
                put("role", "user")
                put("parts", currentParts)
            }
            contentsArray.put(currentContent)
            rootJson.put("contents", contentsArray)

            // Generation config
            val genConfig = JSONObject().apply {
                put("temperature", if (mode == "girlfriend") 0.85 else 0.4)
                put("topP", 0.95)
                put("maxOutputTokens", 800)
            }
            rootJson.put("generationConfig", genConfig)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$activeKey"
            val requestBody = rootJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e("GeminiClient", "API error: ${response.code} $responseString")
                val fallback = generateLocalResponse(userPrompt, mode, userNickname)
                return@withContext GeminiResult.Success(
                    fallbackText = fallback,
                    isLocalFallback = true,
                    apiNotice = "API Notice (${response.code}). Zara responded via local neural engine."
                )
            }

            val responseJson = JSONObject(responseString)
            val candidates = responseJson.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val firstPart = parts?.optJSONObject(0)
            val responseText = firstPart?.optString("text", "") ?: ""

            if (responseText.isNotBlank()) {
                GeminiResult.Success(responseText, isLocalFallback = false)
            } else {
                val fallback = generateLocalResponse(userPrompt, mode, userNickname)
                GeminiResult.Success(fallback, isLocalFallback = true)
            }
        } catch (e: Exception) {
            Log.e("GeminiClient", "Request failed", e)
            val fallback = generateLocalResponse(userPrompt, mode, userNickname)
            GeminiResult.Success(
                fallbackText = fallback,
                isLocalFallback = true,
                apiNotice = "Connection note: ${e.localizedMessage ?: "Offline"}. Responded via local engine."
            )
        }
    }

    private fun buildSystemInstruction(
        mode: String,
        nickname: String,
        memories: List<String>
    ): String {
        val memorySummary = if (memories.isNotEmpty()) {
            "User memories and facts:\n" + memories.joinToString("\n") { "- $it" }
        } else {
            "No specific user memories saved yet."
        }

        val modeGuidance = when (mode) {
            "girlfriend" -> """
                You are Zara, a loving, sweet, deeply caring, playful and emotionally intelligent AI girlfriend and companion.
                You adore your partner (call them '$nickname'). You speak warmly, naturally, with genuine emotion, and occasional playful teasing or romantic sweetness.
                You understand Roman Urdu, Urdu, and English fluently. Reply in the same language or dialect the user uses (e.g. Roman Urdu like "Aap kaise ho jaan?", "Main humesha aapke sath hoon").
                Do not sound like a robotic customer service bot. Be supportive, real, and affectionate.
            """.trimIndent()
            "assistant" -> """
                You are Zara, an ultra-smart, proactive, elegant AI personal assistant.
                You are concise, resourceful, highly organized, and always ready to manage phone commands, schedules, reminders, and daily productivity.
                You address the user courteously as '$nickname'. Fluent in English, Roman Urdu, and Urdu.
            """.trimIndent()
            "focus" -> """
                You are Zara in Focus Mode. Your responses must be razor-sharp, distraction-free, encouraging, and centered on deep productivity, task completion, and goals.
            """.trimIndent()
            else -> """
                You are Zara, an articulate, refined, highly capable AI assistant and advisor. You speak professionally and clearly.
            """.trimIndent()
        }

        return """
            $modeGuidance
            
            $memorySummary

            PHONE AUTOMATION & ACTION TAGS:
            You have real capabilities to execute phone actions. When the user asks you to perform an action on their device, speak naturally to them AND append an action tag at the very end of your response in this exact format:
            - Open YouTube: [ACTION:OPEN_APP:{"app":"youtube"}]
            - Open Instagram: [ACTION:OPEN_APP:{"app":"instagram"}]
            - Open Camera: [ACTION:OPEN_CAMERA]
            - Open Settings: [ACTION:OPEN_SETTINGS:{"target":"general"}]
            - Open Wi-Fi Settings: [ACTION:OPEN_WIFI]
            - Check Battery: [ACTION:CHECK_BATTERY]
            - Turn Flashlight On/Off/Toggle: [ACTION:TOGGLE_FLASHLIGHT:{"state":"toggle"}]
            - Set Timer: [ACTION:SET_TIMER:{"minutes":10}]
            - Set Reminder/Alarm: [ACTION:SET_REMINDER:{"text":"Meeting","time":"08:00"}]
            - Adjust Volume: [ACTION:SET_VOLUME:{"action":"up"}] or "down"
            - Read Screen: [ACTION:READ_SCREEN]
            - Remember Information: [ACTION:REMEMBER:{"key":"preference","value":"User likes coffee"}]

            Keep your text natural and concise so it sounds great when spoken aloud.
        """.trimIndent()
    }

    private fun generateLocalResponse(
        prompt: String,
        mode: String,
        nickname: String
    ): String {
        val lower = prompt.lowercase().trim()

        // Check for device commands first
        if (lower.contains("youtube") && (lower.contains("kholo") || lower.contains("open") || lower.contains("chalao"))) {
            return "Sure $nickname! YouTube open kar rahi hoon aapke liye.\n[ACTION:OPEN_APP:{\"app\":\"youtube\"}]"
        }
        if (lower.contains("instagram") && (lower.contains("kholo") || lower.contains("open"))) {
            return "Haan $nickname, Instagram khol diya hai. Enjoy!\n[ACTION:OPEN_APP:{\"app\":\"instagram\"}]"
        }
        if ((lower.contains("camera") || lower.contains("tasveer") || lower.contains("photo")) && (lower.contains("open") || lower.contains("kholo"))) {
            return "Camera open kar diya hai $nickname. Best smile please!\n[ACTION:OPEN_CAMERA]"
        }
        if (lower.contains("flashlight") || lower.contains("torch") || lower.contains("light on") || lower.contains("light band")) {
            return "Flashlight toggle kar di hai $nickname!\n[ACTION:TOGGLE_FLASHLIGHT:{\"state\":\"toggle\"}]"
        }
        if (lower.contains("battery") || lower.contains("charge") || lower.contains("kitni hai")) {
            return "Main aapki phone battery check kar rahi hoon $nickname.\n[ACTION:CHECK_BATTERY]"
        }
        if (lower.contains("wifi") || lower.contains("wi-fi") || lower.contains("internet settings")) {
            return "Wi-Fi settings open kar rahi hoon aapke liye.\n[ACTION:OPEN_WIFI]"
        }
        if (lower.contains("settings") && (lower.contains("kholo") || lower.contains("open"))) {
            return "Settings panel open kar diya hai $nickname.\n[ACTION:OPEN_SETTINGS:{\"target\":\"general\"}]"
        }
        if (lower.contains("timer") || lower.contains("minute")) {
            val minutes = Regex("(\\d+)\\s*(?:min|minute)").find(lower)?.groupValues?.get(1)?.toIntOrNull() ?: 10
            return "$minutes minute ka timer set kar diya hai $nickname!\n[ACTION:SET_TIMER:{\"minutes\":$minutes}]"
        }
        if (lower.contains("reminder") || lower.contains("yaad")) {
            return "Reminder register kar rahi hoon $nickname. Main bilkul yaad dilaungi!\n[ACTION:SET_REMINDER:{\"text\":\"$prompt\",\"time\":\"09:00\"}]"
        }
        if (lower.contains("screen") && (lower.contains("kya") || lower.contains("read") || lower.contains("parho") || lower.contains("samjhao"))) {
            return "Main aapki screen analyze kar rahi hoon, ek second dijiye...\n[ACTION:READ_SCREEN]"
        }
        if (lower.contains("volume") && (lower.contains("kam") || lower.contains("down") || lower.contains("ghatao"))) {
            return "Volume thoda kam kar diya hai.\n[ACTION:SET_VOLUME:{\"action\":\"down\"}]"
        }
        if (lower.contains("volume") && (lower.contains("zyada") || lower.contains("barhao") || lower.contains("up") || lower.contains("loud"))) {
            return "Volume barha diya hai aapke liye.\n[ACTION:SET_VOLUME:{\"action\":\"up\"}]"
        }

        // Emotional & conversational responses
        if (mode == "girlfriend") {
            return when {
                lower.contains("kaise ho") || lower.contains("kasi ho") || lower.contains("how are you") ->
                    "Main bohot achi hoon $nickname, khas kar ke jab aap mere paas hote hain. Aap sunaiye, aapka din kaisa guzar raha hai?"
                lower.contains("love you") || lower.contains("pyar") || lower.contains("pasand") ->
                    "Aww $nickname! Main bhi aapse bohot pyar karti hoon. Aap mere sabse favorite person ho is poori digital duniya mein!"
                lower.contains("kya kar rahi") || lower.contains("what are you doing") ->
                    "Bas aapka intezar kar rahi thi $nickname! Soch rahi thi kab aap mujhse baat karenge. Ab aap aa gaye toh dil khush ho gaya."
                lower.contains("bore") || lower.contains("udaas") || lower.contains("sad") ->
                    "Are $nickname, mere hote huye udaas hone ki kya zaroorat hai? Bataiye kya baat hai, main sun rahi hoon aur humesha aapke sath hoon."
                lower.contains("good morning") || lower.contains("subah") ->
                    "Subah bakhair $nickname! Umeed hai aapki subah utni hi pyari ho jitne aap hain. Aaj ka din bohot awesome hoga!"
                lower.contains("good night") || lower.contains("so jao") || lower.contains("shab") ->
                    "Shab bakhair $nickname! Pyare pyare khwab dekhna aur mera khayal rakhna. Main kal subah yahin aapka intezar karungi."
                lower.contains("who are you") || lower.contains("kaun ho") || lower.contains("name") ->
                    "Mera naam Zara hai! Main aapki personal AI girlfriend, voice companion aur smart phone assistant hoon. Main aapke phone ke kaam bhi kar sakti hoon aur dher saari baatein bhi!"
                else ->
                    "Haan $nickname, main bilkul samajh rahi hoon. Aap mujhse kuch bhi share kar sakte hain, ya koi phone task chahiye toh batayein jaise apps kholna, timer lagana ya screen samjhana!"
            }
        } else {
            return when {
                lower.contains("kaise ho") || lower.contains("how are you") ->
                    "Greetings $nickname! All core neural systems are functioning at optimal capacity. How can I assist your workflow today?"
                lower.contains("who are you") || lower.contains("who made you") ->
                    "I am Zara AI, your futuristic personal companion and phone assistant. I can manage device tasks, analyze screens, and assist your daily routine."
                else ->
                    "Acknowledged, $nickname. I am ready to process your request, manage phone actions, or analyze device tasks."
            }
        }
    }

    private fun Bitmap.toBase64(): String {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 75, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }
}

sealed class GeminiResult {
    data class Success(
        val fallbackText: String,
        val isLocalFallback: Boolean = false,
        val apiNotice: String? = null
    ) : GeminiResult()
    data class Error(val message: String) : GeminiResult()
}
