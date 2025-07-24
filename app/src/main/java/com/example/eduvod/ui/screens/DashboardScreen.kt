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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.Dp
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header section
                    item {
                        Column {
                            Text(
                                "School Admin Dashboard",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Comprehensive overview of school statistics",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Schools by Region
                    item {
                        SectionTitle("Schools by Region")
                    }
                    item {
                        StaggeredAnimatedCard(index = 0) {
                            stats?.schoolsPerRegion?.let { data ->
                                BarChartCard(
                                    title = "Schools by Region",
                                    data = data,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Students by Gender
                    item {
                        SectionTitle("Students by Gender")
                    }
                    item {
                        StaggeredAnimatedCard(index = 1) {
                            TwoColumnDataCard(
                                "Male" to (stats?.studentCountByGender?.get("MALE")?.toString() ?: "0"),
                                "Female" to (stats?.studentCountByGender?.get("FEMALE")?.toString() ?: "0"),
                                icon = Icons.Default.Group,
                                primaryColor = MaterialTheme.colorScheme.primary,
                                secondaryColor = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    // Differently Abled Students
                    item {
                        SectionTitle("Differently Abled Students")
                    }
                    item {
                        StaggeredAnimatedCard(index = 2) {
                            TwoColumnDataCard(
                                "Male" to (stats?.differentlyAbledByGender?.get("MALE")?.toString() ?: "0"),
                                "Female" to (stats?.differentlyAbledByGender?.get("FEMALE")?.toString() ?: "0"),
                                icon = Icons.Default.Accessibility,
                                primaryColor = MaterialTheme.colorScheme.tertiary,
                                secondaryColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        }
                    }

                    // Teachers by Gender
                    item {
                        SectionTitle("Teachers by Gender")
                    }
                    item {
                        StaggeredAnimatedCard(index = 3) {
                            TwoColumnDataCard(
                                "Male" to (stats?.teacherCountByGender?.get("MALE")?.toString() ?: "0"),
                                "Female" to (stats?.teacherCountByGender?.get("FEMALE")?.toString() ?: "0"),
                                icon = Icons.Default.Person,
                                primaryColor = MaterialTheme.colorScheme.secondary,
                                secondaryColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        }
                    }

                    // Number of Guardians
                    item {
                        SectionTitle("Number of Guardians")
                    }
                    item {
                        StaggeredAnimatedCard(index = 4) {
                            stats?.let {
                                SimpleDataCard(
                                    label = "Total Guardians",
                                    value = it.guardianCount.toString(),
                                    icon = Icons.Default.FamilyRestroom,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Students by Class
                    item {
                        SectionTitle("Students by Class")
                    }
                    item {
                        StaggeredAnimatedCard(index = 5) {
                            stats?.studentsPerClass?.let { data ->
                                BarChartCard(
                                    title = "Students by Class",
                                    data = data,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun SimpleDataCard(
    label: String, value: String,
    icon: ImageVector,
    color: Color
    ) {
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
fun TwoColumnDataCard(
    vararg values: Pair<String, Any>,
    icon: ImageVector,
    primaryColor: Color = Color(0xFF1565C0),
    secondaryColor: Color = Color(0xFF0D47A1),
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { it }),
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    values.forEach { (label, value) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = value.toString(),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = secondaryColor
                            )
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
    val scrollState = rememberScrollState()
    val itemsToShow = 4
    val itemHeight = 48.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 300.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Box(
                modifier = Modifier
                    .height(itemHeight * itemsToShow)
                    .verticalScroll(scrollState)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    data.entries.forEach { (label, value) ->
                        BarItem(
                            label = label,
                            value = value,
                            maxValue = maxValue,
                            color = color,
                            height = itemHeight - 12.dp
                        )
                    }
                }
            }

            if (data.size > itemsToShow) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Scroll for more →",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun BarItem(
    label: String,
    value: Int,
    maxValue: Int,
    color: Color,
    height: Dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(80.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(height)
                .background(
                    color = color.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(value.toFloat() / maxValue.coerceAtLeast(1))
                    .background(
                        color = color,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}





