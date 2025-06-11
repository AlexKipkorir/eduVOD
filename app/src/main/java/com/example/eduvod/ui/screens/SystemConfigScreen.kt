package com.example.eduvod.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.viewmodel.SystemConfigViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemConfigScreen(
    navController: NavHostController,
    viewModel: SystemConfigViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var isEditDialog by remember { mutableStateOf(false) }
    var dialogSection by remember { mutableStateOf("") }
    var oldValue by remember { mutableStateOf("") }
    var inputValue by remember { mutableStateOf("") }

    val configSections = listOf(
        "School Type" to viewModel.types,
        "School Category" to viewModel.categories,
        "Curriculum" to viewModel.curriculums,
        "Region / Diocese" to viewModel.regions
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Configuration", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1))
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            configSections.forEach { (title, list) ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 24.sp,
                                        color = Color(0xFF0D47A1),
                                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                                    )
                                )

                                IconButton(onClick = {
                                    inputValue = ""
                                    oldValue = ""
                                    dialogSection = title
                                    isEditDialog = false
                                    showDialog = true
                                }) {
                                    Icon(Icons.Default.Add, contentDescription = "Add")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (list.isEmpty()) {
                            Text("No entries yet.", color = Color.Gray)
                        } else {
                            list.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Text(
                                        text = item,
                                        modifier = Modifier.padding(start = 8.dp),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(onClick = {
                                            inputValue = item
                                            oldValue = item
                                            dialogSection = title
                                            isEditDialog = true
                                            showDialog = true
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                                        }
                                        IconButton(onClick = {
                                            viewModel.deleteItem(dialogSection, item)
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Deleted: $item")
                                            }
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(if (isEditDialog) "Edit $dialogSection" else "Add $dialogSection")
            },
            text = {
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    label = { Text(dialogSection) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (inputValue.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Input cannot be empty.")
                        }
                        return@TextButton
                    }

                    val exists = viewModel.sectionList(dialogSection).contains(inputValue)
                    if (!isEditDialog && exists) {
                        scope.launch {
                            snackbarHostState.showSnackbar("This value already exists.")
                        }
                        return@TextButton
                    }

                    if (isEditDialog) {
                        viewModel.updateItem(dialogSection, oldValue, inputValue)
                        scope.launch {
                            snackbarHostState.showSnackbar("Updated $dialogSection.")
                        }
                    } else {
                        viewModel.addItem(dialogSection, inputValue)
                        scope.launch {
                            snackbarHostState.showSnackbar("Added to $dialogSection.")
                        }
                    }

                    showDialog = false
                }) {
                    Text(if (isEditDialog) "Save" else "Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}