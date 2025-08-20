package com.example.eduvod.ui.screens.gradesmanagement

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eduvod.model.Grade
import com.example.eduvod.model.SimpleItem
import com.example.eduvod.ui.theme.EduVODTheme
import com.example.eduvod.viewmodel.GradesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesManagementScreen(
    navController: NavHostController,
    viewModel: GradesViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val isLoading by viewModel.isLoading

    var showAddDialog by remember { mutableStateOf(false) }
    var newGrade by remember { mutableStateOf("") }
    var selectedCurriculum by remember { mutableStateOf(viewModel.curriculums.firstOrNull()?.name ?: "") }
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<GradeFilter?>(null) }
    var gradeToDelete by remember { mutableStateOf<Grade?>(null) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var isFilterExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    val filteredGrades = remember(viewModel.grades, searchQuery, selectedCurriculum, selectedFilter) {
        viewModel.grades.filter { grade ->
            val matchesSearch = grade.name.contains(searchQuery, ignoreCase = true)
            val matchesCurriculum = grade.curriculum == selectedCurriculum
            matchesSearch && matchesCurriculum
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                    text = "Grades Management",
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

            // Main content column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
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
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Filter Options
                if (isFilterExpanded) {
                    GradeFilterOptions(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { filter ->
                            selectedFilter = if (selectedFilter == filter) null else filter
                            isFilterExpanded = false
                        },
                        onClose = { isFilterExpanded = false },
                        modifier = Modifier.padding(top = 8.dp)
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
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Curriculum tabs
                if (viewModel.curriculums.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        viewModel.curriculums.forEach { curriculum ->
                            CurriculumTab(
                                curriculum = curriculum,
                                isSelected = selectedCurriculum == curriculum.name,
                                onClick = { selectedCurriculum = curriculum.name }
                            )
                        }
                    }
                }

                // Grades list
                if (filteredGrades.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No grades found",
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Tap '+' to add your first grade",
                            color = Color.Gray,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredGrades) { grade ->
                            GradeCard(
                                grade = grade,
                                backgroundColor = viewModel.getCurriculumColor(grade.curriculum),
                                onDelete = { gradeToDelete = grade }
                            )
                        }
                    }
                }
            }
        }

        // Add Grade FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF0D80F2),
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Grade",
                modifier = Modifier.size(24.dp)
            )
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
                        "Loading grades...",
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

    // Add Grade Dialog
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
                    text = "Add New Grade",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = newGrade,
                    onValueChange = { newGrade = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    placeholder = { Text("Grade Name") },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF0F2F5),
                        focusedContainerColor = Color(0xFFF0F2F5),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCurriculum,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Select Curriculum") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF0F2F5),
                            focusedContainerColor = Color(0xFFF0F2F5),
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        )
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        viewModel.curriculums.forEach { curriculum ->
                            DropdownMenuItem(
                                text = { Text(curriculum.name) },
                                onClick = {
                                    selectedCurriculum = curriculum.name
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (newGrade.isNotBlank()) {
                            viewModel.addGrade(newGrade, selectedCurriculum)
                            showAddDialog = false
                            newGrade = ""
                        }
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
                    Text("Add Grade", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Delete Dialog
    gradeToDelete?.let { grade ->
        AlertDialog(
            onDismissRequest = { gradeToDelete = null },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete '${grade.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteGrade(grade)
                        gradeToDelete = null
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { gradeToDelete = null },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }
            },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
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
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            placeholder = { Text("Search grades...") },
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
fun CurriculumTab(
    curriculum: SimpleItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = curriculum.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFF111418) else Color(0xFF60758A)
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .height(3.dp)
                .width(40.dp)
                .background(
                    color = if (isSelected) Color(0xFF111418) else Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}

@Composable
fun GradeCard(
    grade: Grade,
    backgroundColor: Color,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = grade.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Text(
                    text = "Curriculum: ${grade.curriculum}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

@Composable
private fun GradeFilterOptions(
    selectedFilter: com.example.eduvod.ui.screens.gradesmanagement.GradeFilter?,
    onFilterSelected: (com.example.eduvod.ui.screens.gradesmanagement.GradeFilter) -> Unit,
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
                text = "Filter Grades",
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

        com.example.eduvod.ui.screens.gradesmanagement.GradeFilter.entries.forEach { filter ->
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

enum class GradeFilter(val displayName: String) {
    ACTIVE("Active Grades"),
    ARCHIVED("Archived Grades"),
    CORE_SUBJECTS("Core Subjects"),
    ELECTIVES("Electives")
}

// Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesManagementScreenPreviewContent(
    grades: List<Grade>,
    curriculums: List<String>,
    isLoading: Boolean,
    onAddGrade: (String, String) -> Unit = { _, _ -> },
    onDeleteGrade: (Grade) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newGrade by remember { mutableStateOf("") }
    var selectedCurriculum by remember { mutableStateOf(curriculums.firstOrNull() ?: "") }
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var gradeToDelete by remember { mutableStateOf<Grade?>(null) }

    val filteredGrades = remember(grades, searchQuery, selectedCurriculum) {
        grades.filter { grade ->
            grade.name.contains(searchQuery, ignoreCase = true) &&
                    grade.curriculum == selectedCurriculum
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header and search UI would be the same as in GradesManagementScreen

            // Curriculum tabs
            if (curriculums.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    curriculums.forEach { curriculum ->
                        FilterChip(
                            selected = selectedCurriculum == curriculum,
                            onClick = { selectedCurriculum = curriculum },
                            label = { Text(curriculum) }
                        )
                    }
                }
            }

            // Grades list
            if (filteredGrades.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No grades found",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredGrades) { grade ->
                        GradeCard(
                            grade = grade,
                            backgroundColor = Color(0xFFE3F2FD),
                            onDelete = { gradeToDelete = grade }
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
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Loading grades...",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Grade") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newGrade,
                        onValueChange = { newGrade = it },
                        label = { Text("Grade Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    )

                    var expanded by remember { mutableStateOf(false) }
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
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            curriculums.forEach { curriculum ->
                                DropdownMenuItem(
                                    text = { Text(curriculum) },
                                    onClick = {
                                        selectedCurriculum = curriculum
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newGrade.isNotBlank()) {
                            onAddGrade(newGrade, selectedCurriculum)
                            showAddDialog = false
                            newGrade = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
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
                TextButton(
                    onClick = {
                        onDeleteGrade(grade)
                        gradeToDelete = null
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { gradeToDelete = null },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }
            },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GradesManagementScreenPreview() {
    val sampleGrades = listOf(
        Grade(1, "Grade 1", "CBC"),
        Grade(2, "Grade 2", "CBC"),
        Grade(3, "Form 1", "8-4-4"),
        Grade(4, "Form 2", "8-4-4")
    )
    val sampleCurriculums = listOf("CBC", "8-4-4", "British", "IGCSE")

    EduVODTheme {
        GradesManagementScreenPreviewContent(
            grades = sampleGrades,
            curriculums = sampleCurriculums,
            isLoading = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GradesManagementScreenLoadingPreview() {
    EduVODTheme {
        GradesManagementScreenPreviewContent(
            grades = emptyList(),
            curriculums = emptyList(),
            isLoading = true
        )
    }
}