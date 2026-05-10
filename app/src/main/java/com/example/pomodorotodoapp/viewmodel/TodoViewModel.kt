package com.example.pomodorotodoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pomodorotodoapp.data.TaskRepository
import com.example.pomodorotodoapp.model.TodoTask
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.pomodorotodoapp.model.PomodoroSession
import kotlinx.coroutines.flow.stateIn
class TodoViewModel(private val repository: TaskRepository) : ViewModel() {

    val tasks: StateFlow<List<TodoTask>> = repository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val archivedTasks = repository.getArchivedTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val sessions = repository.getAllSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTask(title: String) {
        viewModelScope.launch {
            repository.insertTask(title)
        }
    }

    fun toggleTask(task: TodoTask) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun archiveTask(task: TodoTask) {
        viewModelScope.launch {
            repository.archiveTask(task)
        }
    }

    fun restoreTask(task: TodoTask) {
        viewModelScope.launch {
            repository.restoreTask(task)
        }
    }

    fun addPomodoro(task: TodoTask) {
        viewModelScope.launch {
            repository.addPomodoro(task)
        }
    }
}

class TodoViewModelFactory(
    private val repository: TaskRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TodoViewModel(repository) as T
    }
}