package com.example.eduvod.ui.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
    var expanded by remember { mutableStateOf(false) }

    val gradeList = viewMap.grades
    val groupedGrades = gradeList.groupBy { it.curriculum }
    val curriculumOptions = viewMap.allCurriculums

    LaunchedEffect(Unit) {
        viewMap.snackbarMessage.collect { message ->
            message?.let {
                snackbarHostState.showSnackbar(it)
                viewMap.clearSnackbar()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Grades Management",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1565C0), Color(0xFF0D47A1))
                    )
                )
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

            if (gradeList.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No grades added yet.", color = Color.Gray)
                    }
                }
            } else {
                groupedGrades.forEach { (curriculum, grades) ->
                    item {
                        Text(
                            text = curriculum,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1565C0)
                            ),
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }

                    items(grades) { grade ->
                        val bgColor = when (grade.curriculum) {
                            "CBC" -> Color(0xFFE3F2FD)
                            "8-4-4" -> Color(0xFFFFF9C4)
                            "British" -> Color(0xFFF3E5F5)
                            "IGCSE" -> Color(0xFFFFEBEE)
                            else -> Color.White
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(4.dp, shape = RoundedCornerShape(16.dp)),
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = bgColor),
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(grade.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Curriculum: ${grade.curriculum}", color = Color.Gray, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${grade.streams.size} stream(s)", color = Color.Gray, fontSize = 14.sp)
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
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            shape = RoundedCornerShape(16.dp),
            title = { Text("Add Grade", fontWeight = FontWeight.Bold) },
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
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCurriculum,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Curriculum") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            curriculumOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedCurriculum = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                ElevatedButton(onClick = {
                    if (newGrade.isNotBlank()) {
                        viewMap.addGrade(
                            Grade(
                                name = newGrade,
                                curriculum = selectedCurriculum,
                                streams = mutableStateListOf(),
                                hasSchool = false
                            )
                        )
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



