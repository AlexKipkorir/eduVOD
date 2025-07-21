package com.example.eduvod.ui.screens.schoolmanagement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.eduvod.ui.screens.AppScaffold
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
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var showConfirmUnassign by remember { mutableStateOf(false) }
    var selectedAdminEmail by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var isAssigning by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading
    val currentSchool = viewModel.getSchoolByName(schoolName ?: "")
    val allAdmins = viewModel.schoolAdmins.filter {
        it.schoolName == schoolName && it.email.contains(searchQuery, ignoreCase = true)
    }
    val unassignedAdmins = viewModel.getUnassignedAdmins()

    AppScaffold(
        title = "Admins - ${schoolName ?: "Unknown"}",
        snackbarHostState = snackbarHostState,
        showTopBar = true,
        showBackButton = true,
        onBack = { navController.popBackStack() }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Managing admins for ${schoolName ?: "school"}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Admins") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(allAdmins) { admin ->
                        AdminCard(
                            admin = AdminAccount(
                                email = admin.email,
                                isBlocked = admin.status != "ACTIVE"
                            ),
                            onBlock = {
                                val shouldBlock = admin.status == "ACTIVE"
                                viewModel.blockAdmin(admin.email, shouldBlock)
                            },
                            onReset = {
                                viewModel.resetAdmin(admin.email)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Password reset for ${admin.email}")
                                }
                            }
                        )
                    }
                }
            }
        }

        // Floating action button
        Box(modifier = Modifier
            .padding(end = 24.dp, bottom = 24.dp)
            .fillMaxSize(), contentAlignment = Alignment.BottomEnd
        ) {
            ExtendedFloatingActionButton(
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Add Admin") },
                text = { Text("Add Admin") },
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF1565C0),
                contentColor = Color.White
            )
        }
    }

    // Assign Admin Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                selectedAdminEmail = ""
            },
            title = { Text("Assign Admin", fontWeight = FontWeight.SemiBold) },
            text = {
                if (unassignedAdmins.isEmpty()) {
                    Text("No unassigned admins available.")
                } else {
                    var expanded by remember { mutableStateOf(false) }

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = selectedAdminEmail,
                            onValueChange = {},
                            label = { Text("Select Admin") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            unassignedAdmins.forEach { email ->
                                DropdownMenuItem(
                                    text = { Text(email) },
                                    onClick = {
                                        selectedAdminEmail = email
                                        expanded = false
                                    }
                                )
                            }
                        }

                        if (isAssigning) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (selectedAdminEmail.isNotBlank()) {
                            scope.launch {
                                isAssigning = true
                                viewModel.assignAdminToSchool(selectedAdminEmail, schoolName ?: "")
                                viewModel.fetchAdmins()
                                snackbarHostState.showSnackbar("Admin assigned successfully.")
                                isAssigning = false
                                showAddDialog = false
                                selectedAdminEmail = ""
                            }
                        }
                    },
                    enabled = selectedAdminEmail.isNotBlank() && !isAssigning
                ) {
                    Text("Assign")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    selectedAdminEmail = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Unassign Confirmation Dialog
    if (showConfirmUnassign && selectedAdminEmail.isNotBlank()) {
        AlertDialog(
            onDismissRequest = {
                showConfirmUnassign = false
                selectedAdminEmail = ""
            },
            title = { Text("Confirm Unassign", fontWeight = FontWeight.SemiBold) },
            text = { Text("Are you sure you want to unassign this admin from $schoolName?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        viewModel.setLoading(true)
                        viewModel.unassignAdmin(selectedAdminEmail)
                        viewModel.fetchAdmins()
                        viewModel.setLoading(false)
                        snackbarHostState.showSnackbar("Admin unassigned")
                        showConfirmUnassign = false
                        selectedAdminEmail = ""
                    }
                }) {
                    Text("Yes", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showConfirmUnassign = false
                    selectedAdminEmail = ""
                }) {
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (admin.isBlocked) Color(0xFFFFEBEE) else Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.Person, contentDescription = null)
            Text(admin.email, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onReset
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset Password")
                }

                OutlinedButton(
                    onClick = onBlock
                ) {
                    Icon(Icons.Default.Block, contentDescription = "Block")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (admin.isBlocked) "Unblock" else "Block")
                }
            }
        }
    }
}

