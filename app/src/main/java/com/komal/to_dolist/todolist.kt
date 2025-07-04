package com.komal.to_dolist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.komal.to_dolist.model.TaskEntity
import kotlinx.coroutines.delay

@Composable
fun ToDoListScreen(viewModel: TaskViewModel = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }
    var taskName by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var editTaskId by remember { mutableStateOf<Int?>(null) }

    val tasks by viewModel.tasks.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                showDialog = true
                editTaskId = null
                taskName = ""
                duration = ""
            },
            modifier = Modifier.padding(10.dp).offset(y=20.dp)
        ) {
            Text("Add Task")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onCheckedChange = { isChecked ->
                        viewModel.updateTask(task.copy(isDone = isChecked))
                    },
                    onEditClick = {
                        taskName = task.name
                        duration = task.tduration.toString()
                        editTaskId = task.id
                        showDialog = true
                    },
                    onDeleteClick = {
                        viewModel.deleteTask(task)
                    }
                )
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        text = if (editTaskId != null) "Edit Task" else "Add Task",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = taskName,
                            onValueChange = { taskName = it },
                            label = { Text("Task Name") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.EditCalendar, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("Duration (min)") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.HourglassEmpty, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val tdurationInt = duration.toIntOrNull() ?: 0
                            if (taskName.isNotBlank()) {
                                if (editTaskId != null) {
                                    viewModel.updateTask(
                                        TaskEntity(editTaskId!!, taskName, tdurationInt, false)
                                    )
                                } else {
                                    viewModel.addTask(taskName, tdurationInt)

                                }
                                showDialog = false
                                taskName = ""
                                duration = ""
                                editTaskId = null
                            }
                        }
                    ) {
                        Text(text = if (editTaskId != null) "Save" else "Add")
                    }
                }
            )
        }
    }
}

@Composable
fun TaskItem(
    task: TaskEntity,
    onCheckedChange: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth()
            .border(BorderStroke(2.dp, Color.Gray), shape = RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Checkbox(
            checked = task.isDone,
            onCheckedChange = onCheckedChange
        )
        Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
            Text(
                text = task.name,
                style = TextStyle(
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                    fontSize = 18.sp
                )
            )
            Text(text = "${task.tduration} min", fontSize = 14.sp, color = Color.Gray)
        }
        Row {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
