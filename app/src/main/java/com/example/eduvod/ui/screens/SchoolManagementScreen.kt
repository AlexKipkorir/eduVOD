package com.example.eduvod.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import androidx.test.espresso.core.internal.deps.dagger.Lazy


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolManagementScreen(navController: NavHostController) {
    val schools = remember { mutableStateListOf("Green Ivy High", "St. Monica Academy", "Hope Junior School") }


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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)

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

                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Registered Schools",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(schools) { schoolName ->
                SchoolCard(
                    schoolName = schoolName,
                    onView = { /* TODO: View School Details */ },
                    onEdit = { /* TODO: Edit School Details */ },
                    onManageAdmin = { /* TODO: Manage School Admins */ }
                )

            }
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
               Text("MoE REG: 123456 | Email: school@edu.org", fontSize = 13.sp, color = Color.Gray)
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