package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.ZaraAccessibilityService
import com.example.ui.AiCoreState
import com.example.ui.MainViewModel
import com.example.ui.components.ZaraGlassCard
import com.example.ui.theme.ZaraCyberViolet
import com.example.ui.theme.ZaraDeepVoid
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
fun ScreenAssistScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val liveAnalysis by viewModel.liveScreenAnalysis.collectAsState()
    val aiState by viewModel.aiState.collectAsState()
    val isAccessibilityActive = ZaraAccessibilityService.isServiceActive

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
            IconButton(onClick = onBack, modifier = Modifier.testTag("screen_assist_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ZaraNeonCyan
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "SCREEN UNDERSTANDING",
                    color = ZaraTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "AI Vision & Active Window Inspection",
                    color = ZaraTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Accessibility Status Card
        ZaraGlassCard(
            modifier = Modifier.fillMaxWidth(),
            glowAccent = isAccessibilityActive
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isAccessibilityActive) ZaraNeonGreen else ZaraNeonRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAccessibilityActive) "ACCESSIBILITY SERVICE ACTIVE" else "ACCESSIBILITY OFF",
                            color = if (isAccessibilityActive) ZaraNeonGreen else ZaraNeonRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (!isAccessibilityActive) {
                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ZaraGlassSurfaceLighter),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("enable_accessibility_button")
                        ) {
                            Text("Enable", color = ZaraNeonCyan, fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = ZaraNeonCyan, modifier = Modifier.size(12.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isAccessibilityActive)
                        "Zara has permission to read active screen controls and assist your navigation in real-time."
                    else
                        "To let Zara read buttons, explain complex screens, and guide you, enable Zara AI under Accessibility Settings.",
                    color = ZaraTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Trigger Screen Analysis Button
        Button(
            onClick = { viewModel.analyzeActiveScreen() },
            colors = ButtonDefaults.buttonColors(containerColor = ZaraNeonCyan),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("button_analyze_screen")
        ) {
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = null,
                tint = ZaraDeepVoid,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (aiState == AiCoreState.THINKING) "READING SCREEN HIERARCHY..." else "ANALYZE ACTIVE SCREEN",
                color = ZaraDeepVoid,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Screen Telemetry Window
        ZaraGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVE WINDOW BUFFER",
                        color = ZaraNeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${ZaraAccessibilityService.currentInteractiveCount} elements",
                        color = ZaraTextMuted,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ZaraGlassSurfaceLighter)
                        .padding(10.dp)
                ) {
                    Text(
                        text = liveAnalysis ?: ZaraAccessibilityService.getScreenContext(),
                        color = ZaraTextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 8
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Inquiries
        Text(
            text = "SAMPLE SCREEN QUESTIONS",
            color = ZaraNeonCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        SampleQuestionChip("Zara, screen par kya likha hai?") {
            viewModel.handleUserInput("Zara, screen par kya likha hai?")
        }
        Spacer(modifier = Modifier.height(6.dp))
        SampleQuestionChip("Yahan mujhe kis button par click karna hai?") {
            viewModel.handleUserInput("Yahan mujhe kis button par click karna hai?")
        }
        Spacer(modifier = Modifier.height(6.dp))
        SampleQuestionChip("Is error ka matlab samjhao.") {
            viewModel.handleUserInput("Is error ka matlab samjhao.")
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Privacy & Zero-Knowledge Guarantee
        ZaraGlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = ZaraNeonGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Privacy Guarantee",
                        color = ZaraTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Screen content is analyzed on-demand only. Zara never logs passwords, banking info, or background screens without your explicit trigger.",
                        color = ZaraTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SampleQuestionChip(text: String, onClick: () -> Unit) {
    ZaraGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.QuestionAnswer,
                contentDescription = null,
                tint = ZaraCyberViolet,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = ZaraTextPrimary,
                fontSize = 13.sp
            )
        }
    }
}
