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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.viewmodel.SchoolManagementViewModel
import kotlinx.coroutines.launch

data class AdminAccount(
    val  email: String,
    var isBlocked: Boolean = false
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSchoolAdminsScreen(
    navController: NavHostController,
    schoolName: String?,
    viewModel: SchoolManagementViewModel = viewModel()
) {

    val admins = remember {
        mutableStateListOf(
            AdminAccount("john.doe@school.org"),
            AdminAccount("mary.jane@school.org")
        )
    }
    var showAddDialog by remember { mutableStateOf(false) }
    var newAdminEmail by remember { mutableStateOf("") }

    val snackbarHostState= remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val currentSchool = viewModel.getSchoolByName(schoolName ?: "")

    var newAdminPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var adminAssigned by remember { mutableStateOf(currentSchool?.hasAdmin == true) }

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
            if (!adminAssigned) {
                ExtendedFloatingActionButton(
                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
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
                items(admins) { admin ->
                    AdminCard(
                        admin = admin,
                        onBlock = {
                            admin.isBlocked = !admin.isBlocked
                        },
                        onReset = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Password reset for ${admin.email}")
                            }
                        }
                    )

                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Create Admin Account") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newAdminEmail,
                        onValueChange = { newAdminEmail = it },
                        label = { Text("Admin Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAdminPassword,
                        onValueChange = { newAdminPassword = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    when {
                        newAdminEmail.isBlank() || newAdminPassword.isBlank() || confirmPassword.isBlank() -> {
                            scope.launch {
                                snackbarHostState.showSnackbar("All fields are required.")
                            }
                        }
                        newAdminPassword != confirmPassword -> {
                            scope.launch {
                                snackbarHostState.showSnackbar("Passwords do not match.")
                            }
                        }
                        else -> {
                            admins.add(AdminAccount(email = newAdminEmail))
                            viewModel.assignAdmin(schoolName?: "")
                            adminAssigned = true
                            showAddDialog = false
                            scope.launch {
                                snackbarHostState.showSnackbar("Admin created successfully")
                            }
                        }
                    }
                }) {
                    Text("Add Admin")
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
fun AdminCard(
    admin: AdminAccount,
    onBlock: () -> Unit,
    onReset: () -> Unit
) {
    var isBlocked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (admin.isBlocked) Color(0xFFFFEBEE) else Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(admin.email, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        onReset()
                        //Simulate logic
                        println("Reset admin password")
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset Password")
                }

                OutlinedButton(
                    onClick = {
                        isBlocked = !isBlocked
                        onBlock()
                    }
                ) {
                    Icon(Icons.Default.Block, contentDescription = "Block")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (admin.isBlocked) "Unblock" else "Block")
                }
            }
        }
    }
}
