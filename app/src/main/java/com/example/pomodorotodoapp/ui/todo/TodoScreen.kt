package com.example.pomodorotodoapp.ui.todo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.pomodorotodoapp.model.TodoTask
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.foundation.interaction.MutableInteractionSource

@Composable
fun TodoScreen(
    tasks: List<TodoTask>,
    onAddTask: (String) -> Unit,
    onToggleTask: (TodoTask) -> Unit,
    onArchiveTask: (TodoTask) -> Unit,
    onAddPomodoro: (TodoTask) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var expandedTaskId by remember { mutableStateOf<Int?>(null) }
    var revealedTaskId by remember { mutableStateOf<Int?>(null) }
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                expandedTaskId = null
                revealedTaskId = null
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("添加一个任务...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (text.isNotBlank()) {
                                onAddTask(text.trim())
                                text = ""
                                expandedTaskId = null
                                revealedTaskId = null
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "添加任务"
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors()
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = tasks,
                    key = { it.id }
                ) { task ->
                    SwipeRevealTaskItem(
                        task = task,
                        isExpanded = expandedTaskId == task.id,
                        isRevealed = revealedTaskId == task.id,
                        onExpandChange = { shouldExpand ->
                            expandedTaskId = if (shouldExpand) task.id else null
                            if (shouldExpand) {
                                revealedTaskId = null
                            }
                        },
                        onRevealChange = { shouldReveal ->
                            revealedTaskId = if (shouldReveal) task.id else null
                            if (shouldReveal) {
                                expandedTaskId = null
                            }
                        },
                        onToggleTask = onToggleTask,
                        onArchiveTask = {
                            onArchiveTask(task)
                            if (expandedTaskId == task.id) expandedTaskId = null
                            if (revealedTaskId == task.id) revealedTaskId = null
                        },
                        onAddPomodoro = onAddPomodoro
                    )
                }
            }
        }
    }
}

@Composable
private fun SwipeRevealTaskItem(
    task: TodoTask,
    isExpanded: Boolean,
    isRevealed: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onRevealChange: (Boolean) -> Unit,
    onToggleTask: (TodoTask) -> Unit,
    onArchiveTask: () -> Unit,
    onAddPomodoro: (TodoTask) -> Unit
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val actionWidthPx = with(density) { 92.dp.toPx() }
    val offsetX = remember(task.id) { Animatable(0f) }

    LaunchedEffect(isRevealed) {
        offsetX.animateTo(
            targetValue = if (isRevealed) -actionWidthPx else 0f,
            animationSpec = tween(180)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
    ) {
        Surface(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(20.dp))
                .clickable { onArchiveTask() },
            color = MaterialTheme.colorScheme.errorContainer,
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier.width(92.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "归档",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(offsetX.value.roundToInt(), 0)
                }
                .pointerInput(task.id, isRevealed) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset = (offsetX.value + dragAmount).coerceIn(-actionWidthPx, 0f)
                            coroutineScope.launch {
                                offsetX.snapTo(newOffset)
                            }
                        },
                        onDragEnd = {
                            coroutineScope.launch {
                                val shouldReveal = offsetX.value < -actionWidthPx / 2
                                onRevealChange(shouldReveal)
                            }
                        }
                    )
                }
                .clickable {
                    when {
                        isRevealed -> onRevealChange(false)
                        else -> onExpandChange(!isExpanded)
                    }
                },
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )

                    TaskStatusChip(isCompleted = task.isCompleted)

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = if (isExpanded) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                        contentDescription = "展开任务"
                    )
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "番茄次数：${task.pomodoroCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onAddPomodoro(task) },
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("+1 番茄")
                            }

                            OutlinedButton(
                                onClick = { onToggleTask(task) },
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(if (task.isCompleted) "标记未完成" else "标记完成")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskStatusChip(isCompleted: Boolean) {
    val containerColor = if (isCompleted) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isCompleted) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(containerColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isCompleted) "已完成" else "进行中",
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}