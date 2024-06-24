package com.example.dailyplanner

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dailyplanner.ui.theme.DailyPlannerTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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

data class Task(val description: String, val deadline: String, val category: String)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyPlannerApp() {
    var taskDescription by remember { mutableStateOf("") }
    var taskDeadline by remember { mutableStateOf("") }
    var taskCategory by remember { mutableStateOf("") }
    var taskList by remember { mutableStateOf(listOf<Task>()) }
    var showSnackbar by remember { mutableStateOf(false) }
    var currentEditIndex by remember { mutableStateOf(-1) }
    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .scale(1.3f)
                .offset(y = -60.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFFA726), Color(0xFFFF7043), Color(0xFFFF5722))
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Daily Planner",
                        color = Color(0xFFF4F4F4), // Set text color here
                        style = MaterialTheme.typography.headlineSmall.copy(
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(4f, 4f),
                            )
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            TaskInput(
                taskDescription = taskDescription,
                taskDeadline = taskDeadline,
                taskCategory = taskCategory,
                onTaskDescriptionChange = { taskDescription = it },
                onTaskDeadlineChange = { taskDeadline = it },
                onTaskCategoryChange = { taskCategory = it },
                onAddTask = {
                    if (taskDescription.isNotBlank() && taskDeadline.isNotBlank() && taskCategory.isNotBlank()) {
                        if (currentEditIndex >= 0) {
                            taskList = taskList.toMutableList().also {
                                it[currentEditIndex] = Task(taskDescription, taskDeadline, taskCategory)
                            }
                            currentEditIndex = -1
                        } else {
                            taskList = taskList + Task(taskDescription, taskDeadline, taskCategory)
                        }
                        taskDescription = ""
                        taskDeadline = ""
                        taskCategory = ""
                        showSnackbar = true
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                label = { Text(text = "Search Tasks") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            TaskList(
                tasks = taskList.filter {
                    it.description.contains(searchQuery, ignoreCase = true) ||
                            it.deadline.contains(searchQuery, ignoreCase = true) ||
                            it.category.contains(searchQuery, ignoreCase = true)
                },
                onEditTask = { index, task ->
                    taskDescription = task.description
                    taskDeadline = task.deadline
                    taskCategory = task.category
                    currentEditIndex = index
                },
                onDeleteTask = { index ->
                    taskList = taskList.toMutableList().also { it.removeAt(index) }
                    if (currentEditIndex == index) {
                        currentEditIndex = -1
                        taskDescription = ""
                        taskDeadline = ""
                        taskCategory = ""
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
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskInput(
    taskDescription: String,
    taskDeadline: String,
    taskCategory: String,
    onTaskDescriptionChange: (String) -> Unit,
    onTaskDeadlineChange: (String) -> Unit,
    onTaskCategoryChange: (String) -> Unit,
    onAddTask: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val timePickerDialog = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute)
                    onTaskDeadlineChange(selectedDateTime.format(dateFormatter))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column {
        TextField(
            value = taskDescription,
            onValueChange = onTaskDescriptionChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(8.dp),
            label = { Text(text = "Task Description") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            TextField(
                value = taskDeadline,
                onValueChange = {},
                enabled = false,
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                label = { Text(text = "Deadline") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.None
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Pict Date")
            }
        }
        TextField(
            value = taskCategory,
            onValueChange = onTaskCategoryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(8.dp),
            label = { Text(text = "Category") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onAddTask() }
            )
        )
        Button(
            onClick = onAddTask,
            modifier = Modifier
                .align(Alignment.End)
                .padding(8.dp)
        ) {
            Text("Add Task")
        }
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onEditTask: (Int, Task) -> Unit,
    onDeleteTask: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(tasks.size) { index ->
            val task = tasks[index]
            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = task.description, style = MaterialTheme.typography.bodyLarge)
                        Text(text = task.deadline, style = MaterialTheme.typography.bodyMedium)
                        Text(text = task.category, style = MaterialTheme.typography.bodyMedium)
                    }
                    IconButton(onClick = { onEditTask(index, task) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.edit_fill),
                            contentDescription = "Edit",
                            modifier = Modifier.size(24.dp) // Atur ukuran ikon di sini
                        )
                    }
                    IconButton(onClick = { onDeleteTask(index) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete_bin_line),
                            contentDescription = "Delete",
                            modifier = Modifier.size(24.dp) // Atur ukuran ikon di sini
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DailyPlannerTheme {
        DailyPlannerApp()
    }
}
