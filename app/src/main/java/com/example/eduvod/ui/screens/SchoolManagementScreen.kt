package com.example.eduvod.ui.screens

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.model.School
import com.example.eduvod.viewmodel.SchoolManagementViewModel
import kotlinx.coroutines.launch

data class School(
    val name: String,
    val moeRegNo: String,
    val email: String,
    var hasAdmin: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolManagementScreen(
    navController: NavHostController,
    viewModel: SchoolManagementViewModel = viewModel(),
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    val schools = viewModel.schools

    val selectedSchool by remember { mutableStateOf<String?>(null) }
    var showAdminDialog by remember { mutableStateOf(false) }

    var selectedRegion by remember { mutableStateOf("ALL") }
    var selectedType by remember { mutableStateOf("ALL") }

    val regionOptions = listOf("ALL", "Nairobi", "Mombasa", "Kisumu", "Eldoret", "Garissa", "Isiolo", "Nakuru", "Turkana")
    val typeOptions = listOf("All", "Primary", "Secondary", "Mixed")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                    text = "School Management",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                  )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)

                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xff0D47A1))
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon =  { Icon(Icons.Default.Add, contentDescription = "null") },
                text = { Text("Add New School")},
                onClick = {
                    navController.navigate("add_school")
                }
            )
        },
        containerColor = Color(0xFFF4F9FC),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val filteredSchools = schools.filter {
            it.name.contains(searchQuery, ignoreCase = true) &&
                    (selectedRegion == "ALL" || it.region == selectedRegion) &&
                    (selectedType == "ALL" || it.type == selectedType)
        }


        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Import Schools",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF0D47A1),
                        fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("School template downloaded.")
                            }
                        }
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download Template")
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Import functionality not connected yet.")
                            }
                        }
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Import Excel")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Schools") },
                    singleLine = true,
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                           IconButton(onClick = { searchQuery = "" }) {
                               Icon(Icons.Default.Close, contentDescription = "Clear Search")
                           }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Registered Schools",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterDropdown(
                        label = "Region",
                        options = regionOptions,
                        selectedOption = selectedRegion,
                        onSelected = { selectedRegion = it }
                    )
                    FilterDropdown(
                        label = "Type",
                        options = typeOptions,
                        selectedOption = selectedType,
                        onSelected = { selectedType = it }
                    )
                    Button(
                        onClick = {
                            searchQuery = ""
                            selectedRegion = "ALL"
                            selectedType = "ALL"
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Filters", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset Filters")
                    }
                }
            }

            items(filteredSchools) { school ->
                SchoolCard(
                    school = school,
                    onView = {
                        navController.navigate("school_details/${Uri.encode(school.name)}")
                    },
                    onEdit = {
                        navController.navigate("edit_school/${Uri.encode(school.name)}")
                    },
                    onManageAdmin = {
                        navController.navigate("manage_admins/${Uri.encode(school.name)}")
                    }
                )
            }


        }
        if (showAdminDialog && selectedSchool != null) {
            AlertDialog(
                onDismissRequest = { showAdminDialog = false },
                title = { Text("Manage Admin for $selectedSchool") },
                text = {
                    Column {
                        Text("• Add Admin")
                        Text("• Block/Disable Admin")
                        Text("• Reset Admin Account")
                    }
                },
                confirmButton =  {
                    TextButton(onClick = { showAdminDialog = false }) {
                        Text("Close")
                    }
                }
            )

        }

    }
}

@Composable
fun SchoolCard(
    school: School,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onManageAdmin: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                school.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0D47A1)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "MoE REG: ${school.moeRegNo} | Email: ${school.email}",
                fontSize = 13.sp,
                color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionIconWithLabel(
                    icon = Icons.Default.Visibility,
                    label = "View",
                    onClick = onView
                )
                ActionIconWithLabel(
                    icon = Icons.Default.Edit,
                    label = "Edit",
                    onClick = onEdit
                )

                if (!school.hasAdmin) {
                    ActionIconWithLabel(
                        icon = Icons.Default.PersonAdd,
                        label = "Add Admin",
                        onClick = onManageAdmin
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Admin Assigned",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(24.dp)
                        )
                        Text("Admin", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun ActionIconWithLabel(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 4.dp)
    ) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = label, tint = Color(0xFF1565C0), modifier = Modifier.size(24.dp))
        }
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", Modifier.clickable { expanded = true })
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0D47A1),
                unfocusedBorderColor = Color.LightGray
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}
