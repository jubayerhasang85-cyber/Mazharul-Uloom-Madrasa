package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val roll: Int,
    val className: String,
    val phone: String,
    val guardianName: String = "",
    val admissionYear: String = "২০২৪",
    val bloodGroup: String = "O+"
)
