package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ConversationMessage
import com.example.ui.MainViewModel
import com.example.ui.theme.ZaraCyberViolet
import com.example.ui.theme.ZaraDeepVoid
import com.example.ui.theme.ZaraElectricBlue
import com.example.ui.theme.ZaraGlassBorder
import com.example.ui.theme.ZaraGlassSurface
import com.example.ui.theme.ZaraGlassSurfaceLighter
import com.example.ui.theme.ZaraNeonCyan
import com.example.ui.theme.ZaraNeonGreen
import com.example.ui.theme.ZaraNeonPink
import com.example.ui.theme.ZaraNeonRed
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextPrimary
import com.example.ui.theme.ZaraTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConversationScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val listState = rememberLazyListState()
    var showClearDialog by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Conversation?", color = ZaraTextPrimary) },
            text = { Text("All message logs with Zara will be removed from memory.", color = ZaraTextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllConversations()
                        showClearDialog = false
                    }
                ) {
                    Text("Clear All", color = ZaraNeonRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
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
    ) {
        // Screen Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("chat_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ZaraNeonCyan
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "CONVERSATION LOGS",
                        color = ZaraTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${messages.size} neural exchanges recorded",
                        color = ZaraTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = { showClearDialog = true },
                modifier = Modifier.testTag("clear_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear History",
                    tint = ZaraTextMuted
                )
            }
        }

        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "NO MESSAGES YET",
                                color = ZaraTextMuted,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Speak or type to begin chatting with Zara.",
                                color = ZaraTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(messages, key = { it.id }) { msg ->
                    MessageBubble(
                        message = msg,
                        onSpeak = {
                            viewModel.voiceManager.speak(
                                msg.content,
                                viewModel.preferences.voicePitch.value,
                                viewModel.preferences.speechRate.value
                            )
                        }
                    )
                }
            }
        }

        // Bottom input row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ZaraGlassSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.toggleVoiceListening() },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isListening) ZaraNeonGreen else ZaraGlassSurfaceLighter)
                    .testTag("chat_mic_button")
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mic",
                    tint = if (isListening) ZaraDeepVoid else ZaraNeonCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = inputText,
                onValueChange = { viewModel.setInputText(it) },
                placeholder = { Text("Message Zara...", color = ZaraTextMuted, fontSize = 13.sp) },
                trailingIcon = {
                    IconButton(
                        onClick = { viewModel.submitCurrentInput() },
                        enabled = inputText.isNotBlank(),
                        modifier = Modifier.testTag("chat_send_button")
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
                shape = RoundedCornerShape(22.dp),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field")
            )
        }
    }
}

@Composable
private fun MessageBubble(
    message: ConversationMessage,
    onSpeak: () -> Unit
) {
    val isUser = message.role == "user"
    val timeFormatted = remember(message.timestamp) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date(message.timestamp))
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Label & time
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 3.dp, start = 4.dp, end = 4.dp)
        ) {
            Text(
                text = if (isUser) "YOU" else "ZARA",
                color = if (isUser) ZaraNeonCyan else ZaraNeonPink,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = timeFormatted,
                color = ZaraTextMuted,
                fontSize = 10.sp
            )
        }

        // Bubble container
        Box(
            modifier = Modifier
                .widthIn(max = 310.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(if (isUser) ZaraGlassSurfaceLighter else ZaraGlassSurface)
                .border(
                    width = 1.dp,
                    color = if (isUser) ZaraGlassBorder else ZaraCyberViolet.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = message.content,
                    color = ZaraTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                // Action execution badge if any
                if (message.actionExecuted != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (message.actionStatus == "SUCCESS")
                                    ZaraNeonGreen.copy(alpha = 0.15f)
                                else
                                    ZaraElectricBlue.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (message.actionStatus == "SUCCESS") Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (message.actionStatus == "SUCCESS") ZaraNeonGreen else ZaraElectricBlue,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${message.actionExecuted} (${message.actionStatus ?: "RUN"})",
                            color = if (message.actionStatus == "SUCCESS") ZaraNeonGreen else ZaraElectricBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Voice playback button for Zara replies
                if (!isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onSpeak,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Listen",
                                tint = ZaraNeonPink,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
