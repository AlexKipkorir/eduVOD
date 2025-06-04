package com.example.eduvod.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolManagementScreen(navController: NavHostController) {
    val schools = remember { mutableStateListOf("Green Ivy High", "St. Monica Academy", "Hope Junior School") }

    var selectedSchool by remember { mutableStateOf<String?>(null) }
    var showAdminDialog by remember { mutableStateOf(false) }


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
        containerColor = Color(0xFFF4F9FC)
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Import Schools",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF0D47A1), fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { /* TODO: Download Template */ }) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download Template")
                    }
                    Button(onClick = { /* TODO: Upload Template */ }) {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Import Excel")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Registered Schools",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(schools) { schoolName ->
                SchoolCard(
                    schoolName = schoolName,
                    onView = {
                        navController.navigate("school_details/${Uri.encode(schoolName)}")
                    },
                    onEdit = {
                        navController.navigate("add_school?schoolName=${Uri.encode(schoolName)}")
                    },
                    onManageAdmin = {
                        navController.navigate("manage_admins/${Uri.encode(schoolName)}")
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
    schoolName: String,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onManageAdmin: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
           Column {
               Text(schoolName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
               Text("MoE REG: 123456 | Email: school@edu.org", fontSize = 13.sp, color = Color.Black)
           }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onView) {
                    Icon(Icons.Default.Visibility, contentDescription = "View", tint = Color(0xFF1565C0))
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF1565C0))
                }
                IconButton(onClick = onManageAdmin) {
                    Icon(Icons.Default.Settings, contentDescription = "Admin", tint = Color(0xFF1565C0))
                }
            }
        }
    }
}