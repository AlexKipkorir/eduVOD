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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

    val loginState by authViewModel.loginState.collectAsState()
    LaunchedEffect(loginState) {
        if (loginState is LoginState.LoggedOut) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
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
        showBottomBar = true,
        showLogout = true,
        snackbarHostState = snackbarHostState,
        bottomBarContent = { BottomNavigationBar(navController) },
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
                stats?.schoolsByRegion?.let { data ->
                    val chartItems = data.map { StatItem(label = it.key, value = it.value) }
                    BarChartCard(title = "Schools by Region", data = chartItems)
                }
            }

            SectionTitle("Students by Gender")
            StaggeredAnimatedCard(index = 1) {
                TwoColumnDataCard(
                    "Male" to (stats?.studentsByGender?.get("Male") ?: "0"),
                    "Female" to (stats?.studentsByGender?.get("Female") ?: "0"),
                    icon = Icons.Default.Group
                )
            }

            SectionTitle("Differently Abled Students")
            StaggeredAnimatedCard(index = 2) {
                TwoColumnDataCard(
                    "Male" to (stats?.differentlyAbled?.get("Male") ?: "0"),
                    "Female" to (stats?.differentlyAbled?.get("Female") ?: "0"),
                    icon = Icons.Default.Accessibility
                )
            }

            SectionTitle("Teachers by Gender")
            StaggeredAnimatedCard(index = 3) {
                TwoColumnDataCard(
                    "Male" to (stats?.teachersByGender?.get("Male") ?: "0"),
                    "Female" to (stats?.teachersByGender?.get("Female") ?: "0"),
                    icon = Icons.Default.Person
                )
            }

            SectionTitle("Number of Guardians")
            StaggeredAnimatedCard(index = 4) {
                stats?.let {
                    SimpleDataCard(
                        label = "Total Guardians",
                        value = it.guardiansCount.toString(),
                        icon = Icons.Default.FamilyRestroom
                    )
                }
            }

            SectionTitle("Students by Class/Grade/Stream")
            StaggeredAnimatedCard(index = 5) {
                stats?.studentsByClass?.let { data ->
                    val chartItems = data.map { StatItem(label = it.key, value = it.value) }
                    BarChartCard(title = "Students by Stream", data = chartItems)
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
    data: List<StatItem>,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF1976D2)
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF0D47A1)
            )
            Spacer(modifier = Modifier.height(16.dp))

            val maxValue = data.maxOfOrNull { it.value } ?: 1

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { (label, value) ->
                    val barHeightRatio = value.toFloat() / maxValue

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .height(120.dp),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .fillMaxHeight(barHeightRatio)
                                .background(barColor, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = value.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

data class StatItem(val label: String, val value: Int)




