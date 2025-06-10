package com.example.eduvod.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.viewmodel.SchoolManagementViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSchoolAdminsScreen(
    navController: NavHostController,
    schoolName: String?,
    viewModel: SchoolManagementViewModel = viewModel()
) {
    val admins = remember { mutableStateListOf("john.doe@school.org", "mary.jane@School.org")}
    var showAddDialog by remember { mutableStateOf(false) }
    var newAdminEmail by remember { mutableStateOf("") }

    val snackbarHostState= remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val currentSchool = viewModel.getSchoolByName(schoolName ?: "")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text("Admins - ${schoolName ?: "Unknown"}")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack()}) {
                       Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentSchool?.hasAdmin == false) {
                ExtendedFloatingActionButton(
                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Add Admin") },
                    text = { Text("Add Admin") },
                    onClick = { showAddDialog = true }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Managing admins for ${schoolName ?: "school"}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(admins) { email ->
                    AdminCard(
                        email = email,
                        onBlock = { admins.remove(email) },
                        onReset = {
                            //TODO: Reset admin account
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Admin") },
            text = {
                OutlinedTextField(
                    value = newAdminEmail,
                    onValueChange = { newAdminEmail = it },
                    label = { Text("Admin Email") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newAdminEmail.isNotBlank()) {
                        if (viewModel.getSchoolByName(schoolName?: "")?.hasAdmin == true){
                            scope.launch {
                                snackbarHostState.showSnackbar("Admin already assigned for this school.")
                            }
                            showAddDialog = false
                            return@TextButton
                        }

                        admins.add(newAdminEmail)
                        viewModel.assignAdmin(schoolName ?: "")
                        showAddDialog = false
                        newAdminEmail = ""

                        scope.launch {
                            snackbarHostState.showSnackbar("Admin successfully assigned.")
                        }

                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminCard(email: String, onBlock: () -> Unit, onReset: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(email, fontSize = 16.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onReset) {
                    Icon(Icons.Default.Block, contentDescription = "Block")
                }
            }
        }
    }
}
