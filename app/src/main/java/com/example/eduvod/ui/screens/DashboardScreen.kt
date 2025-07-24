package com.example.eduvod.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eduvod.BottomNavigationBar
import com.example.eduvod.viewmodel.AuthViewModel
import com.example.eduvod.viewmodel.DashboardViewModel
import com.example.eduvod.viewmodel.LoginState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel,
    authViewModel: AuthViewModel
) {
    val scope = rememberCoroutineScope()
    val stats by viewModel.stats.collectAsState()
    val snackbar by viewModel.snackbar.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading by viewModel.isLoading.collectAsState()
    val loginState by authViewModel.loginState.collectAsState()

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

    LaunchedEffect(snackbar) {
        snackbar?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    AppScaffold(
        title = "eduVOD Admin Dashboard",
        showTopBar = true,
        showLogout = true,
        snackbarHostState = snackbarHostState,
        onLogout = {
            scope.launch {
                authViewModel.logout()
                delay(300)
                navController.navigate("splash") {
                    popUpTo("dashboard") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    ) { innerPadding ->

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Loading dashboard stats...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            stats == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Failed to load dashboard stats.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    SectionTitle("Schools by Region")
                    StaggeredAnimatedCard(index = 0) {
                        stats?.schoolsPerRegion?.let { data ->
                            BarChartCard(
                                title = "Schools by Region",
                                data = data,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }

                    SectionTitle("Students by Gender")
                    StaggeredAnimatedCard(index = 1) {
                        TwoColumnDataCard(
                            "Male" to (stats?.studentCountByGender?.get("MALE")?.toString() ?: "0"),
                            "Female" to (stats?.studentCountByGender?.get("FEMALE")?.toString() ?: "0"),
                            icon = Icons.Default.Group
                        )
                    }

                    SectionTitle("Differently Abled Students")
                    StaggeredAnimatedCard(index = 2) {
                        TwoColumnDataCard(
                            "Male" to (stats?.differentlyAbledByGender?.get("MALE")?.toString() ?: "0"),
                            "Female" to (stats?.differentlyAbledByGender?.get("FEMALE")?.toString() ?: "0"),
                            icon = Icons.Default.Accessibility
                        )
                    }

                    SectionTitle("Teachers by Gender")
                    StaggeredAnimatedCard(index = 3) {
                        TwoColumnDataCard(
                            "Male" to (stats?.teacherCountByGender?.get("MALE")?.toString() ?: "0"),
                            "Female" to (stats?.teacherCountByGender?.get("FEMALE")?.toString() ?: "0"),
                            icon = Icons.Default.Person
                        )
                    }

                    SectionTitle("Number of Guardians")
                    StaggeredAnimatedCard(index = 4) {
                        stats?.let {
                            SimpleDataCard(
                                label = "Total Guardians",
                                value = it.guardianCount.toString(),
                                icon = Icons.Default.FamilyRestroom
                            )
                        }
                    }

                    SectionTitle("Students by Class")
                    StaggeredAnimatedCard(index = 5) {
                        stats?.studentsPerClass?.let { data ->
                            BarChartCard(
                                title = "Students by Class",
                                data = data,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = Color(0xFF0D47A1)
        )
    )
}

@Composable
fun SimpleDataCard(label: String, value: String, icon: ImageVector) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { it / 2 }),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = label, fontSize = 14.sp, color = Color.Gray) // safe
                    Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                }
            }
        }
    }
}

@Composable
fun TwoColumnDataCard(vararg values: Pair<String, Any>, icon: ImageVector) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { it }),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Icon(icon, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    values.forEach { (label, value) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(label, color = Color.Gray)
                            Text(value.toString(), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF0D47A1))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StaggeredAnimatedCard(
    index: Int,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(index) {
        delay(index * 200L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { it / 2 }),
    ) {
        content()
    }
}

@Composable
fun BarChartCard(
    title: String,
    data: Map<String, Int>,
    color: Color,
    modifier: Modifier = Modifier
) {
    val maxValue = data.values.maxOrNull() ?: 1

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable vertical list with fixed height
            Column(
                modifier = Modifier
                    .heightIn(min = 100.dp, max = 300.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                data.entries.forEach { (label, value) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.width(80.dp),

                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(20.dp)
                                .background(
                                    color = color.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(value.toFloat() / maxValue.coerceAtLeast(1))
                                    .background(
                                        color = color,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = value.toString(),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

data class StatItem(val label: String, val value: Int)




