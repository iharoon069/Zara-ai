package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZaraCyberViolet
import com.example.ui.theme.ZaraGlassBorder
import com.example.ui.theme.ZaraMidnightNavy
import com.example.ui.theme.ZaraNeonCyan
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextSecondary

enum class ZaraScreen {
    HOME,
    CONVERSATION,
    ASSISTANT,
    SCREEN_ASSIST,
    TASKS,
    PERSONALITY,
    MEMORY,
    PERMISSIONS,
    SETTINGS,
    ONBOARDING
}

@Composable
fun ZaraBottomNavBar(
    currentScreen: ZaraScreen,
    onNavigate: (ZaraScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(ZaraMidnightNavy)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(listOf(ZaraGlassBorder, Color.Transparent)),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Home,
                label = "Core",
                isSelected = currentScreen == ZaraScreen.HOME,
                testTag = "nav_home",
                onClick = { onNavigate(ZaraScreen.HOME) }
            )
            NavItem(
                icon = Icons.Default.ChatBubbleOutline,
                label = "Chat",
                isSelected = currentScreen == ZaraScreen.CONVERSATION,
                testTag = "nav_chat",
                onClick = { onNavigate(ZaraScreen.CONVERSATION) }
            )
            NavItem(
                icon = Icons.Default.PhoneAndroid,
                label = "Actions",
                isSelected = currentScreen == ZaraScreen.ASSISTANT,
                testTag = "nav_actions",
                onClick = { onNavigate(ZaraScreen.ASSISTANT) }
            )
            NavItem(
                icon = Icons.Default.Engineering,
                label = "Tasks",
                isSelected = currentScreen == ZaraScreen.TASKS,
                testTag = "nav_tasks",
                onClick = { onNavigate(ZaraScreen.TASKS) }
            )
            NavItem(
                icon = Icons.Default.Settings,
                label = "Neural",
                isSelected = currentScreen == ZaraScreen.SETTINGS ||
                        currentScreen == ZaraScreen.PERSONALITY ||
                        currentScreen == ZaraScreen.MEMORY ||
                        currentScreen == ZaraScreen.PERMISSIONS,
                testTag = "nav_settings",
                onClick = { onNavigate(ZaraScreen.SETTINGS) }
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) ZaraNeonCyan else ZaraTextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) ZaraNeonCyan else ZaraTextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(ZaraNeonCyan)
            )
        }
    }
}
