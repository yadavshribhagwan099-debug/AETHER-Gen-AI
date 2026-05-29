package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AetherDao {

    // --- CHAT LOGS ---
    @Query("SELECT * FROM chat_logs WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getChatLogsForSession(sessionId: String): Flow<List<ChatLog>>

    @Query("SELECT DISTINCT sessionId FROM chat_logs ORDER BY id DESC")
    fun getAllSessionIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatLog(chatLog: ChatLog): Long

    @Query("DELETE FROM chat_logs WHERE sessionId = :sessionId")
    suspend fun clearChatSession(sessionId: String)

    // --- MEMORIES ---
    @Query("SELECT * FROM memory_nodes ORDER BY timestamp DESC")
    fun getAllMemoriesFlow(): Flow<List<MemoryNode>>

    @Query("SELECT * FROM memory_nodes ORDER BY timestamp DESC")
    suspend fun getAllMemoriesDirect(): List<MemoryNode>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memoryNode: MemoryNode): Long

    @Query("DELETE FROM memory_nodes WHERE id = :id")
    suspend fun deleteMemory(id: Long)

    @Query("DELETE FROM memory_nodes")
    suspend fun clearAllMemories()

    // --- TASKS ---
    @Query("SELECT * FROM tasks ORDER BY timestamp DESC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, isCompleted: Boolean)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)

    // --- FOCUS SESSIONS ---
    @Query("SELECT * FROM focus_sessions ORDER BY timestamp DESC")
    fun getAllFocusSessions(): Flow<List<FocusSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSession): Long

    // --- NOTES ---
    @Query("SELECT * FROM app_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<AppNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: AppNote): Long

    @Query("DELETE FROM app_notes WHERE id = :id")
    suspend fun deleteNote(id: Long)
}
