package com.example.eduvod.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.viewmodel.UserManagementViewModel
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class AdminUser(
    val email: String,
    val isBlocked: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    navController: NavHostController,
    viewModel: UserManagementViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    val admins = viewModel.admins

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EduVOD User Management", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1) )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Admin") },
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                onClick = { showAddDialog = true }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF4F9FC)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Registered Administrators",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                    color = Color(0xFF0D47A1)
                )
            }
            items(admins) { admin ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(admin.email, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (admin.isBlocked) "Status: Blocked" else "Status: Active",
                                    color = if (admin.isBlocked) Color.Red else Color(0xFF2E7D32),
                                    fontSize = 14.sp
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(onClick = {
                                    viewModel.toggleBlock(admin)
                                }) {
                                    Icon(
                                        imageVector = if (admin.isBlocked) Icons.Default.LockOpen else Icons.Default.Block,
                                        contentDescription = "Block/Unblock"
                                    )
                                }

                                IconButton(onClick = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Password reset link sent.")
                                    }
                                }) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Reset Account")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddAdminDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { email, password ->
                val added = viewModel.addAdmin(email)
                scope.launch {
                    snackbarHostState.showSnackbar(
                        if (added) "Admin added successfully." else "Admin already exists"
                    )
                }
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddAdminDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add EduVOD Admin") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(icon, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (email.isNotBlank() && password == confirmPassword && password.isNotBlank()) {
                    onConfirm(email, password)
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}