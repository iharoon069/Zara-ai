package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversation_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ConversationMessage>>

    @Query("SELECT * FROM conversation_messages ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMessages(limit: Int): Flow<List<ConversationMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ConversationMessage): Long

    @Query("DELETE FROM conversation_messages")
    suspend fun clearAllMessages()

    @Delete
    suspend fun deleteMessage(message: ConversationMessage)
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM user_memories ORDER BY isPinned DESC, timestamp DESC")
    fun getAllMemories(): Flow<List<UserMemory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: UserMemory): Long

    @Update
    suspend fun updateMemory(memory: UserMemory)

    @Delete
    suspend fun deleteMemory(memory: UserMemory)

    @Query("DELETE FROM user_memories WHERE id = :id")
    suspend fun deleteMemoryById(id: Long)

    @Query("SELECT * FROM user_memories WHERE key = :key LIMIT 1")
    suspend fun getMemoryByKey(key: String): UserMemory?
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM automation_tasks ORDER BY timestamp DESC")
    fun getAllTasks(): Flow<List<AutomationTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: AutomationTask): Long

    @Update
    suspend fun updateTask(task: AutomationTask)

    @Query("UPDATE automation_tasks SET status = :status, resultMessage = :result WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, status: String, result: String?)

    @Query("DELETE FROM automation_tasks")
    suspend fun clearAllTasks()
}
