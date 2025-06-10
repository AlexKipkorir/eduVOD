package com.example.eduvod.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Details For: $schoolName",
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
        }
    ) { padding ->
        if (school != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Basic Information
                SectionCard(title = "Basic Information", items = listOf(
                    "School Name" to school.name,
                    "MoE REG NO" to school.moeRegNo,
                    "KPSA REG NO" to school.kpsaRegNo,
                    "Curriculum" to school.curriculum,
                    "Category" to school.category,
                    "Type" to school.type,
                    "Composition" to school.composition
                ))

                //Section 2: Contact Details
                SectionCard(title = "Contact Information", items = listOf(
                    "Mobile" to school.mobile,
                    "Email" to school.email,
                    "Website" to school.website
                ))

                //Section 3: Location Details
                SectionCard(title = "Location", items = listOf(
                    "Region" to school.region,
                    "Diocese" to school.diocese,
                    "County" to school.county,
                    "SubCounty" to school.subCounty,
                    "Location" to school.location,
                    "Address" to school.address
                ))
            }
        } else {
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
fun SectionCard(title: String, items: List<Pair<String, String>>) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0D47A1)
            )
            Spacer(modifier = Modifier.height(8.dp))
            items.forEach{ (label,value) ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$label:",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(0.4f)
                    )
                    Text(
                        text = value,
                        modifier = Modifier.weight(0.6f)
                    )
                }
            }
        }
    }
}