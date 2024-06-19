package com.example.dailyplanner

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dailyplanner.ui.theme.DailyPlannerTheme
import io.github.boguszpawlowski.composecalendar.ComposeCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import kotlinx.datetime.LocalDate
import java.time.LocalDate

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

data class Task(val description: String, val deadline: String, val category: String)

@Composable
fun DailyPlannerApp() {
    var taskDescription by remember { mutableStateOf("") }
    var taskDeadline by remember { mutableStateOf("") }
    var taskCategory by remember { mutableStateOf("") }
    var taskList by remember { mutableStateOf(listOf<Task>()) }
    var showSnackbar by remember { mutableStateOf(false) }
    var currentEditIndex by remember { mutableStateOf(-1) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Daily Planner",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        CalendarView(tasks = taskList, onDateSelected = { date ->
            selectedDate = date
        })
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
        TaskList(
            tasks = taskList,
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Day(date: LocalDate, tasks: List<Task>) {
    Box(modifier = Modifier.padding(4.dp)) {
        Text(date.dayOfMonth.toString())
        if (tasks.isNotEmpty()) {
            Box(modifier = Modifier.size(8.dp).background(Color.Red))
        }
    }
}


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
    Column {
        TextField(
            value = taskDescription,
            onValueChange = onTaskDescriptionChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = { Text(text = "Task Description") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )
        TextField(
            value = taskDeadline,
            onValueChange = onTaskDeadlineChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = { Text(text = "Deadline") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )
        TextField(
            value = taskCategory,
            onValueChange = onTaskCategoryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = { Text(text = "Category") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onAddTask() })
        )
        Button(
            onClick = onAddTask,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
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
    Column {
        for ((index, task) in tasks.withIndex()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = task.description, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Deadline: ${task.deadline}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Category: ${task.category}", style = MaterialTheme.typography.bodyMedium)
                    }
                    IconButton(
                        onClick = { onEditTask(index, task) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.edit_fill),
                            contentDescription = "Edit Task"
                        )
                    }
                    IconButton(
                        onClick = { onDeleteTask(index) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete_bin_line),
                            contentDescription = "Delete Task"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarView(tasks: List<Task>, onDateSelected: (LocalDate) -> Unit) {
    val calendarState = rememberSelectableCalendarState()
    val selectedDate = calendarState.selectionState.value

    Column {
        ComposeCalendar(
            state = calendarState,
            dayContent = { day ->
                Day(
                    date = day.date,
                    tasks = tasks.filter { task -> LocalDate.parse(task.deadline) == day.date }
                )
            }
        )
        if (selectedDate != null) {
            val tasksForSelectedDate = tasks.filter { task ->
                LocalDate.parse(task.deadline) == selectedDate
            }
            Text("Tasks for ${selectedDate.dayOfMonth} ${selectedDate.month.name.lowercase().replaceFirstChar { it.uppercase() }}")
            for (task in tasksForSelectedDate) {
                Text(task.description)
            }
            onDateSelected(selectedDate)
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
