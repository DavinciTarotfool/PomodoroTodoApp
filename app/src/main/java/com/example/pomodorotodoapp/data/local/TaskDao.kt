package com.example.pomodorotodoapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.pomodorotodoapp.model.TodoTask
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM todo_tasks WHERE isArchived = 0 ORDER BY id DESC")
    fun getAllTasks(): Flow<List<TodoTask>>

    @Query("SELECT * FROM todo_tasks WHERE isArchived = 1 ORDER BY id DESC")
    fun getArchivedTasks(): Flow<List<TodoTask>>

    @Insert
    suspend fun insertTask(task: TodoTask)

    @Update
    suspend fun updateTask(task: TodoTask)

    @Delete
    suspend fun deleteTask(task: TodoTask)
}