package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ConversationMessage::class,
        UserMemory::class,
        AutomationTask::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ZaraDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun memoryDao(): MemoryDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: ZaraDatabase? = null

        fun getDatabase(context: Context): ZaraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZaraDatabase::class.java,
                    "zara_ai_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
