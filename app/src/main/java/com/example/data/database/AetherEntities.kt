package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_logs")
data class ChatLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,       // Grouping multiple chat sessions
    val role: String,            // "user" or "model"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "memory_nodes")
data class MemoryNode(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val keyPhrase: String,       // e.g. "User lives in New York"
    val contextType: String,     // e.g. "preference", "fact", "habit"
    val intensity: Float = 1.0f, // 0.0 to 1.0 importance
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,        // e.g. "Study", "Creator", "Professional", "Life"
    val isCompleted: Boolean = false,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val minutes: Int,
    val ambientType: String,     // e.g. "Cosmic White Noise", "Deep Space Resonance"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_notes")
data class AppNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "Life",
    val timestamp: Long = System.currentTimeMillis()
)
