package com.example.eduvod.ui.screens.schoolmanagement

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eduvod.viewmodel.SchoolAdmin
import com.example.eduvod.viewmodel.SchoolManagementViewModel
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
    var isSearchExpanded by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf("ALL") }
    var isFilterExpanded by remember { mutableStateOf(false) }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with back button and title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.popBackStack() },
                tint = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "School Administrators",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = (-0.015).sp
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Search and Filter icons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { isSearchExpanded = true },
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { isFilterExpanded = true },
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Section title with admin count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All Admins (${filteredAdmins.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
        }

        // Search Bar
        if (isSearchExpanded) {
            SearchBar(
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { isSearchExpanded = false },
                onClose = {
                    searchQuery = ""
                    isSearchExpanded = false
                }
            )
        }

        // Filter Options (appears when expanded)
        if (isFilterExpanded) {
            AdminFilterOptions(
                selectedFilter = when(filter) {
                    "Assigned" -> AdminFilterOption.ASSIGNED
                    "Unassigned" -> AdminFilterOption.UNASSIGNED
                    else -> null
                },
                onFilterSelected = { option ->
                    filter = when(option) {
                        AdminFilterOption.ASSIGNED -> "Assigned"
                        AdminFilterOption.UNASSIGNED -> "Unassigned"
                    }
                    isFilterExpanded = false
                },
                onClose = { isFilterExpanded = false }
            )
        }

        // Show active filter chip if any
        if (filter != "ALL") {
            FilterChip(
                selected = true,
                onClick = { filter = "ALL" },
                label = { Text(
                    when(filter) {
                        "Assigned" -> "Assigned"
                        "Unassigned" -> "Unassigned"
                        else -> ""
                    }
                ) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Clear filter",
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Admin list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(filteredAdmins) { admin ->
                AdminListItem(
                    admin = admin,
                    onAssignClick = {
                        selectedAdmin = admin
                        selectedSchool = ""
                        showReassignDialog = true
                    },
                    onUnassignClick = {
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
                    onDeleteClick = {
                        adminToDelete = admin
                        showDeleteDialog = true
                    },
                    isLoading = loadingAdminEmail == admin.email && (isUnassigning || isAssigning || isDeleting)
                )
            }
        }
    }

    // Floating Action Button
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Admin")
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
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading admins...", color = MaterialTheme.colorScheme.primary)
            }
        }
    }

    // Add Admin Dialog
    if (showAddDialog) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { if (!isAddingAdmin) showAddDialog = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Add New Admin",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = usernameInput,
                    onValueChange = { usernameInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    placeholder = { Text("Username") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
                )

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    placeholder = { Text("Email") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
                )

                Button(
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
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
                        Text("Add Admin", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Assign Admin Dialog
    if (showReassignDialog && selectedAdmin != null) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = {
                if (!isAssigning) {
                    showReassignDialog = false
                    selectedSchool = ""
                    selectedAdmin = null
                }
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Assign Admin",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text("Assign ${selectedAdmin!!.email} to which school?",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 12.dp))

                val unassignedSchools = viewModel.schools.filter { school ->
                    viewModel.schoolAdmins.none { it.schoolName == school.name }
                }

                SchoolDropdownField(
                    label = "Select School",
                    options = unassignedSchools.map { it.name },
                    selectedOption = selectedSchool,
                    onSelected = { selectedSchool = it }
                )

                Button(
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
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
                        Text("Assign", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
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
                        Text("Delete", color = MaterialTheme.colorScheme.error)
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
private fun AdminListItem(
    admin: SchoolAdmin,
    onAssignClick: () -> Unit,
    onUnassignClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Admin info
            Column(
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = admin.email,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = admin.username,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = admin.schoolName ?: "Unassigned",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (admin.schoolName != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (admin.schoolName != null) {
                        Button(
                            onClick = onUnassignClick,
                            modifier = Modifier.width(100.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text("Unassign")
                            }
                        }
                    } else {
                        Button(
                            onClick = onAssignClick,
                            modifier = Modifier.width(84.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            enabled = !isLoading
                        ) {
                            Text("Assign")
                        }
                    }

                    Button(
                        onClick = onDeleteClick,
                        modifier = Modifier.width(84.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        enabled = !isLoading
                    ) {
                        Text("Delete")
                    }
                }
            }

            // Admin icon
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Admin",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Search admins...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        modifier = Modifier.clickable { onClose() }
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun AdminFilterOptions(
    selectedFilter: AdminFilterOption?,
    onFilterSelected: (AdminFilterOption) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filter Admins",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onClose() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AdminFilterOption.entries.forEach { filter ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFilterSelected(filter) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedFilter == filter,
                    onClick = { onFilterSelected(filter) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = filter.displayName,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

enum class AdminFilterOption(val displayName: String) {
    ASSIGNED("Assigned"),
    UNASSIGNED("Unassigned")
}

//Preview Screen
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SchoolAdminsScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column {
                // Header with back button and title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 16.dp, 16.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { },
                        tint = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "School Administrators",
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            letterSpacing = (-0.015).sp
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Search and Filter icons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { },
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { },
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                // Section title with admin count
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 16.dp, 16.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "All Admins (3)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Filter chip (example)
                FilterChip(
                    selected = true,
                    onClick = { },
                    label = { Text("Assigned") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear filter",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Sample admin list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(getMockAdmins()) { admin ->
                        AdminListItemPreview(admin = admin)
                    }
                }
            }

            // Floating Action Button
            FloatingActionButton(
                onClick = { },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Admin")
            }
        }
    }
}

@Composable
private fun AdminListItemPreview(admin: MockAdmin) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Admin info
            Column(
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = admin.email,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = admin.username,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = admin.schoolName ?: "Unassigned",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (admin.schoolName != null) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (admin.schoolName != null) {
                        Button(
                            onClick = { },
                            modifier = Modifier.width(100.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text("Unassign")
                        }
                    } else {
                        Button(
                            onClick = { },
                            modifier = Modifier.width(84.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text("Assign")
                        }
                    }

                    Button(
                        onClick = { },
                        modifier = Modifier.width(84.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text("Delete")
                    }
                }
            }

            // Admin icon
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Admin",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class MockAdmin(
    val email: String,
    val username: String,
    val schoolName: String?
)

private fun getMockAdmins(): List<MockAdmin> {
    return listOf(
        MockAdmin("admin1@school.com", "Admin One", "Greenwood High"),
        MockAdmin("admin2@school.com", "Admin Two", "Sunrise Academy"),
        MockAdmin("unassigned@school.com", "Unassigned Admin", null)
    )
}