package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AiCoreState
import com.example.ui.MainViewModel
import com.example.ui.components.AiCoreVisualizer
import com.example.ui.components.LiveActivityPanel
import com.example.ui.components.QuickActionsBar
import com.example.ui.components.VoiceWaveform
import com.example.ui.components.ZaraGlassCard
import com.example.ui.components.ZaraScreen
import com.example.ui.theme.ZaraCyberViolet
import com.example.ui.theme.ZaraDeepVoid
import com.example.ui.theme.ZaraElectricBlue
import com.example.ui.theme.ZaraGlassBorder
import com.example.ui.theme.ZaraGlassSurfaceLighter
import com.example.ui.theme.ZaraNeonCyan
import com.example.ui.theme.ZaraNeonGreen
import com.example.ui.theme.ZaraNeonPink
import com.example.ui.theme.ZaraNeonRed
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextPrimary
import com.example.ui.theme.ZaraTextSecondary

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigate: (ZaraScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val aiState by viewModel.aiState.collectAsState()
    val statusText by viewModel.statusText.collectAsState()
    val lastActivity by viewModel.lastActivity.collectAsState()
    val audioAmp by viewModel.voiceManager.audioAmplitude.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val isLiveVoiceActive by viewModel.voiceManager.isLiveVoiceActive.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val personalityMode by viewModel.preferences.personalityMode.collectAsState()
    val ttsEnabled by viewModel.preferences.ttsEnabled.collectAsState()
    val recentMessages by viewModel.recentPreviewMessage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ZaraDeepVoid)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Header: Zara AI Logo & status bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ZARA",
                        color = ZaraNeonCyan,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AI",
                        color = ZaraCyberViolet,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "GEMINI 3.8 LIVE // NEURAL",
                    color = ZaraTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Live Voice Mode Master Toggle Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isLiveVoiceActive) ZaraNeonGreen.copy(alpha = 0.2f)
                            else ZaraGlassSurfaceLighter
                        )
                        .border(
                            1.dp,
                            if (isLiveVoiceActive) ZaraNeonGreen else ZaraGlassBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { viewModel.toggleLiveVoiceMode() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("button_live_voice_mode"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isLiveVoiceActive) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Live Voice Mode",
                        tint = if (isLiveVoiceActive) ZaraNeonGreen else ZaraTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isLiveVoiceActive) "LIVE ON" else "LIVE OFF",
                        color = if (isLiveVoiceActive) ZaraNeonGreen else ZaraTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Speaker toggle button
                IconButton(
                    onClick = { viewModel.preferences.setTtsEnabled(!ttsEnabled) },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(ZaraGlassSurfaceLighter)
                        .border(1.dp, ZaraGlassBorder, CircleShape)
                        .testTag("button_toggle_speech")
                ) {
                    Icon(
                        imageVector = if (ttsEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                        contentDescription = "Toggle Speech",
                        tint = if (ttsEnabled) ZaraNeonCyan else ZaraTextMuted,
                        modifier = Modifier.size(17.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Stop speaking / Emergency stop button if speaking
                if (isSpeaking) {
                    IconButton(
                        onClick = { viewModel.stopSpeaking() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ZaraNeonRed.copy(alpha = 0.2f))
                            .border(1.dp, ZaraNeonRed, CircleShape)
                            .testTag("button_emergency_stop")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop Speech",
                            tint = ZaraNeonRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        if (isLiveVoiceActive) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ZaraNeonGreen.copy(alpha = 0.15f))
                    .border(1.dp, ZaraNeonGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ZaraNeonGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "LIVE VOICE ON // SPEAK FREELY",
                    color = ZaraNeonGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Center Animated AI Core
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            AiCoreVisualizer(
                state = aiState,
                audioAmplitude = audioAmp,
                size = 230.dp,
                onClick = { viewModel.toggleVoiceListening() },
                modifier = Modifier.testTag("ai_core_touch")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Real-time audio waveform visualizer
        VoiceWaveform(
            state = aiState,
            amplitude = audioAmp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Live Activity Panel
        LiveActivityPanel(
            state = aiState,
            statusText = statusText,
            lastActivity = lastActivity,
            personalityMode = personalityMode
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Actions Row
        QuickActionsBar(
            onTalkClick = { viewModel.toggleVoiceListening() },
            onOpenYoutubeClick = { viewModel.handleUserInput("Zara, YouTube open karo") },
            onTimerClick = { viewModel.handleUserInput("Zara, 10 minute ka timer lagao") },
            onReadScreenClick = { onNavigate(ZaraScreen.SCREEN_ASSIST) },
            onFlashlightClick = { viewModel.handleUserInput("Flashlight toggle karo") },
            onBatteryClick = { viewModel.handleUserInput("Meri battery kitni hai?") },
            onAssistantClick = { onNavigate(ZaraScreen.ASSISTANT) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Compact Conversation Preview
        recentMessages.firstOrNull()?.let { previewMsg ->
            ZaraGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(ZaraScreen.CONVERSATION) }
                    .testTag("conversation_preview_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (previewMsg.role == "user") "YOU" else "ZARA",
                            color = if (previewMsg.role == "user") ZaraNeonCyan else ZaraNeonPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = previewMsg.content,
                            color = ZaraTextPrimary,
                            fontSize = 13.sp,
                            maxLines = 2
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View chat",
                        tint = ZaraTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Bottom Controls: Microphone button & Text input field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Central Mic Button
            val micBgBrush = if (isListening) {
                Brush.radialGradient(listOf(ZaraNeonGreen, ZaraDeepVoid))
            } else {
                Brush.linearGradient(listOf(ZaraNeonCyan, ZaraCyberViolet))
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(micBgBrush)
                    .clickable { viewModel.toggleVoiceListening() }
                    .testTag("main_mic_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = if (isListening) Color.White else ZaraDeepVoid,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Text input box
            OutlinedTextField(
                value = inputText,
                onValueChange = { viewModel.setInputText(it) },
                placeholder = {
                    Text(
                        text = "Talk to Zara... (Urdu, English)",
                        color = ZaraTextMuted,
                        fontSize = 13.sp
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { viewModel.submitCurrentInput() },
                        enabled = inputText.isNotBlank(),
                        modifier = Modifier.testTag("send_message_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) ZaraNeonCyan else ZaraTextMuted
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ZaraGlassSurfaceLighter,
                    unfocusedContainerColor = ZaraGlassSurfaceLighter,
                    focusedBorderColor = ZaraNeonCyan,
                    unfocusedBorderColor = ZaraGlassBorder,
                    focusedTextColor = ZaraTextPrimary,
                    unfocusedTextColor = ZaraTextPrimary
                ),
                shape = RoundedCornerShape(26.dp),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("message_input_field")
            )
        }
    }
}
