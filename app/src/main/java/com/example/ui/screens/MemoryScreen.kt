package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserMemory
import com.example.ui.MainViewModel
import com.example.ui.components.ZaraGlassCard
import com.example.ui.theme.ZaraCyberViolet
import com.example.ui.theme.ZaraDeepVoid
import com.example.ui.theme.ZaraGlassBorder
import com.example.ui.theme.ZaraGlassSurface
import com.example.ui.theme.ZaraGlassSurfaceLighter
import com.example.ui.theme.ZaraNeonCyan
import com.example.ui.theme.ZaraNeonGold
import com.example.ui.theme.ZaraNeonPink
import com.example.ui.theme.ZaraNeonRed
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextPrimary
import com.example.ui.theme.ZaraTextSecondary

@Composable
fun MemoryScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val memories by viewModel.memories.collectAsState()
    val nickname by viewModel.preferences.userNickname.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingNickname by remember { mutableStateOf(false) }
    var nicknameInput by remember { mutableStateOf(nickname) }

    var newCategory by remember { mutableStateOf("preference") }
    var newKey by remember { mutableStateOf("") }
    var newValue by remember { mutableStateOf("") }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Memory", color = ZaraTextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newKey,
                        onValueChange = { newKey = it },
                        label = { Text("Topic / Key (e.g. favorite_color)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = ZaraTextPrimary,
                            unfocusedTextColor = ZaraTextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = newValue,
                        onValueChange = { newValue = it },
                        label = { Text("Memory Detail (e.g. Loves black coffee)") },
                        singleLine = false,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = ZaraTextPrimary,
                            unfocusedTextColor = ZaraTextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newKey.isNotBlank() && newValue.isNotBlank()) {
                            viewModel.addMemory(newCategory, newKey, newValue)
                            newKey = ""
                            newValue = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Save", color = ZaraNeonCyan)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
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
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("memory_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ZaraNeonCyan
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "NEURAL MEMORY",
                        color = ZaraTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${memories.size} personalized neural associations",
                        color = ZaraTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.testTag("add_memory_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Memory",
                    tint = ZaraNeonCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Nickname Card
        ZaraGlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PREFERRED CALLSIGN / NICKNAME",
                        color = ZaraNeonPink,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    if (editingNickname) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = nicknameInput,
                                onValueChange = { nicknameInput = it },
                                singleLine = true,
                                modifier = Modifier.width(160.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = ZaraTextPrimary,
                                    unfocusedTextColor = ZaraTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    viewModel.preferences.setUserNickname(nicknameInput)
                                    editingNickname = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ZaraNeonPink),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Save", color = ZaraDeepVoid, fontSize = 11.sp)
                            }
                        }
                    } else {
                        Text(
                            text = "\"$nickname\"",
                            color = ZaraTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (!editingNickname) {
                    IconButton(onClick = { editingNickname = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Nickname",
                            tint = ZaraTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "STORED MEMORIES & PREFERENCES",
            color = ZaraNeonCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(memories, key = { it.id }) { memory ->
                MemoryItemRow(memory = memory, onDelete = { viewModel.deleteMemory(memory) })
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun MemoryItemRow(memory: UserMemory, onDelete: () -> Unit) {
    ZaraGlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (memory.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = ZaraNeonGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = memory.key.uppercase(),
                        color = ZaraNeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = memory.value,
                    color = ZaraTextPrimary,
                    fontSize = 13.sp
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = ZaraTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
