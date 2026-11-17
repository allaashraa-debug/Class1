package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Attempt
import kotlinx.coroutines.flow.Flow

@Dao
interface AttemptDao {
    @Query("SELECT * FROM attempts ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<Attempt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: Attempt)

    @Query("DELETE FROM attempts WHERE id = :id")
    suspend fun deleteAttemptById(id: Long)

    @Query("DELETE FROM attempts")
    suspend fun clearAllAttempts()
}
