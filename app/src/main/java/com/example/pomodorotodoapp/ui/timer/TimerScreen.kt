package com.example.pomodorotodoapp.ui.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pomodorotodoapp.model.TodoTask

@Composable
fun TimerScreen(
    timeLeft: Long,
    isRunning: Boolean,
    tasks: List<TodoTask>,
    selectedTaskId: Int?,
    onSelectTask: (Int) -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit
) {
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeText = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "番茄钟",
            style = MaterialTheme.typography.headlineMedium
        )

        val currentTask = tasks.find { it.id == selectedTaskId }

        if (currentTask != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "当前任务：${currentTask.title}",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = timeText,
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = onStart,
                enabled = !isRunning && timeLeft > 0 && selectedTaskId != null
            ) {
                Text("开始")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(onClick = onPause, enabled = isRunning) {
                Text("暂停")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(onClick = onReset) {
                Text("重置")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "选择当前任务",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { task ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = task.title)
                            Text(text = "已完成番茄：${task.pomodoroCount}")
                        }

                        RadioButton(
                            selected = selectedTaskId == task.id,
                            onClick = { onSelectTask(task.id) }
                        )
                    }
                }
            }
        }
    }
}