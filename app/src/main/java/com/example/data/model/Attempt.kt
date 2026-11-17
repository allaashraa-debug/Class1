package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attempts")
data class Attempt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentName: String,
    val subject: String,
    val score: Int,
    val totalQuestions: Int,
    val starsEarned: Int,
    val wrongQuestionsCount: Int,
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis()
)
