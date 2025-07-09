//package com.example.eduvod.ui.screens.gradesmanagement
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.ArrowDownward
//import androidx.compose.material.icons.filled.ArrowUpward
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material.icons.filled.School
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavHostController
//import com.example.eduvod.viewmodel.GradesViewModel
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun StreamViewScreen(
//    navController: NavHostController,
//    gradeName: String,
//    viewModel: GradesViewModel = viewModel()
//) {
//    val snackbarHostState = remember { SnackbarHostState() }
//    val scope = rememberCoroutineScope()
//
//    LaunchedEffect(Unit) {
//        viewModel.selectedGrade.value = viewModel.grades.find { it.name == gradeName }
//    }
//
//    val selectedGrade by viewModel.selectedGrade
//    val streams = selectedGrade?.streams ?: emptyList()
//
//    var newStream by remember { mutableStateOf("") }
//    val snackbar by viewModel.snackbarMessage.collectAsState()
//    var streamToDelete by remember { mutableStateOf<String?>(null) }
//
//
//    LaunchedEffect(snackbar) {
//        snackbar?.let {
//            snackbarHostState.showSnackbar(it)
//            viewModel.clearSnackbar()
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        "Streams in $gradeName",
//                        color = Color.White,
//                        fontWeight = FontWeight.Bold
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
//                modifier = Modifier.background(
//                    Brush.verticalGradient(
//                        listOf(Color(0xFF1565C0), Color(0xFF0D47A1))
//                    )
//                )
//            )
//        },
//        snackbarHost = { SnackbarHost(snackbarHostState) },
//        containerColor = Color(0xFFF4F9FC)
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .padding(16.dp)
//                .fillMaxSize(),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            OutlinedTextField(
//                value = newStream,
//                onValueChange = { newStream = it },
//                label = { Text("New Stream") },
//                modifier = Modifier.fillMaxWidth(),
//                singleLine = true,
//                trailingIcon = {
//                    IconButton(onClick = {
//                        if (newStream.isNotBlank()) {
//                            viewModel.addStreamToSelectedGrade(newStream.trim())
//                            newStream = ""
//                            scope.launch {
//                                snackbarHostState.showSnackbar("Stream added")
//                            }
//                        }
//                    }) {
//                        Icon(Icons.Default.Add, contentDescription = "Add Stream")
//                    }
//                }
//            )
//
//            Card(
//                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(12.dp)
//            ) {
//                Column(modifier = Modifier.padding(16.dp)) {
//                    Text(text = "Grade: $gradeName", fontWeight = FontWeight.Bold)
//                    Text(text = "Curriculum: ${selectedGrade?.curriculum ?: "N/A"}", color = Color.DarkGray)
//                    Text(text = "Streams: ${streams.size}", color = Color.DarkGray)
//                }
//            }
//
//            if (streams.isEmpty()) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 48.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Icon(Icons.Default.School, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text("No streams available.", color = Color.Gray)
//                }
//            } else {
//                LazyColumn(
//                    verticalArrangement = Arrangement.spacedBy(12.dp),
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    itemsIndexed(streams) { index, stream ->
//                        var showEditDialog by remember { mutableStateOf(false) }
//
//                        Card(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .shadow(2.dp, shape = RoundedCornerShape(16.dp)),
//                            colors = CardDefaults.cardColors(containerColor = Color.White),
//                            shape = RoundedCornerShape(16.dp),
//                            elevation = CardDefaults.cardElevation(4.dp)
//                        ) {
//                            Column(modifier = Modifier.padding(16.dp)) {
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.SpaceBetween,
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Text(
//                                        text = stream.name,
//                                        fontWeight = FontWeight.Bold,
//                                        fontSize = 16.sp
//                                    )
//
//                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                                        IconButton(onClick = { showEditDialog = true }) {
//                                            Icon(Icons.Default.Edit, contentDescription = "Edit")
//                                        }
//                                        IconButton(onClick = {
//                                            streamToDelete = stream.name
//                                        }) {
//                                            Icon(Icons.Default.Delete, contentDescription = "Delete")
//                                        }
//                                    }
//                                }
//
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.End
//                                ) {
//                                    IconButton(
//                                        onClick = { viewModel.moveStreamUp(index) },
//                                        enabled = index > 0
//                                    ) {
//                                        Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up")
//                                    }
//                                    IconButton(
//                                        onClick = { viewModel.moveStreamDown(index) },
//                                        enabled = index < streams.size - 1
//                                    ) {
//                                        Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down")
//                                    }
//                                }
//                            }
//                        }
//
//                        if (showEditDialog) {
//                            EditStreamDialog(
//                                currentName = stream.name,
//                                onDismiss = { showEditDialog = false },
//                                onConfirm = { newName ->
//                                    val renamed = viewModel.renameStream(
//                                        gradeName,
//                                        oldName = stream.name,
//                                        newName = newName
//                                    )
//                                    showEditDialog = false
//                                    scope.launch {
//                                        snackbarHostState.showSnackbar(
//                                            if (renamed) "Stream renamed." else "Stream name already exists"
//                                        )
//                                    }
//                                }
//                            )
//                        }
//
//
//                    }
//                }
//                if (streamToDelete != null) {
//                    AlertDialog(
//                        onDismissRequest = { streamToDelete = null },
//                        title = { Text("Confirm Deletion") },
//                        text = { Text("Are you sure you want to delete stream \"${streamToDelete}\"?") },
//                        confirmButton = {
//                            TextButton(onClick = {
//                                viewModel.removeStreamFromSelectedGrade(streamToDelete!!)
//                                streamToDelete = null
//                                scope.launch {
//                                    snackbarHostState.showSnackbar("Stream deleted.")
//                                }
//                            }) {
//                                Text("Delete")
//                            }
//                        },
//                        dismissButton = {
//                            TextButton(onClick = { streamToDelete = null }) {
//                                Text("Cancel")
//                            }
//                        },
//                        shape = RoundedCornerShape(16.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun EditStreamDialog(
//    currentName: String,
//    onDismiss: () -> Unit,
//    onConfirm: (String) -> Unit
//) {
//    var newName by remember { mutableStateOf(currentName) }
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Rename Stream", fontWeight = FontWeight.Bold) },
//        text = {
//            OutlinedTextField(
//                value = newName,
//                onValueChange = { newName = it },
//                label = { Text("New Stream Name") },
//                singleLine = true,
//                modifier = Modifier.fillMaxWidth()
//            )
//        },
//        confirmButton = {
//            TextButton(onClick = {
//                val trimmed = newName.trim()
//                if (trimmed.isNotEmpty()) {
//                    onConfirm(trimmed)
//                }
//            }) {
//                Text("Save")
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = onDismiss) {
//                Text("Cancel")
//            }
//        },
//        shape = RoundedCornerShape(16.dp)
//    )
//}
