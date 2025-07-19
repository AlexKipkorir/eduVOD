package com.example.eduvod.ui.screens.systemconfiguration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.model.SimpleItem
import com.example.eduvod.viewmodel.SystemConfigViewModel
import kotlinx.coroutines.launch

@Composable
fun CurriculumScreen(
    viewModel: SystemConfigViewModel = viewModel(),
    navController: NavHostController
) {
    val curriculums by remember { derivedStateOf { viewModel.curriculums } }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val isLoading by viewModel.isLoading.collectAsState()

    var newCurriculum by remember { mutableStateOf("") }
    var editMode by remember { mutableStateOf<SimpleItem?>(null) }
    var editedName by remember { mutableStateOf(TextFieldValue("")) }

    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("School Curriculum Configuration") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                // Add new curriculum
                OutlinedTextField(
                    value = newCurriculum,
                    onValueChange = { newCurriculum = it },
                    label = { Text("New Curriculum") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (newCurriculum.isBlank()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Curriculum name cannot be empty.")
                            }
                            return@Button
                        }
                        viewModel.addItem("Curriculum", newCurriculum.trim())
                        newCurriculum = ""
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Add")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider()

                Text("Available Curriculums", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(curriculums) { item ->
                        if (editMode == item) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedTextField(
                                    value = editedName,
                                    onValueChange = { editedName = it },
                                    label = { Text("Edit Curriculum") },
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = {
                                    editMode = null
                                    focusManager.clearFocus()
                                }) {
                                    Text("Cancel")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = {
                                    if (editedName.text.isBlank()) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Name cannot be empty.")
                                        }
                                    } else {
                                        viewModel.updateItem(
                                            section = "Curriculum",
                                            oldValue = item.name,
                                            newValue = editedName.text.trim()
                                        )
                                        editMode = null
                                    }
                                }) {
                                    Text("Save")
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.name, modifier = Modifier.weight(1f))
                                Button(onClick = {
                                    editMode = item
                                    editedName = TextFieldValue(item.name)
                                }) {
                                    Text("Edit")
                                }
                            }
                        }
                    }
                }
            }


            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xAAFFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = Color(0xFF1565C0),
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading Curriculums...", color = Color(0xFF1565C0))
                    }
                }
            }
        }
    }
}

