package com.example.pomodorotodoapp.data

import com.example.pomodorotodoapp.data.local.PomodoroSessionDao
import com.example.pomodorotodoapp.data.local.TaskDao
import com.example.pomodorotodoapp.model.PomodoroSession
import com.example.pomodorotodoapp.model.TodoTask
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val pomodoroSessionDao: PomodoroSessionDao
) {

    fun getAllTasks(): Flow<List<TodoTask>> = taskDao.getAllTasks()

    fun getArchivedTasks(): Flow<List<TodoTask>> = taskDao.getArchivedTasks()

    fun getAllSessions(): Flow<List<PomodoroSession>> = pomodoroSessionDao.getAllSessions()

    suspend fun insertTask(title: String) {
        if (title.isNotBlank()) {
            taskDao.insertTask(TodoTask(title = title.trim()))
        }
    }

    suspend fun updateTask(task: TodoTask) {
        taskDao.updateTask(task)
    }

    suspend fun archiveTask(task: TodoTask) {
        taskDao.updateTask(
            task.copy(isArchived = true)
        )
    }

    suspend fun restoreTask(task: TodoTask) {
        taskDao.updateTask(
            task.copy(isArchived = false)
        )
    }

    suspend fun addPomodoro(task: TodoTask) {
        taskDao.updateTask(
            task.copy(pomodoroCount = task.pomodoroCount + 1)
        )

        pomodoroSessionDao.insertSession(
            PomodoroSession(
                taskId = task.id,
                taskTitleSnapshot = task.title
            )
        )
    }
}