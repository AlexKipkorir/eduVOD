package com.example.eduvod.ui.screens.schoolmanagement

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eduvod.model.School
import com.example.eduvod.viewmodel.SchoolManagementViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolDetailsScreen(
    navController: NavController,
    schoolName: String,
    viewModel: SchoolManagementViewModel
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

    Scaffold(
        floatingActionButton = {
            if (!isLoading && school != null) {
                FloatingActionButton(
                    onClick = { navController.navigate("edit_school/${school?.id}") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
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
                isLoading -> LoadingState()
                currentSchool != null -> SchoolDetailsContent(navController, school = currentSchool)
                else -> ErrorState(schoolName = schoolName)
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
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun SchoolDetailsContent(
    navController: NavController,
    school: School
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header with back button and title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.popBackStack() },
                tint = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "School Details",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = (-0.015).sp
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // School Information Section
        Text(
            text = "Basic Information",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, 8.dp),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Basic Information Fields
        DetailItem(label = "MoE REG NO", value = school.moeRegNo)
        DetailItem(label = "KPSA REG NO", value = school.kpsaRegNo ?: "—")
        DetailItem(label = "School Name", value = school.name)
        DetailItem(label = "School Type", value = school.type)
        DetailItem(label = "Curriculum", value = school.curriculum ?: "—")
        DetailItem(label = "Category", value = school.category)
        DetailItem(label = "Composition", value = school.composition)

        // Contact Information Section
        Text(
            text = "Contact Information",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 24.dp, 16.dp, 8.dp),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Contact Information Fields
        DetailItem(label = "Phone Number", value = school.mobile ?: "—")
        DetailItem(label = "Email", value = school.email)
        DetailItem(label = "Website", value = school.website ?: "—")

        // Location Information Section
        Text(
            text = "Location Information",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 24.dp, 16.dp, 8.dp),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Location Information Fields
        DetailItem(label = "Region", value = school.region)
        DetailItem(label = "Diocese", value = school.diocese ?: "—")
        DetailItem(label = "County", value = school.county)
        DetailItem(label = "Sub-County", value = school.subCounty)
        DetailItem(label = "Location", value = school.location ?: "—")
        DetailItem(label = "Address", value = school.address ?: "—")
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(0.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(0.3f),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
            Text(
                text = value,
                modifier = Modifier.weight(0.7f),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SchoolDetailsScreenPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with back button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 16.dp, 16.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(24.dp),
                    tint = Color(0xFF111418)
                )

                Text(
                    text = "School Details",
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = Color(0xFF111418)
                )
            }

            // School Information Section
            Text(
                text = "Basic Information",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 16.dp, 16.dp, 8.dp),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = Color(0xFF111418)
            )

            // Basic Information Fields
            PreviewDetailItem(label = "MoE REG NO", value = "MOE/1234/2023")
            PreviewDetailItem(label = "KPSA REG NO", value = "KPSA/5678/2023")
            PreviewDetailItem(label = "School Name", value = "Greenwood High")
            PreviewDetailItem(label = "School Type", value = "Public")
            PreviewDetailItem(label = "Curriculum", value = "CBC")
            PreviewDetailItem(label = "Category", value = "Day")
            PreviewDetailItem(label = "Composition", value = "Mixed")

            // Contact Information Section
            Text(
                text = "Contact Information",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 24.dp, 16.dp, 8.dp),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = Color(0xFF111418)
            )

            // Contact Information Fields
            PreviewDetailItem(label = "Phone Number", value = "+254712345678")
            PreviewDetailItem(label = "Email", value = "info@greenwoodhigh.edu")
            PreviewDetailItem(label = "Website", value = "www.greenwoodhigh.edu")

            // Location Information Section
            Text(
                text = "Location Information",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 24.dp, 16.dp, 8.dp),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = Color(0xFF111418)
            )

            // Location Information Fields
            PreviewDetailItem(label = "Region", value = "Nairobi")
            PreviewDetailItem(label = "Diocese", value = "Nairobi Diocese")
            PreviewDetailItem(label = "County", value = "Nairobi")
            PreviewDetailItem(label = "Sub-County", value = "Westlands")
            PreviewDetailItem(label = "Location", value = "Parklands")
            PreviewDetailItem(label = "Address", value = "123 Oak St, Anytown")
        }
    }
}

@Composable
private fun PreviewDetailItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFE5E7EB),
                shape = RoundedCornerShape(0.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(0.3f),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF6B7280)
                )
            )
            Text(
                text = value,
                modifier = Modifier.weight(0.7f),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF111418)
                )
            )
        }
    }
}