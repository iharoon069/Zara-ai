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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.device.ActionResult
import com.example.ui.MainViewModel
import com.example.ui.components.ZaraGlassCard
import com.example.ui.theme.ZaraCyberViolet
import com.example.ui.theme.ZaraDeepVoid
import com.example.ui.theme.ZaraElectricBlue
import com.example.ui.theme.ZaraGlassSurfaceLighter
import com.example.ui.theme.ZaraNeonCyan
import com.example.ui.theme.ZaraNeonGold
import com.example.ui.theme.ZaraNeonGreen
import com.example.ui.theme.ZaraNeonPink
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextPrimary
import com.example.ui.theme.ZaraTextSecondary

@Composable
fun PhoneAssistantScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var feedbackMessage by remember { mutableStateOf("Ready to trigger device automation.") }

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
            IconButton(onClick = onBack, modifier = Modifier.testTag("assistant_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ZaraNeonCyan
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "DEVICE ASSISTANT",
                    color = ZaraTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Approved System & Intent Automations",
                    color = ZaraTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Status banner
        ZaraGlassCard(
            modifier = Modifier.fillMaxWidth(),
            glowAccent = true
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ZaraNeonCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = ZaraNeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LAST SYSTEM RESULT",
                        color = ZaraNeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = feedbackMessage,
                        color = ZaraTextPrimary,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section 1: Hardware Telemetry & Controls
        SectionTitle(title = "HARDWARE TELEMETRY & CONTROLS")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Battery Status Card
            ZaraGlassCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        val res = viewModel.deviceActionManager.checkBattery()
                        if (res is ActionResult.Success) feedbackMessage = res.message
                    }
                    .testTag("card_battery")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = null,
                            tint = ZaraNeonGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = ZaraTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Battery Gauge",
                        color = ZaraTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Tap to query level",
                        color = ZaraTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Flashlight Toggle Card
            ZaraGlassCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        val res = viewModel.deviceActionManager.toggleFlashlight("toggle")
                        when (res) {
                            is ActionResult.Success -> feedbackMessage = res.message
                            is ActionResult.Failure -> feedbackMessage = res.error
                        }
                    }
                    .testTag("card_flashlight")
            ) {
                Column {
                    Icon(
                        imageVector = Icons.Default.FlashlightOn,
                        contentDescription = null,
                        tint = ZaraNeonGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Flashlight",
                        color = ZaraTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Toggle torch on/off",
                        color = ZaraTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Volume Controls
        ZaraGlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Audio Volume:",
                    color = ZaraTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val res = viewModel.deviceActionManager.adjustVolume("down")
                            if (res is ActionResult.Success) feedbackMessage = res.message
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZaraGlassSurfaceLighter),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("button_vol_down")
                    ) {
                        Icon(Icons.Default.VolumeDown, contentDescription = "Down", tint = ZaraNeonCyan, modifier = Modifier.size(18.dp))
                    }
                    Button(
                        onClick = {
                            val res = viewModel.deviceActionManager.adjustVolume("up")
                            if (res is ActionResult.Success) feedbackMessage = res.message
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZaraGlassSurfaceLighter),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("button_vol_up")
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Up", tint = ZaraNeonCyan, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section 2: Application Launchers
        SectionTitle(title = "NATIVE APPLICATION LAUNCHERS")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AppLauncherButton(
                name = "YouTube",
                icon = Icons.Default.SmartDisplay,
                color = ZaraNeonPink,
                modifier = Modifier.weight(1f),
                tag = "launch_youtube",
                onClick = {
                    val res = viewModel.deviceActionManager.openApplication("youtube")
                    if (res is ActionResult.Success) feedbackMessage = res.message
                }
            )
            AppLauncherButton(
                name = "Camera",
                icon = Icons.Default.PhotoCamera,
                color = ZaraNeonCyan,
                modifier = Modifier.weight(1f),
                tag = "launch_camera",
                onClick = {
                    val res = viewModel.deviceActionManager.openCamera()
                    if (res is ActionResult.Success) feedbackMessage = res.message
                }
            )
            AppLauncherButton(
                name = "Instagram",
                icon = Icons.Default.CameraAlt,
                color = ZaraCyberViolet,
                modifier = Modifier.weight(1f),
                tag = "launch_instagram",
                onClick = {
                    val res = viewModel.deviceActionManager.openApplication("instagram")
                    if (res is ActionResult.Success) feedbackMessage = res.message
                }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section 3: Timers & Reminders
        SectionTitle(title = "TIMERS & ALERTS")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimerButton(minutes = 1, tag = "timer_1m") {
                val res = viewModel.deviceActionManager.setDeviceTimer(1)
                if (res is ActionResult.Success) feedbackMessage = res.message
            }
            TimerButton(minutes = 5, tag = "timer_5m") {
                val res = viewModel.deviceActionManager.setDeviceTimer(5)
                if (res is ActionResult.Success) feedbackMessage = res.message
            }
            TimerButton(minutes = 10, tag = "timer_10m") {
                val res = viewModel.deviceActionManager.setDeviceTimer(10)
                if (res is ActionResult.Success) feedbackMessage = res.message
            }
            TimerButton(minutes = 30, tag = "timer_30m") {
                val res = viewModel.deviceActionManager.setDeviceTimer(30)
                if (res is ActionResult.Success) feedbackMessage = res.message
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section 4: System Settings Hub
        SectionTitle(title = "SYSTEM SETTINGS HUB")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SystemSettingChip(
                name = "Wi-Fi Hub",
                icon = Icons.Default.Wifi,
                tag = "settings_wifi",
                modifier = Modifier.weight(1f),
                onClick = { viewModel.deviceActionManager.openSettings("wifi") }
            )
            SystemSettingChip(
                name = "Settings Main",
                icon = Icons.Default.Settings,
                tag = "settings_general",
                modifier = Modifier.weight(1f),
                onClick = { viewModel.deviceActionManager.openSettings("general") }
            )
            SystemSettingChip(
                name = "Accessibility",
                icon = Icons.Default.Language,
                tag = "settings_accessibility",
                modifier = Modifier.weight(1f),
                onClick = { viewModel.deviceActionManager.openSettings("accessibility") }
            )
        }

        Spacer(modifier = Modifier.height(26.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = ZaraNeonCyan,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun AppLauncherButton(
    name: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    tag: String,
    onClick: () -> Unit
) {
    ZaraGlassCard(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = name,
                color = ZaraTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TimerButton(
    minutes: Int,
    tag: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = ZaraGlassSurfaceLighter),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag(tag)
    ) {
        Icon(
            imageVector = Icons.Default.Alarm,
            contentDescription = null,
            tint = ZaraNeonGold,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${minutes}m",
            color = ZaraTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SystemSettingChip(
    name: String,
    icon: ImageVector,
    tag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ZaraGlassCard(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = ZaraNeonCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = name,
                color = ZaraTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
