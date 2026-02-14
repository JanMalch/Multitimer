package io.github.janmalch.multitimer.ui.screens.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.janmalch.multitimer.models.Timer
import io.github.janmalch.multitimer.ui.components.TimerColorBox

data object ConfigScreen

@Composable
fun ConfigScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConfigViewModel = hiltViewModel()
) {
    val timers by viewModel.timers.collectAsStateWithLifecycle()
    ConfigScreen(
        timers = timers,
        navigateUp = navigateUp,
        addTimer = { name, color -> viewModel.addTimer(name, color) },
        modifier = modifier,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfigScreen(
    timers: List<Timer>,
    navigateUp: () -> Unit,
    addTimer: (name: String, color: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isNewDialogOpen by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text("Timer")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isNewDialogOpen = true
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "New timer")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            for (timer in timers) {
                ListItem(
                    leadingContent = {
                        TimerColorBox(timer)
                    },
                    headlineContent = {
                        Text(text = timer.name)
                    }
                )
            }
        }
        if (isNewDialogOpen) {
            var name by rememberSaveable { mutableStateOf("") }
            var color by rememberSaveable { mutableStateOf("#FF0000") }
            val isValidName = name.isNotBlank()
            val isValidColor = runCatching { color.toColorInt() }.isSuccess
            AlertDialog(
                onDismissRequest = {
                    isNewDialogOpen = false
                },
                icon = {
                    Icon(Icons.Filled.AddCircle, contentDescription = null)
                },
                title = {
                    Text("New timer")
                },
                text = {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        TextField(
                            value = name,
                            onValueChange = { name = it },
                            singleLine = true,
                            isError = !isValidName,
                            label = {
                                Text(text = "Name")
                            }
                        )
                        TextField(
                            value = color,
                            onValueChange = { color = it },
                            singleLine = true,
                            isError = !isValidColor,
                            label = {
                                Text(text = "Color")
                            }
                        )
                    }
                },
                confirmButton = {

                    TextButton(
                        enabled = isValidName && isValidColor,
                        onClick = {
                            addTimer(name.trim(), color)
                            isNewDialogOpen = false
                        }
                    ) {
                        Text(text = "Save")
                    }
                }
            )
        }
    }
}
