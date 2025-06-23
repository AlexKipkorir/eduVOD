package com.example.eduvod.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateListOf
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
import com.example.eduvod.model.Grade
import com.example.eduvod.viewmodel.GradesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesManagementScreen(
    navController: NavHostController,
    viewMap: GradesViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var newGrade by remember { mutableStateOf("") }
    var selectedCurriculum by remember { mutableStateOf("CBC") }

    val gradeList = viewMap.grades
    val curriculumOptions = listOf("CBC", "8-4-4", "British", "IGCSE")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grades Management", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1))
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Grade") },
                text = { Text("Add Grade") },
                onClick = { showDialog = true }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF4F9FC)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Available Grades",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        color = Color(0xFF0D47A1),
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            items(gradeList) { grade ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(grade.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Curriculum: ${grade.curriculum}", color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Streams: ${grade.streams.joinToString()}", color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            IconButton(onClick = {
                                navController.navigate("view_streams/${Uri.encode(grade.name)}")
                            }) {
                                Icon(Icons.Default.Visibility, contentDescription = "View Stream", tint = Color(0xFF1565C0))
                            }


                            if (!grade.hasSchool) {
                                IconButton(onClick = {
                                    viewMap.deleteGrade(grade)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Deleted: ${grade.name}")
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Grade", tint = Color.Black)
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
            title = { Text("Add Grade") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newGrade,
                        onValueChange = { newGrade = it },
                        label = { Text("Grade Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = {}
                    ) {
                        OutlinedTextField(
                            value = selectedCurriculum,
                            onValueChange = {},
                            label = { Text("Curriculum") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(false) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = false,
                            onDismissRequest = { },
                        ) {
                           curriculumOptions.forEach { option ->
                               DropdownMenuItem(
                                   text = { Text(option) },
                                   onClick = { selectedCurriculum = option }
                               )
                           }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newGrade.isNotBlank()) {
                        viewMap.addGrade(Grade(
                            name = newGrade,
                            curriculum = selectedCurriculum,
                            streams = mutableStateListOf()
                        ))
                        showDialog = false
                        newGrade = ""
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
}


