package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "results")
data class ResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val roll: Int,
    val className: String,
    val examName: String, // "১ম সাময়িক পরীক্ষা", "২য় সাময়িক পরীক্ষা", "বার্ষিক পরীক্ষা"
    val subject: String,
    val obtainedMarks: Double,
    val totalMarks: Double = 100.0,
    val grade: String = "মুমতাজ" // মুমতাজ (A+), জায়্যিদ জিদ্দান (A), জায়্যিদ (B), মাকবুল (C), রাসেল (F)
)
