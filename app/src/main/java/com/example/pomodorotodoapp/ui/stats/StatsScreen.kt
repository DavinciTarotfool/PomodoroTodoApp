package com.example.pomodorotodoapp.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pomodorotodoapp.model.PomodoroSession
import com.example.pomodorotodoapp.model.TodoTask
import java.util.Calendar

@Composable
fun StatsScreen(
    tasks: List<TodoTask>,
    sessions: List<PomodoroSession>
) {
    val todaySessions = sessions.filter { isToday(it.completedAt) }

    val totalTodayPomodoros = todaySessions.size

    val taskPomodoroMap = todaySessions
        .groupingBy { it.taskId }
        .eachCount()

    val sortedEntries = taskPomodoroMap.entries
        .sortedByDescending { it.value }

    val topTaskEntry = taskPomodoroMap.maxByOrNull { it.value }
    val topTask = tasks.find { it.id == topTaskEntry?.key }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "今日统计",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "今日总番茄数：🍅 $totalTodayPomodoros",
                    style = MaterialTheme.typography.titleLarge
                )

                if (topTask != null && topTaskEntry != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "今日最专注：${topTask.title} 🍅 ${topTaskEntry.value}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "各任务完成情况",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (taskPomodoroMap.isEmpty()) {
            Text(
                text = "今天还没有完成番茄钟，去开始第一个专注时段吧。",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedEntries) { entry ->
                    val task = tasks.find { it.id == entry.key }
                    val titleFromSession = todaySessions
                        .firstOrNull { it.taskId == entry.key }
                        ?.taskTitleSnapshot

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = task?.title ?: titleFromSession ?: "未知任务",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "今日番茄数：🍅 ${entry.value}")
                        }
                    }
                }
            }
        }
    }
}

private fun isToday(timestamp: Long): Boolean {
    val today = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
        timeInMillis = timestamp
    }

    return today.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
}
