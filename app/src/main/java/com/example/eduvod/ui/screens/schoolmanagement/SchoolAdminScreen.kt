package com.example.eduvod.ui.screens.schoolmanagement

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eduvod.ui.theme.responsiveFontSize
import com.example.eduvod.viewmodel.SchoolAdmin
import com.example.eduvod.viewmodel.SchoolManagementViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState



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

    // Loading states
    var isAddingAdmin by remember { mutableStateOf(false) }
    var isAssigning by remember { mutableStateOf(false) }
    var isUnassigning by remember { mutableStateOf(false) }
    var loadingAdminEmail by remember { mutableStateOf<String?>(null) }
    var isDeleting by remember { mutableStateOf(false) }
    val isLoading = viewModel.isLoading.value

    val filteredAdmins = viewModel.schoolAdmins.filter {
        when (filter) {
            "Assigned" -> it.schoolName != null
            "Unassigned" -> it.schoolName == null
            else -> true
        }
    }.filter { it.email.contains(searchQuery, ignoreCase = true) }

    LaunchedEffect(viewModel.snackbarMessage.collectAsState().value) {
        viewModel.snackbarMessage.value?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            viewModel.clearSnackbarMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "School Administrators",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = responsiveFontSize(20f)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    modifier = Modifier.background(
                        Brush.verticalGradient(listOf(Color(0xFF1565C0), Color(0xFF0D47A1)))
                    )
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
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text("View and assign admins here", style = MaterialTheme.typography.bodyMedium)
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
                Text("Registered Admins", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredAdmins) { admin ->
                        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(admin.email, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1565C0))
                                }

                                Text(
                                    text = admin.schoolName?.let { "Assigned to: $it" } ?: "Unassigned",
                                    color = if (admin.schoolName != null) Color(0xFF2E7D32) else Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (admin.schoolName != null) {
                                        Button(
                                            onClick = {
                                                loadingAdminEmail = admin.email
                                                isUnassigning = true
                                                viewModel.unassignAdmin(
                                                    admin.email,
                                                    onDone = {
                                                        loadingAdminEmail = null
                                                        isUnassigning = false
                                                    }
                                                )
                                            },
                                            enabled = loadingAdminEmail != admin.email && !isUnassigning
                                        ) {
                                            if (loadingAdminEmail == admin.email && isUnassigning) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .padding(end = 8.dp),
                                                    strokeWidth = 2.dp,
                                                    color = Color.White
                                                )
                                                Text("Unassigning...")
                                            } else {
                                                Icon(Icons.Default.Clear, contentDescription = null)
                                                Text("Unassign")
                                            }
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                selectedAdmin = admin
                                                selectedSchool = ""
                                                showReassignDialog = true
                                            },
                                            enabled = loadingAdminEmail != admin.email
                                        ) {
                                            Icon(Icons.Default.PersonAdd, contentDescription = null)
                                            Text("Assign")
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            adminToDelete = admin
                                            showDeleteDialog = true
                                        },
                                        enabled = loadingAdminEmail != admin.email
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Admin", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Loading overlay
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
                    Text("Loading admins...", color = Color(0xFF1565C0))
                }
            }
        }
    }

    // Add Admin Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { if (!isAddingAdmin) showAddDialog = false },
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
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        when {
                            usernameInput.isBlank() || emailInput.isBlank() -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Username and Email are required.")
                                }
                            }
                            !Patterns.EMAIL_ADDRESS.matcher(emailInput).matches() -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please enter a valid email address.")
                                }
                            }
                            else -> {
                                isAddingAdmin = true
                                scope.launch {
                                    val added = viewModel.addAdmin(
                                        username = usernameInput.trim(),
                                        email = emailInput.trim()
                                    )
                                    snackbarHostState.showSnackbar(
                                        if (added) "Admin added. Email with temporary password sent." else "Admin already exists or failed."
                                    )
                                    if (added) {
                                        usernameInput = ""
                                        emailInput = ""
                                        showAddDialog = false
                                    }
                                    isAddingAdmin = false
                                }
                            }
                        }
                    },
                    enabled = !isAddingAdmin
                ) {
                    if (isAddingAdmin) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 8.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text("Adding...")
                    } else {
                        Text("Add")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddDialog = false },
                    enabled = !isAddingAdmin
                ) {
                    Text("Cancel")
                }
            }
        )
    }


    // Assign Admin Dialog
    if (showReassignDialog && selectedAdmin != null) {
        AlertDialog(
            onDismissRequest = {
                if (!isAssigning) {
                    showReassignDialog = false
                    selectedSchool = ""
                    selectedAdmin = null
                }
            },
            title = { Text("Assign Admin") },
            text = {
                Column {
                    Text("Assign ${selectedAdmin!!.email} to which school?")
                    Spacer(modifier = Modifier.height(8.dp))

                    val unassignedSchools = viewModel.schools.filter { school ->
                        viewModel.schoolAdmins.none { it.schoolName == school.name }
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
                        if (selectedSchool.isNotBlank() && selectedAdmin != null) {
                            isAssigning = true
                            loadingAdminEmail = selectedAdmin!!.email
                            scope.launch {
                                viewModel.assignAdminToSchool(
                                    selectedAdmin!!.email,
                                    selectedSchool,
                                    onDone = {
                                        isAssigning = false
                                        loadingAdminEmail = null
                                    }
                                )
                                selectedSchool = ""
                                selectedAdmin = null
                                showReassignDialog = false
                            }
                        }
                    },
                    enabled = selectedSchool.isNotBlank() && !isAssigning
                ) {
                    if (isAssigning) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 8.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text("Assigning...")
                    } else {
                        Text("Assign")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        selectedSchool = ""
                        selectedAdmin = null
                        showReassignDialog = false
                    },
                    enabled = !isAssigning
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Admin Dialog
    if (showDeleteDialog && adminToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) {
                    showDeleteDialog = false
                    adminToDelete = null
                }
            },
            title = { Text("Delete Admin") },
            text = { Text("Are you sure you want to delete ${adminToDelete!!.email}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        isDeleting = true
                        loadingAdminEmail = adminToDelete!!.email
                        viewModel.deleteAdmin(adminToDelete!!.email)
                        adminToDelete = null
                        showDeleteDialog = false
                        isDeleting = false
                        loadingAdminEmail = null
                    },
                    enabled = !isDeleting
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 8.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text("Deleting...")
                    } else {
                        Text("Delete", color = Color.Red)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        adminToDelete = null
                        showDeleteDialog = false
                    },
                    enabled = !isDeleting
                ) {
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
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        )

        // Removed animation block — replaced with plain conditional rendering
        if (expanded) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(options) { option ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelected(option)
                                    expanded = false
                                }
                                .background(
                                    if (option == selectedOption) {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    } else {
                                        Color.Transparent
                                    }
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = option,
                                color = if (option == selectedOption) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}

