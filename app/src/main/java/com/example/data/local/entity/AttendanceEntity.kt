package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendances")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val roll: Int,
    val className: String,
    val date: String, // e.g. "2026-10-01"
    val status: String, // "উপস্থিত", "অনুপস্থিত", "ছুটি"
    val remarks: String = ""
)
