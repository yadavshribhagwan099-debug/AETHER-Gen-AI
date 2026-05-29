package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ChatLog::class, MemoryNode::class, TaskItem::class, FocusSession::class, AppNote::class],
    version = 2,
    exportSchema = false
)
abstract class AetherDatabase : RoomDatabase() {
    abstract fun aetherDao(): AetherDao

    companion object {
        @Volatile
        private var INSTANCE: AetherDatabase? = null

        fun getDatabase(context: Context): AetherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AetherDatabase::class.java,
                    "aether_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
