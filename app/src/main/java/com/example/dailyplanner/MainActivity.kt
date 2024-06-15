package com.example.dailyplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dailyplanner.ui.theme.DailyPlannerTheme
import androidx.compose.ui.Alignment


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

@Composable
fun DailyPlannerApp() {
    var task by remember { mutableStateOf("") }
    var taskList by remember { mutableStateOf(listOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TaskInput(
            task = task,
            onTaskChange = { task = it },
            onAddTask = {
                if (task.isNotBlank()) {
                    taskList = taskList + task
                    task = ""
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TaskList(taskList)
    }
}

@Composable
fun TaskInput(
    task: String,
    onTaskChange: (String) -> Unit,
    onAddTask: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        BasicTextField(
            value = task,
            onValueChange = onTaskChange,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onAddTask() })
        )
        Button(
            onClick = onAddTask,
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text("Add")
        }
    }
}

@Composable
fun TaskList(tasks: List<String>) {
    Column {
        for (task in tasks) {
            Text(
                text = task,
                modifier = Modifier.padding(8.dp)
            )
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
