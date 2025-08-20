package com.example.eduvod.ui.screens.usermanagement

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eduvod.ui.theme.EduVODTheme
import com.example.eduvod.viewmodel.AdminUser
import com.example.eduvod.viewmodel.UserManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    navController: NavHostController,
    viewModel: UserManagementViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val currentUserEmail by viewModel.currentUserEmail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<UserFilter?>(null) }
    var expandedMenuId by remember { mutableStateOf<String?>(null) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var isFilterExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    val admins = viewModel.admins
    val filteredAdmins = remember(admins, searchQuery, selectedFilter) {
        admins.filter { admin ->
            val matchesSearch = admin.email.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                UserFilter.ALL -> true
                UserFilter.ACTIVE -> admin.status == "ACTIVE"
                UserFilter.BLOCKED -> admin.status == "BLOCKED"
                UserFilter.DELETED -> admin.status == "DELETED"
                null -> true
            }
            matchesSearch && matchesFilter
        }
    }

    val activeAdmins = filteredAdmins.filter { it.status == "ACTIVE" }
    val blockedAdmins = filteredAdmins.filter { it.status == "BLOCKED" }
    val deletedAdmins = filteredAdmins.filter { it.status == "DELETED" }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 16.dp, 16.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF111418)
                    )
                }

                Text(
                    text = "User Management",
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF111418)
                )

                // Search and Filter icons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = { isSearchExpanded = !isSearchExpanded },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF111418)
                        )
                    }
                    IconButton(
                        onClick = { isFilterExpanded = !isFilterExpanded },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = Color(0xFF111418)
                        )
                    }
                }
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
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Filter Options
            if (isFilterExpanded) {
                FilterOptions(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { filter ->
                        selectedFilter = if (selectedFilter == filter) null else filter
                        isFilterExpanded = false
                    },
                    onClose = { isFilterExpanded = false },
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Active filter chip
            selectedFilter?.let { filter ->
                FilterChip(
                    selected = true,
                    onClick = { selectedFilter = null },
                    label = { Text(filter.displayName) },
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

            // Filter tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UserFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = if (selectedFilter == filter) null else filter },
                        label = { Text(filter.displayName) },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                // Active Admins
                if (activeAdmins.isNotEmpty()) {
                    item {
                        Text(
                            text = "Active Admins",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0xFF111418)
                        )
                    }

                    items(activeAdmins) { admin ->
                        AdminListItem(
                            admin = admin,
                            currentUserEmail = currentUserEmail,
                            expandedMenuId = expandedMenuId,
                            onOptionsClick = { expandedMenuId = admin.email },
                            onDismissMenu = { expandedMenuId = null },
                            onDelete = { viewModel.deleteUser(admin.id) },
                            onResetPassword = { viewModel.resetPassword(admin.email) },
                            onToggleStatus = { viewModel.toggleUserStatus(admin) }
                        )
                        Divider(color = Color(0xFFF0F2F5), thickness = 1.dp)
                    }
                }

                // Blocked Admins
                if (blockedAdmins.isNotEmpty()) {
                    item {
                        Text(
                            text = "Blocked Admins",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0xFF111418)
                        )
                    }

                    items(blockedAdmins) { admin ->
                        AdminListItem(
                            admin = admin,
                            currentUserEmail = currentUserEmail,
                            expandedMenuId = expandedMenuId,
                            onOptionsClick = { expandedMenuId = admin.email },
                            onDismissMenu = { expandedMenuId = null },
                            onDelete = { viewModel.deleteUser(admin.id) },
                            onResetPassword = { viewModel.resetPassword(admin.email) },
                            onToggleStatus = { viewModel.toggleUserStatus(admin) }
                        )
                        Divider(color = Color(0xFFF0F2F5), thickness = 1.dp)
                    }
                }

                // Deleted Admins
                if (deletedAdmins.isNotEmpty()) {
                    item {
                        Text(
                            text = "Deleted Admins",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0xFF111418)
                        )
                    }

                    items(deletedAdmins) { admin ->
                        AdminListItem(
                            admin = admin,
                            currentUserEmail = currentUserEmail,
                            expandedMenuId = expandedMenuId,
                            onOptionsClick = { expandedMenuId = admin.email },
                            onDismissMenu = { expandedMenuId = null },
                            onDelete = { viewModel.deleteUser(admin.id) },
                            onResetPassword = { viewModel.resetPassword(admin.email) },
                            onToggleStatus = { viewModel.toggleUserStatus(admin) }
                        )
                        Divider(color = Color(0xFFF0F2F5), thickness = 1.dp)
                    }
                }

                if (filteredAdmins.isEmpty()) {
                    item {
                        Text(
                            text = "No users found",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Add Admin Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Color(0xFF0D80F2),
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Admin"
                    )
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
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = Color(0xFF0D80F2),
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Loading admins...",
                        color = Color.White
                    )
                }
            }
        }

        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showAddDialog) {
        AddAdminDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { username, email, password ->
                viewModel.registerSuperAdmin(username, email, password)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AdminListItem(
    admin: AdminUser,
    currentUserEmail: String?,
    expandedMenuId: String?,
    onOptionsClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onDelete: () -> Unit,
    onResetPassword: () -> Unit,
    onToggleStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = admin.email.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = admin.email,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = admin.status ?: "UNKNOWN",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = when (admin.status?.uppercase()) {
                            "ACTIVE" -> Color(0xFF2E7D32)
                            "BLOCKED" -> Color.Red
                            "DELETED" -> Color.Gray
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Box {
            IconButton(
                onClick = onOptionsClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            DropdownMenu(
                expanded = expandedMenuId == admin.email,
                onDismissRequest = onDismissMenu,
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                if (admin.status == "BLOCKED") {
                    DropdownMenuItem(
                        text = { Text("Unblock User") },
                        onClick = {
                            onToggleStatus()
                            onDismissMenu()
                        }
                    )
                } else if (admin.status != "DELETED") {
                    DropdownMenuItem(
                        text = { Text("Block User") },
                        onClick = {
                            onToggleStatus()
                            onDismissMenu()
                        }
                    )
                }

                DropdownMenuItem(
                    text = { Text("Reset Password") },
                    onClick = {
                        onResetPassword()
                        onDismissMenu()
                    }
                )

                if (admin.email != currentUserEmail && admin.status != "DELETED") {
                    DropdownMenuItem(
                        text = { Text("Delete User", color = Color.Red) },
                        onClick = {
                            onDelete()
                            onDismissMenu()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            placeholder = { Text("Search by email") },
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                    keyboardController?.hide()
                }
            ),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF0F2F5),
                unfocusedContainerColor = Color(0xFFF0F2F5),
                disabledContainerColor = Color(0xFFF0F2F5),
            )
        )
    }
}

@Composable
private fun FilterOptions(
    selectedFilter: UserFilter?,
    onFilterSelected: (UserFilter) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
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
                text = "Filter Users",
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

        UserFilter.entries.forEach { filter ->
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

@Composable
fun AddAdminDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val isValidPassword = password.length >= 6 && password.any { it.isDigit() } && password.any { it.isLetter() }
    val passwordsMatch = password == confirmPassword

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Admin") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
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
                                if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                )

                if (!isValidPassword && password.isNotEmpty()) {
                    Text(
                        "Password must be at least 6 characters and include letters and numbers.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                if (!passwordsMatch && confirmPassword.isNotEmpty()) {
                    Text("Passwords do not match.", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (username.isNotBlank() && email.isNotBlank() && passwordsMatch && isValidPassword) {
                        onConfirm(username, email, password)
                    }
                },
                enabled = username.isNotBlank() && email.isNotBlank() && passwordsMatch && isValidPassword
            ) {
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

enum class UserFilter(val displayName: String) {
    ALL("All"),
    ACTIVE("Active"),
    BLOCKED("Blocked"),
    DELETED("Deleted")
}

// Preview
@Composable
fun UserManagementScreenPreviewContent(
    admins: List<AdminUser>,
    isLoading: Boolean,
    currentUserEmail: String?,
    onAddAdmin: (String, String, String) -> Unit = { _, _, _ -> },
    onDelete: (Long) -> Unit = {},
    onResetPassword: (String) -> Unit = {},
    onToggleStatus: (AdminUser) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<UserFilter?>(null) }
    var expandedMenuId by remember { mutableStateOf<String?>(null) }

    val filteredAdmins = remember(admins, searchQuery, selectedFilter) {
        admins.filter { admin ->
            val matchesSearch = admin.email.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                UserFilter.ALL -> true
                UserFilter.ACTIVE -> admin.status == "ACTIVE"
                UserFilter.BLOCKED -> admin.status == "BLOCKED"
                UserFilter.DELETED -> admin.status == "DELETED"
                null -> true
            }
            matchesSearch && matchesFilter
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header and search/filter UI would be the same as in UserManagementScreen

            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(filteredAdmins) { admin ->
                    AdminListItem(
                        admin = admin,
                        currentUserEmail = currentUserEmail,
                        expandedMenuId = expandedMenuId,
                        onOptionsClick = { expandedMenuId = admin.email },
                        onDismissMenu = { expandedMenuId = null },
                        onDelete = { onDelete(admin.id) },
                        onResetPassword = { onResetPassword(admin.email) },
                        onToggleStatus = { onToggleStatus(admin) }
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                        thickness = 1.dp
                    )
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Loading admins...",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddAdminDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { username, email, password ->
                onAddAdmin(username, email, password)
                showAddDialog = false
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserManagementScreenPreview() {
    val sampleAdmins = listOf(
        AdminUser(1, "admin1", "admin1@example.com", "SUPER_ADMIN", null, "ACTIVE", null),
        AdminUser(2, "admin2", "admin2@example.com", "SUPER_ADMIN", null, "BLOCKED", null),
        AdminUser(3, "admin3", "admin3@example.com", "SUPER_ADMIN", null, "DELETED", "2023-01-01")
    )

    EduVODTheme {
        UserManagementScreenPreviewContent(
            admins = sampleAdmins,
            isLoading = false,
            currentUserEmail = "admin1@example.com"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserManagementScreenLoadingPreview() {
    EduVODTheme {
        UserManagementScreenPreviewContent(
            admins = emptyList(),
            isLoading = true,
            currentUserEmail = null
        )
    }
}