package com.example.eduvod.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eduvod.viewmodel.SchoolAdmin
import com.example.eduvod.viewmodel.SchoolManagementViewModel
import kotlinx.coroutines.launch
import kotlin.math.exp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolAdminsScreen(
    navController: NavHostController,
    viewModel: SchoolManagementViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("") }

    var searchQuery by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("ALL") }
    val filterOptions = listOf("ALL", "Assigned", "Unassigned")
    var showReassignDialog by remember { mutableStateOf(false) }
    var selectedSchool by remember { mutableStateOf("") }

    var selectedAdmin by remember { mutableStateOf<SchoolAdmin?>(null) }

    val filteredAdmins = viewModel.schoolAdmins.filter {
        (filter == "ALL") || (filter == "Assigned" && it.assignedSchool != null) || (filter == "Unassigned" && it.assignedSchool == null)
    }.filter {
        it.email.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("School Administrators", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
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
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery=it },
                label = { Text("Search Admins") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
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
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Registered Admins", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredAdmins) { admin ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(18.dp))
                            Text(admin.email, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                text = admin.assignedSchool?.let { "Assigned to: $it" } ?: "Unassigned",
                                color = if (admin.assignedSchool != null) Color(0xFF2E7D32) else Color.Gray,
                                fontSize = 14.sp
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                if(admin.assignedSchool != null) {
//                                    Text("Assigned to ${admin.assignedSchool}", fontSize = 13.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Button(onClick = { viewModel.unassignAdmin(admin.email) }) {
                                        Icon(Icons.Default.Clear, contentDescription = null)
                                        Text("Unassign")
                                    }
                                } else {
                                    Button(onClick = {
                                        showReassignDialog = true
                                        selectedAdmin = admin
                                    }) {
                                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                                        Text("Assign")
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
            title = { Text("Add Admin") },
            text = {
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

            },
            confirmButton = {
                TextButton(onClick = {
                    if (emailInput.isNotBlank()) {
                        val added = viewModel.addAdmin(emailInput)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (added) "Admin added successfully." else "Admin already exists."
                            )
                        }
                        if (added) emailInput = ""
                        showDialog = false
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
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
                                selectedSchool = ""
                            }
                        }
                        showReassignDialog = false
                    },
                    enabled = selectedSchool.isNotBlank()
                ) {
                    Text("Assign")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showReassignDialog = false
                    selectedSchool = ""
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

    Column {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text("Selected School") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
}