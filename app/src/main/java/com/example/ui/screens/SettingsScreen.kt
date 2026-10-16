package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.ZaraAssistantService
import com.example.ui.MainViewModel
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
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextPrimary
import com.example.ui.theme.ZaraTextSecondary

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigate: (ZaraScreen) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    val ttsEnabled by viewModel.preferences.ttsEnabled.collectAsState()
    val hapticsEnabled by viewModel.preferences.hapticsEnabled.collectAsState()
    val customKey by viewModel.preferences.customApiKey.collectAsState()
    var keyInput by remember { mutableStateOf(customKey) }
    var keySavedMsg by remember { mutableStateOf<String?>(null) }
    var bgServiceEnabled by remember { mutableStateOf(false) }

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
            IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ZaraNeonCyan
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "NEURAL SETTINGS & HUB",
                    color = ZaraTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "AI Configuration, Hub Navigation & System State",
                    color = ZaraTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section: Hub Navigation
        Text(
            text = "SYSTEM MODULES",
            color = ZaraNeonCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        SettingsNavRow("Voice & Personality", "Configure girlfriend mode, pitch & speed", Icons.Default.Favorite, ZaraNeonPink) {
            onNavigate(ZaraScreen.PERSONALITY)
        }
        Spacer(modifier = Modifier.height(8.dp))

        SettingsNavRow("Phone Assistant", "Hardware controls, app launchers, timers", Icons.Default.PhoneAndroid, ZaraNeonCyan) {
            onNavigate(ZaraScreen.ASSISTANT)
        }
        Spacer(modifier = Modifier.height(8.dp))

        SettingsNavRow("Screen Understanding", "Real-time UI analysis & accessibility guidance", Icons.Default.Visibility, ZaraCyberViolet) {
            onNavigate(ZaraScreen.SCREEN_ASSIST)
        }
        Spacer(modifier = Modifier.height(8.dp))

        SettingsNavRow("Tasks & Automation Logs", "Inspect past command executions & results", Icons.Default.Engineering, ZaraElectricBlue) {
            onNavigate(ZaraScreen.TASKS)
        }
        Spacer(modifier = Modifier.height(8.dp))

        SettingsNavRow("Neural Memory & Callbacks", "Manage personal facts, preferences, nickname", Icons.Default.Memory, ZaraNeonCyan) {
            onNavigate(ZaraScreen.MEMORY)
        }
        Spacer(modifier = Modifier.height(8.dp))

        SettingsNavRow("Permissions & Privacy", "Microphone, accessibility, data purge", Icons.Default.Security, ZaraNeonGreen) {
            onNavigate(ZaraScreen.PERMISSIONS)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Gemini API Key Setting
        Text(
            text = "GEMINI AI ENGINE CONFIGURATION",
            color = ZaraNeonCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        ZaraGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = ZaraNeonCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gemini API Key Override",
                        color = ZaraTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Zara is pre-connected to Google Gemini API via server environment. You can also paste your own custom Gemini key here.",
                    color = ZaraTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    placeholder = { Text("AIzaSy...", color = ZaraTextMuted, fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ZaraGlassSurfaceLighter,
                        unfocusedContainerColor = ZaraGlassSurfaceLighter,
                        focusedBorderColor = ZaraNeonCyan,
                        unfocusedBorderColor = ZaraGlassBorder,
                        focusedTextColor = ZaraTextPrimary,
                        unfocusedTextColor = ZaraTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_custom_api_key")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = keySavedMsg ?: "",
                        color = ZaraNeonGreen,
                        fontSize = 11.sp
                    )

                    Button(
                        onClick = {
                            viewModel.preferences.setCustomApiKey(keyInput.trim())
                            keySavedMsg = "Key saved successfully!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZaraNeonCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("button_save_api_key")
                    ) {
                        Text("Save Key", color = ZaraDeepVoid, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section: System Toggles
        Text(
            text = "SENSORY & BACKGROUND CONTROLS",
            color = ZaraNeonCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        ZaraGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                // Speech output toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = ZaraNeonPink, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Voice Speech Synthesis", color = ZaraTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("Zara speaks replies aloud", color = ZaraTextSecondary, fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = ttsEnabled,
                        onCheckedChange = { viewModel.preferences.setTtsEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ZaraNeonPink,
                            checkedTrackColor = ZaraNeonPink.copy(alpha = 0.35f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Haptics toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Vibration, contentDescription = null, tint = ZaraNeonCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Cybernetic Haptics", color = ZaraTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("Haptic clicks on voice triggers", color = ZaraTextSecondary, fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = { viewModel.preferences.setHapticsEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ZaraNeonCyan,
                            checkedTrackColor = ZaraNeonCyan.copy(alpha = 0.35f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Background Service toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = ZaraCyberViolet, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Foreground Assistant Service", color = ZaraTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("Persistent standby notification", color = ZaraTextSecondary, fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = bgServiceEnabled,
                        onCheckedChange = { enabled ->
                            bgServiceEnabled = enabled
                            if (enabled) {
                                ZaraAssistantService.start(context)
                            } else {
                                ZaraAssistantService.stop(context)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ZaraCyberViolet,
                            checkedTrackColor = ZaraCyberViolet.copy(alpha = 0.35f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // About Zara AI
        ZaraGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = ZaraNeonCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ABOUT ZARA AI",
                        color = ZaraNeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Zara AI is a futuristic personal companion, AI girlfriend, and phone assistant built using Kotlin, Jetpack Compose, Android Speech and Media APIs, and Google Gemini. Crafted to speak Roman Urdu, Urdu, and English naturally.",
                    color = ZaraTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Release Version 1.0.0 // AI Engine: Gemini 2.5 Flash",
                    color = ZaraTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))
    }
}

@Composable
private fun SettingsNavRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    ZaraGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        color = ZaraTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        color = ZaraTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = ZaraTextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
