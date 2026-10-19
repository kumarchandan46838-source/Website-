package com.yesmin.ai.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // "PREFERENCE", "FACT", "CONVERSATION_SUMMARY", "DIRECTIVE"
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)
