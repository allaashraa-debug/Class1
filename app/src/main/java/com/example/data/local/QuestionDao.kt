package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Question
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY id ASC")
    fun getAllQuestions(): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE subject = :subject ORDER BY id ASC")
    fun getQuestionsBySubject(subject: String): Flow<List<Question>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Question)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>)

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteQuestionById(id: Long)

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()
}
