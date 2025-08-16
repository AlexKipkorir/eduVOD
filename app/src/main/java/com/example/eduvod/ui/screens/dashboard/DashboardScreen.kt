package com.example.eduvod.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduvod.model.SuperAdminDashboardResponse
import com.example.eduvod.ui.theme.EduVODTheme
import com.example.eduvod.viewmodel.AuthViewModel
import com.example.eduvod.viewmodel.DashboardViewModel
import com.example.eduvod.viewmodel.LoginState

@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = viewModel(),
    authViewModel: AuthViewModel
) {
    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val loginState by authViewModel.loginState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(loginState) {
        if (loginState is LoginState.LoggedOut) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchDashboardStats()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Dashboard",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 48.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            // Notification and Logout Icons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { /* Handle notification click */ },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                IconButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout Icon",
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Confirm Logout",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                text = {
                    Text(
                        text = "Are you sure you want to log out? You will need to log in again to access your account.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Logout", color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showLogoutDialog = false },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancel")
                    }
                },
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 8.dp,
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Loading dashboard stats...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            stats == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Failed to load dashboard stats",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.fetchDashboardStats() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            else -> {
                // Schools by Region Section
                SectionHeader(title = "Schools by Region")

                StatCard(
                    title = "Schools",
                    value = stats?.schoolsPerRegion?.values?.sum().toString(),
                    subtitle = "Total",
                    content = {
                        stats?.schoolsPerRegion?.let { data ->
                            RegionDistributionChart(data = data)
                        }
                    }
                )

                // Student Demographics Section
                SectionHeader(title = "Student Demographics")

                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        SmallStatCard(
                            title = "Gender",
                            value = "${stats?.studentCountByGender?.get("MALE") ?: 0}/${stats?.studentCountByGender?.get("FEMALE") ?: 0}"
                        )
                    }
                    item {
                        SmallStatCard(
                            title = "Differently Abled",
                            value = (stats?.differentlyAbledByGender?.values?.sum() ?: 0).toString()
                        )
                    }
                }

                // Teachers & Guardians Section
                SectionHeader(title = "Teachers & Guardians")

                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        SmallStatCard(
                            title = "Teachers",
                            value = (stats?.teacherCountByGender?.values?.sum() ?: 0).toString()
                        )
                    }
                    item {
                        SmallStatCard(
                            title = "Guardians",
                            value = (stats?.guardianCount ?: 0).toString()
                        )
                    }
                }

                // Student Distribution Section
                SectionHeader(title = "Student Distribution")

                StatCard(
                    title = "Students by Class",
                    value = (stats?.studentsPerClass?.values?.sum() ?: 0).toString(),
                    subtitle = "Total",
                    content = {
                        stats?.studentsPerClass?.let { data ->
                            HorizontalBarChart(
                                items = data.map { ChartItem(it.key, it.value.toFloat() / (data.values.maxOrNull() ?: 1)) }
                            )
                        }
                    }
                )

                StatCard(
                    title = "Students by Stream",
                    value = (stats?.studentsPerStream?.values?.sum() ?: 0).toString(),
                    subtitle = "Total",
                    content = {
                        stats?.studentsPerStream?.let { data ->
                            HorizontalBarChart(
                                items = data.map { ChartItem(it.key, it.value.toFloat() / (data.values.maxOrNull() ?: 1)) }
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun SmallStatCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier
            .width(158.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun RegionDistributionChart(data: Map<String, Int>) {
    val maxValue = data.values.maxOrNull() ?: 1
    Column {
        data.entries.forEach { (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    modifier = Modifier.width(80.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(value.toFloat() / maxValue)
                            .border(2.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }
        }
    }
}

@Composable
fun HorizontalBarChart(items: List<ChartItem>) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .horizontalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .height(180.dp)
                .padding(horizontal = 8.dp), // Add some padding
            horizontalArrangement = Arrangement.spacedBy(16.dp), // Space between bars
        ) {
            items.forEach { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.height(180.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .fillMaxHeight(item.value)
                            .border(2.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 22.dp, 16.dp, 12.dp),
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            letterSpacing = (-0.015).sp
        ),
        color = MaterialTheme.colorScheme.onBackground
    )
}

data class ChartItem(val label: String, val value: Float)

// Preview with fake ViewModel
@Composable
fun DashboardScreenPreviewContent(
    stats: SuperAdminDashboardResponse?,
    isLoading: Boolean,
    onLogout: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Dashboard",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 48.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            // Notification and Logout Icons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                IconButton(
                    onClick = onLogout,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Loading dashboard stats...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            stats == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Failed to load dashboard stats",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { /* No-op in preview */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            else -> {
                // Schools by Region Section
                SectionHeader(title = "Schools by Region")

                StatCard(
                    title = "Schools",
                    value = stats.schoolsPerRegion.values.sum().toString(),
                    subtitle = "Total",
                    content = {
                        RegionDistributionChart(data = stats.schoolsPerRegion)
                    }
                )

                // Student Demographics Section
                SectionHeader(title = "Student Demographics")

                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        SmallStatCard(
                            title = "Gender",
                            value = "${stats.studentCountByGender["MALE"] ?: 0}/${stats.studentCountByGender["FEMALE"] ?: 0}"
                        )
                    }
                    item {
                        SmallStatCard(
                            title = "Differently Abled",
                            value = (stats.differentlyAbledByGender.values.sum()).toString()
                        )
                    }
                }

                // Teachers & Guardians Section
                SectionHeader(title = "Teachers & Guardians")

                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        SmallStatCard(
                            title = "Teachers",
                            value = (stats.teacherCountByGender.values.sum()).toString()
                        )
                    }
                    item {
                        SmallStatCard(
                            title = "Guardians",
                            value = stats.guardianCount.toString()
                        )
                    }
                }

                // Student Distribution Section
                SectionHeader(title = "Student Distribution")

                StatCard(
                    title = "Students by Class",
                    value = stats.studentsPerClass.values.sum().toString(),
                    subtitle = "Total",
                    content = {
                        HorizontalBarChart(
                            items = stats.studentsPerClass.map {
                                ChartItem(it.key, it.value.toFloat() / (stats.studentsPerClass.values.maxOrNull() ?: 1))
                            }
                        )
                    }
                )

                StatCard(
                    title = "Students by Stream",
                    value = stats.studentsPerStream.values.sum().toString(),
                    subtitle = "Total",
                    content = {
                        HorizontalBarChart(
                            items = stats.studentsPerStream.map {
                                ChartItem(it.key, it.value.toFloat() / (stats.studentsPerStream.values.maxOrNull() ?: 1))
                            }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun DashboardScreenPreview() {
    EduVODTheme {
        DashboardScreenPreviewContent(
            stats = SuperAdminDashboardResponse(
                studentCountByGender = mapOf("MALE" to 550, "FEMALE" to 450),
                differentlyAbledByGender = mapOf("MALE" to 5, "FEMALE" to 5),
                teacherCountByGender = mapOf("MALE" to 150, "FEMALE" to 100),
                guardianCount = 1000,
                studentsPerClass = mapOf(
                    "Class 1" to 200,
                    "Class 2" to 250,
                    "Class 3" to 300,
                    "Class 4" to 350
                ),
                studentsPerStream = mapOf(
                    "Science" to 500,
                    "Arts" to 400,
                    "Commerce" to 300
                ),
                schoolsPerRegion = mapOf(
                    "Nairobi" to 50,
                    "Mombasa" to 30,
                    "Rift Valley" to 20,
                    "Central" to 10
                )
            ),
            isLoading = false,
            onLogout = {},
            onNotificationClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun DashboardScreenLoadingPreview() {
    EduVODTheme {
        DashboardScreenPreviewContent(
            stats = null,
            isLoading = true
        )
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun DashboardScreenErrorPreview() {
    EduVODTheme {
        DashboardScreenPreviewContent(
            stats = null,
            isLoading = false
        )
    }
}