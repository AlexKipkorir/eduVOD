package com.example.eduvod.ui.screens.schoolmanagement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eduvod.viewmodel.SchoolAdmin
import com.example.eduvod.viewmodel.SchoolManagementViewModel
import android.util.Patterns
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolAdminsScreen(
    navController: NavHostController,
    viewModel: SchoolManagementViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("") }

    var searchQuery by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("ALL") }
    val filterOptions = listOf("ALL", "Assigned", "Unassigned")

    var showReassignDialog by remember { mutableStateOf(false) }
    var selectedSchool by remember { mutableStateOf("") }
    var selectedAdmin by remember { mutableStateOf<SchoolAdmin?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var adminToDelete by remember { mutableStateOf<SchoolAdmin?>(null) }

    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }


    val filteredAdmins = viewModel.schoolAdmins.filter {
        when (filter) {
            "Assigned" -> it.assignedSchool != null
            "Unassigned" -> it.assignedSchool == null
            else -> true
        }
    }.filter { it.email.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("School Administrators", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF1565C0),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF4F9FC)
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {

            Text("View and assign admins here", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Admins") },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilterDropdown(
                label = "Filter",
                options = filterOptions,
                selectedOption = filter,
                onSelected = { filter = it }
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("Registered Admins", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredAdmins) { admin ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(admin.email, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1565C0))
                            }

                            Text(
                                text = admin.assignedSchool?.let { "Assigned to: $it" } ?: "Unassigned",
                                color = if (admin.assignedSchool != null) Color(0xFF2E7D32) else Color.Gray,
                                fontSize = 14.sp
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                if (admin.assignedSchool != null) {
                                    Button(onClick = {
                                        viewModel.unassignAdmin(admin.email)
                                        scope.launch { snackbarHostState.showSnackbar("Admin unassigned") }
                                    }) {
                                        Icon(Icons.Default.Clear, contentDescription = null)
                                        Text("Unassign")
                                    }
                                } else {
                                    Button(onClick = {
                                        selectedAdmin = admin
                                        selectedSchool = ""
                                        showReassignDialog = true
                                    }) {
                                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                                        Text("Assign")
                                    }
                                }

                                IconButton(onClick = {
                                    adminToDelete = admin
                                    showDeleteDialog = true
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Admin", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Admin") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { usernameInput = it },
                        label = { Text("Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password (min 6 characters)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmPasswordInput,
                        onValueChange = { confirmPasswordInput = it },
                        label = { Text("Confirm Password") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    when {
                        usernameInput.isBlank() ||
                                emailInput.isBlank() ||
                                passwordInput.isBlank() ||
                                confirmPasswordInput.isBlank() -> {
                            scope.launch {
                                snackbarHostState.showSnackbar("All fields are required.")
                            }
                        }

                        !Patterns.EMAIL_ADDRESS.matcher(emailInput).matches() -> {
                            scope.launch {
                                snackbarHostState.showSnackbar("Please enter a valid email address.")
                            }
                        }

                        passwordInput.length < 6 -> {
                            scope.launch {
                                snackbarHostState.showSnackbar("Password must be at least 6 characters.")
                            }
                        }

                        passwordInput != confirmPasswordInput -> {
                            scope.launch {
                                snackbarHostState.showSnackbar("Passwords do not match.")
                            }
                        }

                        else -> {
                            val added = viewModel.addAdmin(
                                username = usernameInput.trim(),
                                email = emailInput.trim(),
                                password = passwordInput,
                                schoolId = "" // Update if assigning directly
                            )
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (added) "Admin added successfully." else "Admin already exists or failed."
                                )
                            }
                            if (added) {
                                usernameInput = ""
                                emailInput = ""
                                passwordInput = ""
                                confirmPasswordInput = ""
                            }
                            showAddDialog = false
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


    if (showReassignDialog && selectedAdmin != null) {
        AlertDialog(
            onDismissRequest = {
                showReassignDialog = false
                selectedSchool = ""
                selectedAdmin = null
            },
            title = { Text("Assign Admin") },
            text = {
                Column {
                    Text("Assign ${selectedAdmin!!.email} to which school?")
                    Spacer(modifier = Modifier.height(8.dp))

                    val unassignedSchools = viewModel.schools.filter { school ->
                        viewModel.schoolAdmins.none { it.assignedSchool == school.name }
                    }

                    SchoolDropdown(
                        label = "Select School",
                        options = unassignedSchools.map { it.name },
                        selectedOption = selectedSchool,
                        onSelected = { selectedSchool = it }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedAdmin?.let {
                            viewModel.reassignAdmin(it.email, selectedSchool)
                            scope.launch {
                                snackbarHostState.showSnackbar("Assigned to $selectedSchool")
                            }
                        }
                        selectedSchool = ""
                        selectedAdmin = null
                        showReassignDialog = false
                    },
                    enabled = selectedSchool.isNotBlank()
                ) {
                    Text("Assign")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectedSchool = ""
                    selectedAdmin = null
                    showReassignDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog && adminToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                adminToDelete = null
            },
            title = { Text("Delete Admin") },
            text = { Text("Are you sure you want to delete ${adminToDelete!!.email}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAdmin(adminToDelete!!.email)
                    scope.launch {
                        snackbarHostState.showSnackbar("Admin deleted successfully.")
                    }
                    adminToDelete = null
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    adminToDelete = null
                    showDeleteDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
@Composable
fun SchoolDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedOption,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }
    )
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                text = { Text(option) },
                onClick = {
                    onSelected(option)
                    expanded = false
                }
            )
        }
    }
}
