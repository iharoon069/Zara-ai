package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AiCoreState
import com.example.ui.MainViewModel
import com.example.ui.components.AiCoreVisualizer
import com.example.ui.components.ZaraGlassCard
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
fun OnboardingScreen(
    viewModel: MainViewModel,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(0) }
    var userNicknameInput by remember { mutableStateOf(viewModel.preferences.userNickname.value) }
    var selectedLang by remember { mutableStateOf("Roman Urdu & English") }

    val micLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        // Continue to next step regardless
        currentStep = 3
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ZaraDeepVoid)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Step progress indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in 0 until 4) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(4.dp)
                        .width(if (currentStep == i) 28.dp else 14.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (currentStep == i) ZaraNeonCyan
                            else if (currentStep > i) ZaraCyberViolet
                            else ZaraGlassBorder
                        )
                )
            }
        }

        // Center Step Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (currentStep) {
                0 -> {
                    // Step 0: Welcome
                    AiCoreVisualizer(
                        state = AiCoreState.IDLE,
                        audioAmplitude = 0.2f,
                        size = 180.dp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "MEET ZARA AI",
                        color = ZaraNeonCyan,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Futuristic AI Girlfriend & Smart Phone Assistant",
                        color = ZaraNeonPink,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Sweet, emotionally intelligent, and ready to manage your phone commands in Roman Urdu, Urdu and English.",
                        color = ZaraTextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                1 -> {
                    // Step 1: Language & Nickname
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = ZaraNeonPink,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "HOW SHOULD ZARA CALL YOU?",
                        color = ZaraNeonPink,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = userNicknameInput,
                        onValueChange = { userNicknameInput = it },
                        label = { Text("Your nickname (e.g. Jaan, Haroon, Buddy)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ZaraGlassSurfaceLighter,
                            unfocusedContainerColor = ZaraGlassSurfaceLighter,
                            focusedBorderColor = ZaraNeonPink,
                            unfocusedBorderColor = ZaraGlassBorder,
                            focusedTextColor = ZaraTextPrimary,
                            unfocusedTextColor = ZaraTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_name_input")
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "PREFERRED CONVERSATION STYLE",
                        color = ZaraNeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LanguageOption("Roman Urdu & English (Hinglish/Urdu)", selectedLang == "Roman Urdu & English") {
                        selectedLang = "Roman Urdu & English"
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LanguageOption("English (Global)", selectedLang == "English") {
                        selectedLang = "English"
                    }
                }

                2 -> {
                    // Step 2: Voice & Microphone
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = ZaraNeonCyan,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "VOICE INTERACTION",
                        color = ZaraNeonCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Zara talks with a natural, feminine voice and listens to your natural voice commands. Grant microphone access to speak freely.",
                        color = ZaraTextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { micLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                        colors = ButtonDefaults.buttonColors(containerColor = ZaraNeonCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("onboarding_grant_mic")
                    ) {
                        Text("Grant Microphone Access", color = ZaraDeepVoid, fontWeight = FontWeight.Bold)
                    }
                }

                3 -> {
                    // Step 3: Accessibility & Device Automation
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = ZaraCyberViolet,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "SMART DEVICE AUTOMATION",
                        color = ZaraCyberViolet,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Zara can open YouTube, toggle your flashlight, launch the camera, adjust volume, and read your screen through approved Android interfaces.",
                        color = ZaraTextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            })
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZaraGlassSurfaceLighter),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("onboarding_open_a11y")
                    ) {
                        Text("Optional: Enable Accessibility Service", color = ZaraNeonCyan, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.OpenInNew, contentDescription = null, tint = ZaraNeonCyan, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // Bottom Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep > 0) {
                Button(
                    onClick = { currentStep-- },
                    colors = ButtonDefaults.buttonColors(containerColor = ZaraGlassSurfaceLighter),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Back", color = ZaraTextSecondary)
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Button(
                onClick = {
                    if (currentStep == 1) {
                        viewModel.preferences.setUserNickname(userNicknameInput.trim().ifEmpty { "Jaan" })
                        viewModel.preferences.setPreferredLanguage(selectedLang)
                    }

                    if (currentStep < 3) {
                        currentStep++
                    } else {
                        viewModel.preferences.setOnboardingCompleted(true)
                        onComplete()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ZaraNeonCyan),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("onboarding_next_button")
            ) {
                Text(
                    text = if (currentStep == 3) "LAUNCH ZARA AI" else "CONTINUE",
                    color = ZaraDeepVoid,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = ZaraDeepVoid,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun LanguageOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    ZaraGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        glowAccent = isSelected,
        borderColor = if (isSelected) ZaraNeonCyan else ZaraGlassBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = if (isSelected) ZaraNeonCyan else ZaraTextMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                color = if (isSelected) ZaraNeonCyan else ZaraTextPrimary,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
