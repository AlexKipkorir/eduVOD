package com.example.eduvod.ui.screens.schoolmanagement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eduvod.R
import com.example.eduvod.viewmodel.SchoolManagementViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSchoolAdminsScreen(
    navController: NavController,
    schoolName: String?,
    viewModel: SchoolManagementViewModel
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // State for dialogs and menus
    var showAddAdminDialog by remember { mutableStateOf(false) }
    var selectedAdminEmail by remember { mutableStateOf<String?>(null) }
    var expandedMenuForAdmin by remember { mutableStateOf<String?>(null) }
    var showUnassignConfirm by remember { mutableStateOf(false) }

    // Load data
    val isLoading by viewModel.isLoading
    val allAdmins = viewModel.schoolAdmins.filter {
        it.schoolName == schoolName
    }
    val unassignedAdmins = viewModel.getUnassignedAdmins()

    LaunchedEffect(schoolName) {
        viewModel.fetchAdmins()
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
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
                text = "School Admins - ${schoolName ?: ""}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = (-0.015).sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            // Empty space to balance the back button
            Spacer(modifier = Modifier.width(24.dp))
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Admin list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(allAdmins) { admin ->
                    ManageAdminListItem(
                        admin = admin,
                        onMenuClick = { expandedMenuForAdmin = admin.email },
                        onBlockAdmin = {
                            viewModel.blockAdmin(admin.email, admin.status == "ACTIVE")
                            expandedMenuForAdmin = null
                        },
                        onUnassignAdmin = {
                            selectedAdminEmail = admin.email
                            showUnassignConfirm = true
                            expandedMenuForAdmin = null
                        },
                        showMenu = expandedMenuForAdmin == admin.email,
                        onDismissMenu = { expandedMenuForAdmin = null }
                    )
                }
            }

            // Add Admin Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    onClick = { showAddAdminDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Admin",
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Add Admin",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Add Admin Dialog
        if (showAddAdminDialog) {
            val sheetState = rememberModalBottomSheetState()
            ModalBottomSheet(
                onDismissRequest = {
                    showAddAdminDialog = false
                    selectedAdminEmail = null
                },
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
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Admin Dropdown
                    SchoolDropdownField(
                        label = "Select Admin",
                        options = unassignedAdmins,
                        selectedOption = selectedAdminEmail,
                        onSelected = { selectedAdminEmail = it }
                    )

                    Button(
                        onClick = {
                            selectedAdminEmail?.let { email ->
                                scope.launch {
                                    viewModel.assignAdminToSchool(email, schoolName ?: "") {
                                        viewModel.fetchAdmins()
                                    }
                                    showAddAdminDialog = false
                                    selectedAdminEmail = null
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
                        enabled = selectedAdminEmail != null
                    ) {
                        Text("Add Admin", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Unassign Confirmation Dialog
        if (showUnassignConfirm && selectedAdminEmail != null) {
            AlertDialog(
                onDismissRequest = {
                    showUnassignConfirm = false
                    selectedAdminEmail = null
                },
                title = {
                    Text(
                        "Confirm Unassign",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to unassign this admin from $schoolName?",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                selectedAdminEmail?.let { email ->
                                    viewModel.unassignAdmin(email) {
                                        viewModel.fetchAdmins()
                                    }
                                }
                                showUnassignConfirm = false
                                selectedAdminEmail = null
                            }
                        }
                    ) {
                        Text(
                            "Confirm",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showUnassignConfirm = false
                            selectedAdminEmail = null
                        }
                    ) {
                        Text(
                            "Cancel",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun ManageAdminListItem(
    admin: com.example.eduvod.viewmodel.SchoolAdmin,
    onMenuClick: () -> Unit,
    onBlockAdmin: () -> Unit,
    onUnassignAdmin: () -> Unit,
    showMenu: Boolean,
    onDismissMenu: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (admin.status == "BLOCKED") {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Admin info with image
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Admin image with status indicator
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Image(
                            painter = painterResource(R.drawable.ic_profile_placeholder),
                            contentDescription = "Admin Profile",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        if (admin.status == "BLOCKED") {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.error)
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }

                    // Admin name and email
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = admin.username,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = admin.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Options menu
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

            }

            // Dropdown menu for options
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = onDismissMenu,
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            if (admin.status == "ACTIVE") "Block Admin" else "Unblock Admin",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    onClick = {
                        onBlockAdmin()
                        onDismissMenu()
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Block,
                            contentDescription = "Block Admin",
                            tint = if (admin.status == "ACTIVE") Color.Red else MaterialTheme.colorScheme.primary
                        )
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            "Unassign from School",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    onClick = {
                        onUnassignAdmin()
                        onDismissMenu()
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.PersonRemove,
                            contentDescription = "Unassign Admin",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManageSchoolAdminsScreenPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Header with back button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(24.dp),
                    tint = Color(0xFF111418)
                )

                Text(
                    text = "School Admins - Greenwood High",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = Color(0xFF111418)
                )

                // Empty space to balance the back button
                Spacer(modifier = Modifier.width(24.dp))
            }

            // Admin list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(listOf(
                    "admin1@example.com",
                    "admin2@example.com",
                    "admin3@example.com"
                )) { email ->
                    PreviewAdminListItem(
                        email = email,
                        isBlocked = email == "admin2@example.com"
                    )
                }
            }

            // Add Admin Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0D80F2),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Admin",
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Add Admin",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewAdminListItem(
    email: String,
    isBlocked: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isBlocked) Color(0xFFFFEBEE) else Color.White
        )
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Admin info with image
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Admin image
                    Image(
                        painter = painterResource(R.drawable.ic_profile_placeholder),
                        contentDescription = "Admin Profile",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    // Admin email
                    Column {
                        Text(
                            text = email.substringBefore("@"),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xFF111418),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6B7280),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isBlocked) {
                            Text(
                                text = "Blocked",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Red,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Options menu
                IconButton(
                    onClick = {},
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color(0xFF111418)
                    )
                }
            }
        }
    }
}