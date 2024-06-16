package com.example.dailyplanner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dailyplanner.ui.theme.DailyPlannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        setContent {
            DailyPlannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DailyPlannerApp()
                }
            }
        }
    }
}

data class Task(val description: String, val deadline: String)

@Composable
fun DailyPlannerApp() {
    var taskDescription by remember { mutableStateOf("") }
    var taskDeadline by remember { mutableStateOf("") }
    var taskList by remember { mutableStateOf(listOf<Task>()) }
    var showSnackbar by remember { mutableStateOf(false) }
    var currentEditIndex by remember { mutableStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "To-Do List",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        TaskInput(
            taskDescription = taskDescription,
            taskDeadline = taskDeadline,
            onTaskDescriptionChange = { taskDescription = it },
            onTaskDeadlineChange = { taskDeadline = it },
            onAddTask = {
                if (taskDescription.isNotBlank() && taskDeadline.isNotBlank()) {
                    if (currentEditIndex >= 0) {
                        taskList = taskList.toMutableList().also {
                            it[currentEditIndex] = Task(taskDescription, taskDeadline)
                        }
                        currentEditIndex = -1
                    } else {
                        taskList = taskList + Task(taskDescription, taskDeadline)
                    }
                    taskDescription = ""
                    taskDeadline = ""
                    showSnackbar = true
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TaskList(
            tasks = taskList,
            onEditTask = { index, task ->
                taskDescription = task.description
                taskDeadline = task.deadline
                currentEditIndex = index
            },
            onDeleteTask = { index ->
                taskList = taskList.toMutableList().also { it.removeAt(index) }
                if (currentEditIndex == index) {
                    currentEditIndex = -1
                    taskDescription = ""
                    taskDeadline = ""
                } else if (currentEditIndex > index) {
                    currentEditIndex -= 1
                }
            }
        )

        if (showSnackbar) {
            Snackbar(
                action = {
                    TextButton(onClick = { showSnackbar = false }) {
                        Text("OK")
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Task added")
            }
        }
    }
}

@Composable
fun TaskInput(
    taskDescription: String,
    taskDeadline: String,
    onTaskDescriptionChange: (String) -> Unit,
    onTaskDeadlineChange: (String) -> Unit,
    onAddTask: () -> Unit
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = taskDescription,
                onValueChange = onTaskDescriptionChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                )
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = taskDeadline,
                onValueChange = onTaskDeadlineChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onAddTask() }),
                placeholder = { Text(text = "Deadline") }
            )
        }
        Button(
            onClick = onAddTask,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add")
        }
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onEditTask: (Int, Task) -> Unit,
    onDeleteTask: (Int) -> Unit
) {
    Column {
        for ((index, task) in tasks.withIndex()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f).padding(8.dp)
                ) {
                    Text(text = task.description)
                    Text(text = "Deadline: ${task.deadline}")
                }
                IconButton(
                    onClick = { onEditTask(index, task) },
                    modifier = Modifier.size(24.dp) // Menyesuaikan ukuran ikon
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_fill),
                        contentDescription = "Edit Task",
                        modifier = Modifier.size(24.dp) // Menyesuaikan ukuran ikon
                    )
                }
                IconButton(
                    onClick = { onDeleteTask(index) },
                    modifier = Modifier.size(24.dp) // Menyesuaikan ukuran ikon
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete_bin_line),
                        contentDescription = "Delete Task",
                        modifier = Modifier.size(24.dp) // Menyesuaikan ukuran ikon
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DailyPlannerTheme {
        DailyPlannerApp()
    }
}
