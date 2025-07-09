package com.example.eduvod.ui.screens.gradesmanagement

import android.net.Uri
import androidx.compose.foundation.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.R
import com.example.eduvod.model.Grade
import com.example.eduvod.viewmodel.GradesViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    var selectedCurriculum by remember { mutableStateOf(viewMap.curriculums.firstOrNull()?.name ?: "") }
    var expanded by remember { mutableStateOf(false) }

    val searchQuery = remember { mutableStateOf("") }
    var searchJob by remember { mutableStateOf<Job?>(null) }

    var selectedCurriculumTab by remember { mutableStateOf(viewMap.curriculums.firstOrNull()?.name ?: "") }

    val grades = viewMap.grades.filter {
        it.curriculum == selectedCurriculumTab &&
                it.name.contains(searchQuery.value.trim(), ignoreCase = true)
    }

    val curriculumTabs = viewMap.curriculums.map { it.name }
    var gradeToDelete by remember { mutableStateOf<Grade?>(null) }


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
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {

            if (curriculumTabs.isNotEmpty()) {
                val selectedIndex = curriculumTabs.indexOf(selectedCurriculumTab)
                    .takeIf { it >= 0 } ?: 0
                selectedCurriculumTab = curriculumTabs[selectedIndex]

                ScrollableTabRow(
                    selectedTabIndex = selectedIndex,
                    edgePadding = 0.dp
                ) {
                    curriculumTabs.forEachIndexed { index, name ->
                        Tab(
                            selected = selectedIndex == index,
                            onClick = {
                                selectedCurriculumTab = name
                            },
                            text = {
                                Text("$name (${viewMap.grades.count { it.curriculum == name }})")
                            }
                        )
                    }
                }
            } else {
                Text(
                    "No curriculums available",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = {
                    searchQuery.value = it
                    searchJob?.cancel()
                    searchJob = scope.launch {
                        delay(300)
                    }
                },
                label = { Text("Search Grade") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (grades.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No grades found.", color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap '+' to add your first grade.", color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(grades) { grade ->
                        val bgColor = viewMap.getCurriculumColor(grade.curriculum)


                        Card(
                            modifier = Modifier.fillMaxWidth(),
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
                                Spacer(modifier = Modifier.height(12.dp))

                                IconButton(onClick = { gradeToDelete = grade }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Black)
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
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            viewMap.curriculums.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.name) },
                                    onClick = {
                                        selectedCurriculum = item.name
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
                        viewMap.addGrade(newGrade, selectedCurriculum)
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

    gradeToDelete?.let { grade ->
        AlertDialog(
            onDismissRequest = { gradeToDelete = null },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete '${grade.name}'?") },
            confirmButton = {
                TextButton(onClick = {
                    viewMap.deleteGrade(grade)
                    scope.launch {
                        snackbarHostState.showSnackbar("Deleted: ${grade.name}")
                    }
                    gradeToDelete = null
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { gradeToDelete = null }) {
                    Text("Cancel")
                }
            },
            icon = {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
            }
        )
    }
}
