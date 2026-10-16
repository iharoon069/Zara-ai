package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.service.ZaraAccessibilityService
import com.example.ui.MainViewModel
import com.example.ui.components.ZaraGlassCard
import com.example.ui.theme.ZaraDeepVoid
import com.example.ui.theme.ZaraGlassSurface
import com.example.ui.theme.ZaraGlassSurfaceLighter
import com.example.ui.theme.ZaraNeonCyan
import com.example.ui.theme.ZaraNeonGreen
import com.example.ui.theme.ZaraNeonRed
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextPrimary
import com.example.ui.theme.ZaraTextSecondary

@Composable
fun PermissionsPrivacyScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val micLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
    }

    val notifLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasNotificationPermission = granted
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    var showPurgeDialog by remember { mutableStateOf(false) }

    if (showPurgeDialog) {
        AlertDialog(
            onDismissRequest = { showPurgeDialog = false },
            title = { Text("Purge All Neural Data?", color = ZaraTextPrimary) },
            text = { Text("This will permanently wipe all conversation logs, memory associations, and task history.", color = ZaraTextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllConversations()
                        viewModel.clearAllTasks()
                        showPurgeDialog = false
                    }
                ) {
                    Text("Purge Everything", color = ZaraNeonRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPurgeDialog = false }) {
                    Text("Cancel", color = ZaraTextSecondary)
                }
            },
            containerColor = ZaraGlassSurface
        )
    }

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
            IconButton(onClick = onBack, modifier = Modifier.testTag("privacy_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ZaraNeonCyan
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "PERMISSIONS & PRIVACY",
                    color = ZaraTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Zero-Trust Security & Hardware Authorization",
                    color = ZaraTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Emergency Stop Master Control
        ZaraGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = ZaraNeonRed.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "EMERGENCY STOP",
                        color = ZaraNeonRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Immediately terminates speech synthesis and active automation.",
                        color = ZaraTextSecondary,
                        fontSize = 11.sp
                    )
                }
                Button(
                    onClick = {
                        viewModel.stopSpeaking()
                        if (viewModel.voiceManager.isListening.value) {
                            viewModel.voiceManager.stopListening()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZaraNeonRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("button_emergency_kill")
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("HALT", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Permissions List
        Text(
            text = "HARDWARE & SYSTEM PRIVILEGES",
            color = ZaraNeonCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Microphone
        PermissionRow(
            title = "Microphone (Voice Assistant)",
            desc = "Required for speech-to-text recognition. Zara only listens when the mic button is explicitly triggered.",
            icon = Icons.Default.Mic,
            isGranted = hasMicPermission,
            tag = "perm_mic",
            onRequest = { micLauncher.launch(Manifest.permission.RECORD_AUDIO) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Notifications
        PermissionRow(
            title = "Notifications & Alerts",
            desc = "Needed to send timer alarms, scheduled reminders, and background assistant status notifications.",
            icon = Icons.Default.Notifications,
            isGranted = hasNotificationPermission,
            tag = "perm_notif",
            onRequest = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Camera (Torch)
        PermissionRow(
            title = "Camera (Flashlight & Photos)",
            desc = "Needed to toggle the physical device torch and launch camera captures upon voice command.",
            icon = Icons.Default.CameraAlt,
            isGranted = hasCameraPermission,
            tag = "perm_camera",
            onRequest = { cameraLauncher.launch(Manifest.permission.CAMERA) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Accessibility Service
        val isA11yActive = ZaraAccessibilityService.isServiceActive
        PermissionRow(
            title = "Accessibility Service (Automation)",
            desc = "Allows Zara to inspect active screen hierarchy and assist your navigation. Opt-in via Android Settings.",
            icon = Icons.Default.Security,
            isGranted = isA11yActive,
            tag = "perm_accessibility",
            onRequest = {
                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Data Management & Purge
        Text(
            text = "DATA SOVEREIGNTY & PURGE",
            color = ZaraNeonCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        ZaraGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "Local-First Architecture",
                    color = ZaraTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "All conversation logs and memories are stored on your local device. Zara never uploads private messages to third-party ad trackers.",
                    color = ZaraTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showPurgeDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ZaraGlassSurfaceLighter),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("button_purge_data")
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = ZaraNeonRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PURGE ALL LOCAL RECORDS", color = ZaraNeonRed, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))
    }
}

@Composable
private fun PermissionRow(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isGranted: Boolean,
    tag: String,
    onRequest: () -> Unit
) {
    ZaraGlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isGranted) ZaraNeonGreen else ZaraTextMuted,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = ZaraTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    color = ZaraTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (isGranted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Granted",
                    tint = ZaraNeonGreen,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Button(
                    onClick = onRequest,
                    colors = ButtonDefaults.buttonColors(containerColor = ZaraGlassSurfaceLighter),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag(tag)
                ) {
                    Text("Grant", color = ZaraNeonCyan, fontSize = 11.sp)
                }
            }
        }
    }
}
