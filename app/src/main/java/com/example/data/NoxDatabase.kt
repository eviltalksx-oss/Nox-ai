package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "conversations")
data class ChatConversationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: String,
    val sender: String, // "user" or "nox"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val category: String = "general",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "device_logs")
data class DeviceLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actionName: String,
    val description: String,
    val status: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface NoxDao {
    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    fun getAllConversations(): Flow<List<ChatConversationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ChatConversationEntity)

    @Query("DELETE FROM conversations WHERE id = :conversationId")
    suspend fun deleteConversation(conversationId: String)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesForConversation(conversationId: String)

    @Query("SELECT * FROM memories ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemory(id: Long)

    @Query("DELETE FROM memories")
    suspend fun clearAllMemories()

    @Query("SELECT * FROM device_logs ORDER BY timestamp DESC")
    fun getAllDeviceLogs(): Flow<List<DeviceLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceLog(log: DeviceLogEntity)
}

@Database(entities = [ChatConversationEntity::class, ChatMessageEntity::class, MemoryEntity::class, DeviceLogEntity::class], version = 1, exportSchema = false)
abstract class NoxDatabase : RoomDatabase() {
    abstract fun noxDao(): NoxDao

    companion object {
        @Volatile
        private var INSTANCE: NoxDatabase? = null

        fun getDatabase(context: Context): NoxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoxDatabase::class.java,
                    "nox_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
