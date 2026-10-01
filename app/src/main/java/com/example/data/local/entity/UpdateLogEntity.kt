package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "update_logs")
data class UpdateLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val version: String,
    val date: String,
    val title: String,
    val description: String,
    val category: String = "ফিচার আপডেট" // "সিস্টেম", "ফিচার", "সিকিউরিটি", "ডাটাবেজ"
)
