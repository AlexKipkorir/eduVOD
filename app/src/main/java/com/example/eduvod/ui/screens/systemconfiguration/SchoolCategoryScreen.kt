package com.example.eduvod.ui.screens.systemconfiguration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.model.SimpleItem
import com.example.eduvod.ui.screens.SectionTitle
import com.example.eduvod.viewmodel.SystemConfigViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolCategoryScreen(
    viewModel: SystemConfigViewModel = viewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavHostController
) {
    val categories by remember { derivedStateOf { viewModel.categories } }
    var newCategory by remember { mutableStateOf("") }
    var editModeId by remember { mutableStateOf<Int?>(null) }
    var editText by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("School Categories", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1))
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {

            // Main content
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SectionTitle(title = "Add New Category")

                OutlinedTextField(
                    value = newCategory,
                    onValueChange = { newCategory = it },
                    placeholder = { Text("e.g., Private, Government") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (newCategory.isBlank()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Category name cannot be empty.")
                            }
                        } else {
                            viewModel.addItem("School Category", newCategory.trim())
                            newCategory = ""
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Add Category")
                }

                Divider()

                SectionTitle(title = "Existing Categories")

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(categories.size) { index ->
                        val item = categories[index]

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (editModeId == item.id) {
                                    OutlinedTextField(
                                        value = editText,
                                        onValueChange = { editText = it },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    TextButton(onClick = {
                                        if (editText.isNotBlank()) {
                                            val indexToUpdate = viewModel.categories.indexOfFirst { it.id == item.id }
                                            if (indexToUpdate != -1) {
                                                viewModel.categories[indexToUpdate] = SimpleItem(item.id, editText.trim())
                                            }
                                            editModeId = null
                                            editText = ""
                                        } else {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Category name cannot be empty.")
                                            }
                                        }
                                    }) {
                                        Text("Save")
                                    }

                                    TextButton(onClick = {
                                        editModeId = null
                                        editText = ""
                                    }) {
                                        Text("Cancel")
                                    }
                                } else {
                                    Text(item.name, style = MaterialTheme.typography.bodyLarge)

                                    IconButton(onClick = {
                                        editModeId = item.id
                                        editText = item.name
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Loader overlay
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
                        Text("Loading categories...", color = Color(0xFF1565C0))
                    }
                }
            }
        }
    }
}

