package com.example.eduvod.ui.screens.usermanagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.viewmodel.UserManagementViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    navController: NavHostController,
    viewModel: UserManagementViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var page by remember { mutableStateOf(0) }
    val pageSize = 10

    val filteredAdmins = viewModel.admins.filter {
        it.email.contains(searchQuery, ignoreCase = true)
    }.sortedBy { it.isBlocked }

    val pagedAdmins = filteredAdmins.drop(page * pageSize).take(pageSize)
    val hasNextPage = (page + 1) * pageSize < filteredAdmins.size
    val hasPrevPage = page > 0

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1))
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
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by Email") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (pagedAdmins.isEmpty()) {
                Text("No admins found.", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    itemsIndexed(pagedAdmins) { _, admin ->
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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = MaterialTheme.shapes.medium,
                                            color = Color(0xFF0D47A1),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = admin.email.first().uppercase(),
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(admin.email, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = if (admin.isBlocked) "Blocked" else "Active",
                                                color = if (admin.isBlocked) Color.Red else Color(0xFF2E7D32),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(onClick = {
                                            viewModel.toggleBlock(admin)
                                        }) {
                                            Icon(
                                                imageVector = if (admin.isBlocked) Icons.Default.LockOpen else Icons.Default.Block,
                                                tint = Color.Red,
                                                contentDescription = "Block/Unblock"
                                            )
                                        }

                                        IconButton(onClick = {
                                            viewModel.resetPassword(admin.email)
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                tint = Color(0xFF2E7D32),
                                                contentDescription = "Reset Password"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { if (hasPrevPage) page-- }) {
                        Text("Previous")
                    }
                    Text("Page ${page + 1}")
                    TextButton(onClick = { if (hasNextPage) page++ }) {
                        Text("Next")
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddAdminDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { email, password ->
                viewModel.addAdmin(email, password)
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
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val isValidPassword = password.length >= 6 && password.any { it.isDigit() } && password.any { it.isLetter() }
    val passwordsMatch = password == confirmPassword

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
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle Password Visibility"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (!isValidPassword && password.isNotEmpty()) {
                    Text("Password must be at least 6 characters and include letters and numbers.", color = Color.Red)
                }
                if (!passwordsMatch && confirmPassword.isNotEmpty()) {
                    Text("Passwords do not match.", color = Color.Red)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (email.isNotBlank() && passwordsMatch && isValidPassword) {
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