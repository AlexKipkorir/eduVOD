package com.example.eduvod.ui.screens.schoolmanagement

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.model.School
import com.example.eduvod.ui.screens.AppScaffold
import com.example.eduvod.viewmodel.SchoolManagementViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolDetailsScreen(
    navController: NavHostController,
    schoolName: String,
    viewModel: SchoolManagementViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    var school by remember { mutableStateOf<School?>(null) }
    var isLoading by remember { mutableStateOf(true) }


    LaunchedEffect(Unit) {
        isLoading = true
        delay(1000)
        school = viewModel.getSchoolByName(schoolName)
        isLoading = false
    }

    AppScaffold(
        title = "Details for: $schoolName",
        showLogout = false,
        showBackButton = true,
        onBack = { navController.popBackStack() },
        snackbarHostState = snackbarHostState,
        content = { padding ->

            Box(modifier = Modifier.fillMaxSize()) {

                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xAAFFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Color(0xFF1565C0),
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading eduVod Admins...", color = Color(0xFF1565C0))
                        }
                    }
                }

                if (!isLoading) {
                    if (school != null) {
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
                                    "School Name" to school!!.name,
                                    "MoE REG NO" to school!!.moeRegNo,
                                    "KPSA REG NO" to school!!.kpsaRegNo,
                                    "Curriculum" to school!!.curriculum,
                                    "Category" to school!!.category,
                                    "Type" to school!!.type,
                                    "Composition" to school!!.composition
                                )
                            )

                            SectionCard(
                                title = " Contact Information",
                                items = listOf(
                                    "Mobile" to school!!.mobile,
                                    "Email" to school!!.email,
                                    "Website" to school!!.website
                                )
                            )

                            SectionCard(
                                title = " Location",
                                items = listOf(
                                    "Region" to school!!.region,
                                    "Diocese" to school!!.diocese,
                                    "County" to school!!.county,
                                    "SubCounty" to school!!.subCounty,
                                    "Location" to school!!.location,
                                    "Address" to school!!.address
                                )
                            )
                        }

                        FloatingActionButton(
                            onClick = {
                                navController.navigate("edit_school/${school!!.name}")
                            },
                            containerColor = Color(0xFF1565C0),
                            contentColor = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "School not found",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    )
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


