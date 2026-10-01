package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val ilhaqNumber: String = "",
    val pin: String,
    val designation: String,
    val role: String // "TEACHER" or "HEADMASTER"
)
