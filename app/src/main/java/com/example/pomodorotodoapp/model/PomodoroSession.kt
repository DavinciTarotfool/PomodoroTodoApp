package com.example.pomodorotodoapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskId: Int,
    val taskTitleSnapshot: String,
    val completedAt: Long = System.currentTimeMillis()
)