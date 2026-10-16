package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversation_messages")
data class ConversationMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val role: String, // "user" or "zara"
    val content: String,
    val mode: String = "girlfriend", // girlfriend, assistant, focus, professional
    val timestamp: Long = System.currentTimeMillis(),
    val actionExecuted: String? = null,
    val actionStatus: String? = null
)

@Entity(tableName = "user_memories")
data class UserMemory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // "preference", "fact", "nickname", "routine", "favorite"
    val key: String,
    val value: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

@Entity(tableName = "automation_tasks")
data class AutomationTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalCommand: String,
    val actionType: String,
    val parameters: String = "",
    val status: String, // "PENDING", "EXECUTING", "COMPLETED", "FAILED", "CANCELLED"
    val resultMessage: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
