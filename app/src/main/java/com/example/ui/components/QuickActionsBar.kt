package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZaraCyberViolet
import com.example.ui.theme.ZaraGlassBorder
import com.example.ui.theme.ZaraGlassSurfaceLighter
import com.example.ui.theme.ZaraNeonCyan
import com.example.ui.theme.ZaraNeonGold
import com.example.ui.theme.ZaraNeonGreen
import com.example.ui.theme.ZaraNeonPink
import com.example.ui.theme.ZaraTextPrimary

@Composable
fun QuickActionsBar(
    onTalkClick: () -> Unit,
    onOpenYoutubeClick: () -> Unit,
    onTimerClick: () -> Unit,
    onReadScreenClick: () -> Unit,
    onFlashlightClick: () -> Unit,
    onBatteryClick: () -> Unit,
    onAssistantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        QuickActionChip(
            label = "Talk to Zara",
            icon = Icons.Default.Mic,
            accentColor = ZaraNeonCyan,
            tag = "action_talk_zara",
            onClick = onTalkClick
        )
        QuickActionChip(
            label = "Open YouTube",
            icon = Icons.Default.SmartDisplay,
            accentColor = ZaraNeonPink,
            tag = "action_open_youtube",
            onClick = onOpenYoutubeClick
        )
        QuickActionChip(
            label = "Set Reminder",
            icon = Icons.Default.Alarm,
            accentColor = ZaraNeonGold,
            tag = "action_set_reminder",
            onClick = onTimerClick
        )
        QuickActionChip(
            label = "Read Screen",
            icon = Icons.Default.Visibility,
            accentColor = ZaraCyberViolet,
            tag = "action_read_screen",
            onClick = onReadScreenClick
        )
        QuickActionChip(
            label = "Flashlight",
            icon = Icons.Default.FlashlightOn,
            accentColor = ZaraNeonGreen,
            tag = "action_flashlight",
            onClick = onFlashlightClick
        )
        QuickActionChip(
            label = "Battery",
            icon = Icons.Default.BatteryChargingFull,
            accentColor = ZaraNeonCyan,
            tag = "action_battery",
            onClick = onBatteryClick
        )
        QuickActionChip(
            label = "Device Assistant",
            icon = Icons.Default.PhoneAndroid,
            accentColor = ZaraCyberViolet,
            tag = "action_device_assistant",
            onClick = onAssistantClick
        )
    }
}

@Composable
private fun QuickActionChip(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ZaraGlassSurfaceLighter,
        border = BorderStroke(1.dp, ZaraGlassBorder),
        modifier = Modifier
            .testTag(tag)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = ZaraTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
