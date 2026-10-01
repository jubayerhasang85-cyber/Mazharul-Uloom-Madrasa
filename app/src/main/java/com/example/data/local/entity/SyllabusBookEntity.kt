package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "syllabus_books")
data class SyllabusBookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val className: String,
    val bookName: String,
    val subject: String,
    val authorName: String = "",
    val totalMarks: Int = 100,
    val description: String = ""
)
