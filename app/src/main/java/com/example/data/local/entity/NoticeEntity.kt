package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notices")
data class NoticeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String, // "জরুরি", "পরীক্ষা", "ছুটি", "সাধারণ"
    val date: String,
    val authorName: String = "মাদ্রাসা কর্তৃপক্ষ",
    val authorRole: String = "প্রধান শিক্ষক",
    val isPinned: Boolean = false
)
