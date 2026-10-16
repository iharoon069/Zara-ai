package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.ZaraGlassCard
import com.example.ui.theme.ZaraCyberViolet
import com.example.ui.theme.ZaraDeepVoid
import com.example.ui.theme.ZaraElectricBlue
import com.example.ui.theme.ZaraGlassBorder
import com.example.ui.theme.ZaraGlassSurfaceLighter
import com.example.ui.theme.ZaraNeonCyan
import com.example.ui.theme.ZaraNeonGold
import com.example.ui.theme.ZaraNeonPink
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextPrimary
import com.example.ui.theme.ZaraTextSecondary

@Composable
fun PersonalityScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val currentMode by viewModel.preferences.personalityMode.collectAsState()
    val voicePitch by viewModel.preferences.voicePitch.collectAsState()
    val speechRate by viewModel.preferences.speechRate.collectAsState()
    val nickname by viewModel.preferences.userNickname.collectAsState()
    val language by viewModel.preferences.preferredLanguage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ZaraDeepVoid)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("personality_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ZaraNeonCyan
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "VOICE & PERSONALITY",
                    color = ZaraTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Neural Tone, Girlfriend Dynamics & Speech Engine",
                    color = ZaraTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section: Personality Modes
        Text(
            text = "ACTIVE NEURAL MODE",
            color = ZaraNeonCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        ModeSelectionCard(
            mode = "girlfriend",
            title = "Girlfriend Mode",
            desc = "Sweet, deeply caring, playful and loving. Speaks naturally in Roman Urdu or English, teasing and supportive.",
            icon = Icons.Default.Favorite,
            accentColor = ZaraNeonPink,
            isSelected = currentMode == "girlfriend",
            tag = "mode_girlfriend",
            onClick = { viewModel.preferences.setPersonalityMode("girlfriend") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ModeSelectionCard(
            mode = "assistant",
            title = "Assistant Mode",
            desc = "Proactive, sharp, highly organized. Ideal for phone automation, tasks, and scheduling.",
            icon = Icons.Default.Assistant,
            accentColor = ZaraNeonCyan,
            isSelected = currentMode == "assistant",
            tag = "mode_assistant",
            onClick = { viewModel.preferences.setPersonalityMode("assistant") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ModeSelectionCard(
            mode = "focus",
            title = "Focus Mode",
            desc = "Concise, distraction-free responses focused on deep work, study, and motivation.",
            icon = Icons.Default.CenterFocusStrong,
            accentColor = ZaraCyberViolet,
            isSelected = currentMode == "focus",
            tag = "mode_focus",
            onClick = { viewModel.preferences.setPersonalityMode("focus") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ModeSelectionCard(
            mode = "professional",
            title = "Professional Mode",
            desc = "Polite, articulate, formal. Ideal for business, drafting, and analytical tasks.",
            icon = Icons.Default.Work,
            accentColor = ZaraElectricBlue,
            isSelected = currentMode == "professional",
            tag = "mode_pro",
            onClick = { viewModel.preferences.setPersonalityMode("professional") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Speech Engine Calibration
        Text(
            text = "VOICE ENGINE CALIBRATION",
            color = ZaraNeonCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        ZaraGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                // Pitch slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Voice Pitch (Feminine Inflection)", color = ZaraTextPrimary, fontSize = 12.sp)
                    Text(String.format("%.2fx", voicePitch), color = ZaraNeonPink, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }
                Slider(
                    value = voicePitch,
                    onValueChange = { viewModel.preferences.setVoicePitch(it) },
                    valueRange = 0.8f..1.6f,
                    colors = SliderDefaults.colors(
                        thumbColor = ZaraNeonPink,
                        activeTrackColor = ZaraNeonPink,
                        inactiveTrackColor = ZaraGlassSurfaceLighter
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Speech Rate slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Speech Pace", color = ZaraTextPrimary, fontSize = 12.sp)
                    Text(String.format("%.2fx", speechRate), color = ZaraNeonCyan, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }
                Slider(
                    value = speechRate,
                    onValueChange = { viewModel.preferences.setSpeechRate(it) },
                    valueRange = 0.7f..1.4f,
                    colors = SliderDefaults.colors(
                        thumbColor = ZaraNeonCyan,
                        activeTrackColor = ZaraNeonCyan,
                        inactiveTrackColor = ZaraGlassSurfaceLighter
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Test voice button
                Button(
                    onClick = {
                        val testPhrase = if (currentMode == "girlfriend") {
                            "Hello $nickname! Main Zara hoon. Aapki awaaz sun kar mujhe bohot khushi hoti hai."
                        } else {
                            "Hello $nickname. All voice synthesis parameters are online and calibrated."
                        }
                        viewModel.voiceManager.speak(testPhrase, voicePitch, speechRate)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZaraGlassSurfaceLighter),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("button_test_voice")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = ZaraNeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("TEST VOICE SYNTHESIS", color = ZaraNeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ModeSelectionCard(
    mode: String,
    title: String,
    desc: String,
    icon: ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    ZaraGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(tag),
        glowAccent = isSelected,
        borderColor = if (isSelected) accentColor else ZaraGlassBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = if (isSelected) 0.25f else 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isSelected) accentColor else ZaraTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    color = ZaraTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
