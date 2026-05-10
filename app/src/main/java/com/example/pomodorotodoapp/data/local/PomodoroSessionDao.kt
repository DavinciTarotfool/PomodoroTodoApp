package com.example.pomodorotodoapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pomodorotodoapp.model.PomodoroSession
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PomodoroSession)

    @Query("SELECT * FROM pomodoro_sessions")
    fun getAllSessions(): Flow<List<PomodoroSession>>
}