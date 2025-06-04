package com.example.eduvod.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemConfigScreen(navController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogLabel by remember { mutableStateOf("") }
    var currentSelection by remember { mutableStateOf("") }

    val types = remember { mutableStateListOf("Primary", "Secondary") }
    val categories = remember { mutableStateListOf("Public", "Private") }
    val curriculums = remember { mutableStateListOf("CBC", "British", "8-4-4") }

    val sections = listOf(
        "School Type" to types,
        "School Category" to categories,
        "Curriculum" to curriculums
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("System Configuration", color = MaterialTheme.colorScheme.onPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add")},
                onClick = { showDialog = true }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            sections.forEach { (title, list) ->
                item {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(list) { entry ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = entry,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                item{
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            currentSelection = title
                            showDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add $title")
                    }
                }
            }
        }
    }

    if (showDialog) {
        var newEntry by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add $currentSelection")},
            text = {
                OutlinedTextField(
                    value = newEntry,
                    onValueChange = { newEntry = it },
                    label = { Text("New $currentSelection") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    when (currentSelection) {
                        "School Type" -> types.add(newEntry)
                        "School Category" -> categories.add(newEntry)
                        "Curriculum" -> curriculums.add(newEntry)
                    }
                    showDialog = false
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false}) {
                    Text("Cancel")
                }
            }
        )
    }
}