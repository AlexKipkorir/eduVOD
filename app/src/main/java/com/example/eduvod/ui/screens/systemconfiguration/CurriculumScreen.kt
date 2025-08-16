package com.example.eduvod.ui.screens.systemconfiguration

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eduvod.model.SimpleItem
import com.example.eduvod.ui.theme.EduVODTheme
import com.example.eduvod.viewmodel.SystemConfigViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurriculumScreen(
    viewModel: SystemConfigViewModel,
    navController: NavHostController
) {
    val curriculums = viewModel.curriculums
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var newCurriculumName by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<SimpleItem?>(null) }

    LaunchedEffect(Unit) {
        viewModel.initialize()
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            viewModel.clearSnackbar()
        }
    }

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
                text = "Curriculum",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = Color(0xFF111418)
            )
        }

        if (curriculums.isEmpty() && !isLoading) {
            CurriculumEmptyStateView(onAddClick = { showAddDialog = true })
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(curriculums) { curriculum ->
                    CurriculumItem(
                        curriculum = curriculum,
                        onClick = {
                            editingItem = curriculum
                            newCurriculumName = curriculum.name
                            isEditing = true
                            showAddDialog = true
                        }
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = {
                isEditing = false
                editingItem = null
                newCurriculumName = ""
                showAddDialog = true
            },
            containerColor = Color(0xFF0D80F2)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
        }
    }

    if (showAddDialog) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showAddDialog = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (isEditing) "Edit Curriculum" else "Add New Curriculum",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = newCurriculumName,
                    onValueChange = { newCurriculumName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    placeholder = { Text("Curriculum Name") },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF0F2F5),
                        focusedContainerColor = Color(0xFFF0F2F5),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )

                Button(
                    onClick = {
                        if (newCurriculumName.isBlank()) {
                            viewModel.snackbarMessage.value = "Name cannot be empty"
                            return@Button
                        }

                        if (isEditing) {
                            editingItem?.let {
                                viewModel.updateItem("Curriculum", it.name, newCurriculumName)
                            }
                        } else {
                            viewModel.addItem("Curriculum", newCurriculumName)
                        }
                        showAddDialog = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0D80F2),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (isEditing) "Update Curriculum" else "Add Curriculum",
                        fontWeight = FontWeight.Bold
                    )
                }
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
                    color = Color(0xFF0D80F2),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading...",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CurriculumItem(
    curriculum: SimpleItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Curriculum",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF60758A),
                        fontWeight = FontWeight.Normal
                    )
                )
                Text(
                    text = curriculum.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF111418),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = "Description not available", // Placeholder since SimpleItem doesn't have description
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF60758A),
                        fontWeight = FontWeight.Normal
                    )
                )
            }

            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F2F5))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color(0xFF111418),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun CurriculumEmptyStateView(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = Color(0xFFDBE0E6),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(24.dp, 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No Curricula Found",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111418)
                )
            )
            Text(
                text = "Add a new curriculum to get started.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF111418),
                    fontWeight = FontWeight.Normal
                )
            )
        }

        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF0F2F5),
                contentColor = Color(0xFF111418)
            ),
            modifier = Modifier.height(40.dp)
        ) {
            Text(
                text = "Add Curriculum",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

// Preview-only composable that doesn't need a ViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewCurriculumScreen() {
    val mockCurriculums = remember {
        listOf(
            SimpleItem(1, "8-4-4 System"),
            SimpleItem(2, "CBC Curriculum"),
            SimpleItem(3, "IGCSE")
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newCurriculumName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

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
                onClick = { /* No-op for preview */ },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF111418)
                )
            }

            Text(
                text = "Curriculum",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = Color(0xFF111418)
            )
        }

        if (mockCurriculums.isEmpty()) {
            CurriculumEmptyStateView(onAddClick = { showAddDialog = true })
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mockCurriculums) { curriculum ->
                    CurriculumItem(
                        curriculum = curriculum,
                        onClick = { showAddDialog = true }
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = Color(0xFF0D80F2)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
        }
    }

    if (showAddDialog) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showAddDialog = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Add New Curriculum",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = newCurriculumName,
                    onValueChange = { newCurriculumName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    placeholder = { Text("Curriculum Name") },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF0F2F5),
                        focusedContainerColor = Color(0xFFF0F2F5),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )

                Button(
                    onClick = { showAddDialog = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0D80F2),
                        contentColor = Color.White
                    )
                ) {
                    Text("Add Curriculum", fontWeight = FontWeight.Bold)
                }
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
                    color = Color(0xFF0D80F2),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading...",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurriculumScreenPreview() {
    EduVODTheme {
        PreviewCurriculumScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun CurriculumEmptyStatePreview() {
    EduVODTheme {
        CurriculumEmptyStateView(onAddClick = {})
    }
}