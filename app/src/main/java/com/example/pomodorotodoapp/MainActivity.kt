package com.example.pomodorotodoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodorotodoapp.data.TaskRepository
import com.example.pomodorotodoapp.data.local.AppDatabase
import com.example.pomodorotodoapp.ui.theme.PomodoroTodoAppTheme
import com.example.pomodorotodoapp.ui.timer.TimerScreen
import com.example.pomodorotodoapp.ui.todo.TodoScreen
import com.example.pomodorotodoapp.viewmodel.TimerViewModel
import com.example.pomodorotodoapp.viewmodel.TodoViewModel
import com.example.pomodorotodoapp.viewmodel.TodoViewModelFactory

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

import androidx.compose.material.icons.filled.Archive
import com.example.pomodorotodoapp.ui.archive.ArchiveScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.filled.BarChart
import com.example.pomodorotodoapp.ui.stats.StatsScreen

sealed class BottomScreen(val route: String, val label: String) {
    data object Todo : BottomScreen("todo", "待办")
    data object Timer : BottomScreen("timer", "番茄钟")
    data object Stats : BottomScreen("stats", "统计")
    data object Archive : BottomScreen("archive", "归档")
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = TaskRepository(
            database.taskDao(),
            database.pomodoroSessionDao()
        )

        setContent {
            PomodoroTodoAppTheme {
                val todoViewModel: TodoViewModel = viewModel(
                    factory = TodoViewModelFactory(repository)
                )
                val timerViewModel: TimerViewModel = viewModel()

                val tasks by todoViewModel.tasks.collectAsState()
                val archivedTasks by todoViewModel.archivedTasks.collectAsState()
                val sessions by todoViewModel.sessions.collectAsState()
                val timeLeft by timerViewModel.timeLeft.collectAsState()
                val isRunning by timerViewModel.isRunning.collectAsState()
                val isFinished by timerViewModel.isFinished.collectAsState()

                var currentScreen by remember { mutableStateOf<BottomScreen>(BottomScreen.Todo) }
                var selectedTaskId by remember { mutableStateOf<Int?>(null) }

                val context = LocalContext.current

                LaunchedEffect(isFinished, selectedTaskId, tasks) {
                    if (isFinished && selectedTaskId != null) {

                        val selectedTask = tasks.find { it.id == selectedTaskId }
                        if (selectedTask != null) {
                            todoViewModel.addPomodoro(selectedTask)

                            Toast.makeText(
                                context,
                                "任务 ${selectedTask.title} 完成一个番茄钟！+1 🍅",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        timerViewModel.consumeFinishedEvent()
                    }
                }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentScreen == BottomScreen.Todo,
                                onClick = { currentScreen = BottomScreen.Todo },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "待办"
                                    )
                                },
                                label = { Text("待办") }
                            )

                            NavigationBarItem(
                                selected = currentScreen == BottomScreen.Timer,
                                onClick = { currentScreen = BottomScreen.Timer },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = "番茄钟"
                                    )
                                },
                                label = { Text("番茄钟") }
                            )

                            NavigationBarItem(
                                selected = currentScreen == BottomScreen.Stats,
                                onClick = { currentScreen = BottomScreen.Stats },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.BarChart,
                                        contentDescription = "统计"
                                    )
                                },
                                label = { Text("统计") }
                            )

                            NavigationBarItem(
                                selected = currentScreen == BottomScreen.Archive,
                                onClick = { currentScreen = BottomScreen.Archive },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Archive,
                                        contentDescription = "归档"
                                    )
                                },
                                label = { Text("归档") }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            BottomScreen.Todo -> {
                                TodoScreen(
                                    tasks = tasks,
                                    onAddTask = { todoViewModel.addTask(it) },
                                    onToggleTask = { todoViewModel.toggleTask(it) },
                                    onArchiveTask = { todoViewModel.archiveTask(it) },
                                    onAddPomodoro = { todoViewModel.addPomodoro(it) }
                                )
                            }

                            BottomScreen.Timer -> {
                                TimerScreen(
                                    timeLeft = timeLeft,
                                    isRunning = isRunning,
                                    tasks = tasks,
                                    selectedTaskId = selectedTaskId,
                                    onSelectTask = { selectedTaskId = it },
                                    onStart = { timerViewModel.startTimer() },
                                    onPause = { timerViewModel.pauseTimer() },
                                    onReset = { timerViewModel.resetTimer() }
                                )
                            }

                            BottomScreen.Stats -> {
                                StatsScreen(
                                    tasks = tasks,
                                    sessions = sessions
                                )
                            }

                            BottomScreen.Archive -> {
                                ArchiveScreen(
                                    archivedTasks = archivedTasks,
                                    onRestoreTask = { todoViewModel.restoreTask(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}