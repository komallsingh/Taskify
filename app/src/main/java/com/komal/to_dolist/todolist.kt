package com.komal.to_dolist

import android.app.AlertDialog
import android.graphics.drawable.shapes.OvalShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class Task(
    val id: Int,  //UNIQUE ID
    var name: String,  //TASK NAME
    var tduration: Int,
    var isEditing: Boolean = false,
    var isDone: Boolean = false
)

@Composable
fun ToDoList() {
    var doTask by remember { mutableStateOf(listOf<Task>()) }  //LIST OF ALL TASK
    var showDialogue by remember { mutableStateOf(false) }  //ADD OR EDIT CONTROL
    var taskname by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var showCongrats by remember { mutableStateOf<String?>(null) }
    var showCongratsForTask by remember { mutableStateOf<Int?>(null) } //STORE ID OF LAST DONE TASK SO POPUP ONCE
    var editid by remember { mutableStateOf<Int?>(null) } //ID OF TASK WANT TO EDIT
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                showDialogue = true
                editid = null //NEW TASK FOR START
                taskname = ""
                duration = ""
            },
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterHorizontally).offset(y=20.dp)

        ) {
            Text("Add Task")
        }
        LazyColumn(  //TO DISPLAY SCROLLABLE LIST OF TASK
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(doTask) { task ->
                toDOListItems(
                    item = task,
                    onEditClick = {
                        taskname = task.name
                        duration = task.tduration.toString()
                        editid = task.id
                        showDialogue = true
                    }, onDeleteClick = {
                        doTask = doTask.filter { it.id != task.id }
                    },
                    onCheckedChange = { isChecked ->
                        doTask = doTask.map {
                            if (it.id == task.id) {
                                it.copy(isDone = isChecked)
                            } else {
                                it
                            }
                        }
                        if (isChecked && showCongratsForTask != task.id) {
                            showCongrats = task.name
                            showCongratsForTask = task.id
                        }
                    }
                )

                //toDOListItems(it,{},{}) //DISPLAY THE TASK
            }
        }
        if (showCongrats != null && showCongratsForTask != null) {
            AlertDialog(
                onDismissRequest = { showCongrats = null },
                title = { Text("Yayyy! \uD83D\uDE03", fontWeight = FontWeight.Bold) },
                text = { Text("Task Completed: $showCongrats \n" +
                        "\uD83D\uDC4D ") },
                confirmButton = {}
            )
        }
        LaunchedEffect(showCongrats) {
            delay(2000)
            showCongrats = null
            showCongratsForTask = null
        }


    }
    if (showDialogue) {
        AlertDialog(
            onDismissRequest = { showDialogue = false },
            title = {
                Text(
                    "TASK ON!",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = taskname,
                        onValueChange = { taskname = it }, //default string
                        singleLine = true,
                        label = { Text("Enter the Task") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.EditCalendar,
                                contentDescription = "Edit Task"
                            ) //TO IMPORT ICON ADDED TWO DEPENDENCIES IN BUILD GRADLE AND IMPORTED DEFAULT
                            //ICONS PRESENT
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it }, //default string
                        singleLine = true,
                        label = { Text("Enter Duration") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.HourglassEmpty,
                                contentDescription = "Duration"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        if (taskname.isNotBlank()) {
                            if (editid != null) {
                                doTask = doTask.map {
                                    if (it.id == editid) {
                                        it.copy(
                                            name = taskname,
                                            tduration = duration.toIntOrNull() ?: 0
                                        )
                                    } else {
                                        it
                                    }
                                }
                            } else{
                                    val newTask = Task(  //ADDING NEW TASK IN NEW LIST WITH NEW+OLD ITEMS
                                        id = doTask.size + 1,
                                        name = taskname,
                                        tduration = duration.toIntOrNull() ?: 0
                                    )
                                    doTask = doTask + newTask
                                }

                            showDialogue = false
                            taskname = ""
                            duration = ""
                            editid = null
                        }
                    }
                    ) {
                        Text(
                            text = if (editid != null) {
                                "SAVE CHANGES"
                            } else {
                                "ADD"
                            }
                        )
                    }
                    }

            }
        )
    }
}

@Composable
fun toDOListItems(
    item: Task,
    onEditClick: () -> Unit, //LAMBDA FUNCTION //creating our own onclick
    onDeleteClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit //FOR CHECKBOX
) {
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(4.dp, Color.DarkGray), shape = RoundedCornerShape(30)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {//not closed
        Checkbox(checked = item.isDone,
            onCheckedChange = { isChecked -> onCheckedChange(isChecked) }
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name, modifier = Modifier.padding(8.dp),
                style = if (item.isDone) {
                    TextStyle(
                        textDecoration = TextDecoration.LineThrough, fontSize = 20.sp
                    )

                } else {
                    TextStyle(fontSize = 24.sp)
                    LocalTextStyle.current
                }
            )
            Text(text = "${item.tduration} min", modifier = Modifier.padding(8.dp))
        }





        Row(modifier = Modifier.padding(10.dp)) {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}
