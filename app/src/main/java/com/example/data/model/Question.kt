package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val unit: String,
    val questionType: String, // "MULTIPLE_CHOICE", "TRUE_FALSE", "FILL_BLANK", "ARRANGE_LETTERS", "MATCH_COLUMNS", "IMAGE_SELECTION"
    val questionText: String,
    val hintImage: String, // Stores emoji like 🦁, 🍎, 🕋 or simple visual characters
    val options: String, // Semicolon separated options, e.g. "تفاحة;فراولة;موز" or matching pairs
    val correctAnswer: String, // Correct answer value(s)
    val explanation: String,
    val difficulty: String // "سهل", "متوسط", "صعب"
)
