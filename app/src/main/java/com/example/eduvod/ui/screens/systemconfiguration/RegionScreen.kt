package com.example.eduvod.ui.screens.systemconfiguration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.viewmodel.SystemConfigViewModel
import kotlinx.coroutines.launch

@Composable
fun RegionScreen(
    viewModel: SystemConfigViewModel = viewModel(),
    navController: NavHostController
) {
    val regions = viewModel.regions
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    var newRegion by remember { mutableStateOf("") }
    var editingRegion by remember { mutableStateOf<String?>(null) }
    var editText by remember { mutableStateOf(TextFieldValue("")) }


    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Manage Regions") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = newRegion,
                            onValueChange = { newRegion = it },
                            label = { Text("New Region Name") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                        Button(
                            onClick = {
                                if (newRegion.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Region name cannot be empty.")
                                    }
                                } else {
                                    viewModel.addItem("Region", newRegion.trim())
                                    newRegion = ""
                                    focusManager.clearFocus()
                                }
                            }
                        ) {
                            Text("Add")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))


                    LazyColumn {
                        items(regions) { region ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = 4.dp
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = region.name,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = {
                                        editingRegion = region.name
                                        editText = TextFieldValue(region.name)
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
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
                            Text("Loading regions...", color = Color(0xFF1565C0))
                        }
                    }
                }

                // Edit Dialog
                if (editingRegion != null) {
                    AlertDialog(
                        onDismissRequest = {
                            editingRegion = null
                        },
                        title = { Text("Edit Region") },
                        text = {
                            OutlinedTextField(
                                value = editText,
                                onValueChange = { editText = it },
                                label = { Text("New Region Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val old = editingRegion ?: return@TextButton
                                    val new = editText.text.trim()
                                    if (new.isNotBlank()) {
                                        viewModel.updateItem("Region", old, new)
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Region name cannot be empty.")
                                        }
                                    }
                                    editingRegion = null
                                }
                            ) {
                                Text("Update")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { editingRegion = null }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    )
}

