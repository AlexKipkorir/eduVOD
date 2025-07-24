package com.example.eduvod.ui.screens.schoolmanagement

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.viewmodel.SchoolManagementViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolDetailsScreen(
    navController: NavHostController,
    schoolName: String,
    viewModel: SchoolManagementViewModel = viewModel()
) {
    var isLoading by remember { mutableStateOf(true) }
    var showNotFound by remember { mutableStateOf(false) }
    val school by remember { derivedStateOf { viewModel.getSchoolByName(schoolName) } }
    val currentSchool = school

    LaunchedEffect(schoolName) {
        isLoading = true
        delay(500)
        isLoading = false
        showNotFound = school == null
    }

    Log.d("SchoolDetailsScreen", "Requested school name: $schoolName")
    Log.d("SchoolDetailsScreen", "Retrieved school object: ${school?.name ?: "null"}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Details for: $schoolName",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1))
            )
        },
        floatingActionButton = {
            if (!isLoading && school != null) {
                school?.let { school ->
                    FloatingActionButton(
                        onClick = {
                            Log.d("SchoolDetailsScreen", "Navigating to edit screen for: ${school.name}")
                            navController.navigate("edit_school/${school.id}")
                        },
                        containerColor = Color(0xFF1565C0),
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    LoadingState()
                }
                currentSchool != null -> {
                    Log.d("SchoolDetailsScreen", "Displaying school details for: ${currentSchool.name}")
                    SchoolDetailsContent(school = currentSchool)
                }
                else -> {
                    Log.e("SchoolDetailsScreen", "School not found for name: $schoolName")
                    ErrorState(schoolName = schoolName)
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Loading school details...", fontSize = 16.sp)
    }
}

@Composable
private fun ErrorState(schoolName: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "School not found",
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = schoolName,
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun SchoolDetailsContent(school: com.example.eduvod.model.School) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionCard(
            title = "Basic Information",
            items = listOf(
                "School Name" to school.name,
                "MoE REG NO" to school.moeRegNo,
                "KPSA REG NO" to school.kpsaRegNo,
                "Curriculum" to school.curriculum,
                "Category" to school.category,
                "Type" to school.type,
                "Composition" to school.composition
            )
        )

        SectionCard(
            title = "Contact Information",
            items = listOf(
                "Mobile" to school.mobile,
                "Email" to school.email,
                "Website" to school.website
            )
        )

        SectionCard(
            title = "Location",
            items = listOf(
                "Region" to school.region,
                "Diocese" to school.diocese,
                "County" to school.county,
                "SubCounty" to school.subCounty,
                "Location" to school.location,
                "Address" to school.address
            )
        )
    }
}

@Composable
fun SectionCard(title: String, items: List<Pair<String, String?>>) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0D47A1))
            Spacer(modifier = Modifier.height(8.dp))

            items.forEach { (label, value) ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

                    if (label in listOf("Category", "Curriculum", "Type")) {
                        InfoBadge(text = value)
                    } else {
                        Text(
                            text = value ?: "—",
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoBadge(text: String?, backgroundColor: Color = Color(0xFFE3F2FD)) {
    val safeText = text?.takeIf { it.isNotBlank() } ?: return

    Box(
        modifier = Modifier
            .padding(end = 6.dp, top = 4.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = safeText,
            fontSize = 12.sp,
            color = Color(0xFF0D47A1),
            fontWeight = FontWeight.SemiBold
        )
    }
}