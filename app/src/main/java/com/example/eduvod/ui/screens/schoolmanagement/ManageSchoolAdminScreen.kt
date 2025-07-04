package com.example.eduvod.ui.screens.schoolmanagement

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.viewmodel.SchoolAdmin
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

    val admins = viewModel.schoolAdmins

    val allAdmins = viewModel.schoolAdmins.filter { it.assignedSchool == schoolName }

    var showAddDialog by remember { mutableStateOf(false) }

    val snackbarHostState= remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val currentSchool = viewModel.getSchoolByName(schoolName ?: "")

    val unassignedAdmins = viewModel.getUnassignedAdmins()
    var selectedAdminEmail by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Admins - ${schoolName ?: "Unknown"}",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack()}) {
                       Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xff0D47A1))
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Add Admin") },
                onClick = { showAddDialog = true }
            )
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
                //OG
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
                //Retrofit
//                items(allAdmins) { admin ->
//                    AdminCard(
//                        admin = AdminAccount(admin.email, admin.isBlocked),
//                        onBlock = {
//                            viewModel.blockAdmin(admin.email, !admin.isBlocked)
//                        },
//                        onReset = {
//                            viewModel.resetAdmin(admin.email)
//                            scope.launch {
//                                snackbarHostState.showSnackbar("Password reset for ${admin.email}")
//                            }
//                        }
//                    )
//                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Assign Admin") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (unassignedAdmins.isEmpty()) {
                        Text("No unassigned admins available.")
                    } else {
                        var expanded by remember { mutableStateOf(false) }

                        OutlinedTextField(
                            value = selectedAdminEmail,
                            onValueChange = {},
                            label = { Text("Select Admin") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { expanded = true }) {
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
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (selectedAdminEmail.isNotBlank()) {
                            viewModel.reassignAdmin(selectedAdminEmail, schoolName ?: "")
                            viewModel.assignAdmin(schoolName ?: "")
                            showAddDialog = false
                            selectedAdminEmail = ""
                            scope.launch {
                                snackbarHostState.showSnackbar("Admin assigned successfully.")
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Please select an admin.")
                            }
                        }
                    },
                    enabled = selectedAdminEmail.isNotBlank()
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
}

@Composable
fun AdminCard(
    admin: SchoolAdmin,
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

