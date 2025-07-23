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
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.viewmodel.SchoolManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolDetailsScreen(
    navController: NavHostController,
    schoolName: String,
    viewModel: SchoolManagementViewModel = viewModel()
) {
    val school = viewModel.getSchoolByName(schoolName)

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
            if (school != null) {
                FloatingActionButton(
                    onClick = {
                        Log.d("SchoolDetailsScreen", "Navigating to edit screen for: ${school.name}")
                        navController.navigate("edit_school/${school.name}")
                    },
                    containerColor = Color(0xFF1565C0),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
            }
        }
    ) { padding ->
        if (school != null) {
            Log.d("SchoolDetailsScreen", "Displaying school details for: ${school.name}")
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SectionCard(
                    title = " Basic Information",
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
                    title = " Contact Information",
                    items = listOf(
                        "Mobile" to school.mobile,
                        "Email" to school.email,
                        "Website" to school.website
                    )
                )

                SectionCard(
                    title = " Location",
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
        } else {
            Log.e("SchoolDetailsScreen", "School not found for name: $schoolName")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("School not found", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
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


