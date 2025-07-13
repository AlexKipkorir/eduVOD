package com.example.eduvod.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eduvod.viewmodel.AuthViewModel
import com.example.eduvod.viewmodel.DashboardViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import com.example.eduvod.viewmodel.LoginState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController,
                    viewModel: DashboardViewModel,
                    authViewModel: AuthViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navItems = listOf(
        NavItem("Schools Management", Icons.Default.Business, "schools"),
        NavItem("Grades Management", Icons.Default.Grade, "grades"),
        NavItem("User Management", Icons.Default.AdminPanelSettings, "users"),
        NavItem("Systems Configuration", Icons.Default.Settings, "config"),
        NavItem("Logout", Icons.AutoMirrored.Filled.ExitToApp, "logout")
    )

    //Retrofit
    val stats by viewModel.stats.collectAsState()
    val snackbar by viewModel.snackbar.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(snackbar) {
        snackbar?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    val loginState by authViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.LoggedOut) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }


    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Text("Admin Menu", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                navItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                if (item.route == "logout") {
                                    showLogoutDialog = true
                                } else {
                                    navController.navigate(item.route)
                                }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("eduVOD Admin Dashboard", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp))
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0D47A1),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            containerColor = Color(0xFFF4F8FC)
        )
        { innerPadding ->
            //Retrofit
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
                    stats?.let { ScrollableDataCard(data = it.schoolsByRegion, icon = Icons.Default.LocationCity) }
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
                    stats?.let { ScrollableDataCard(data = it.studentsByClass, icon = Icons.Default.School) }
                }
            }

            //OG
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(innerPadding)
//                    .verticalScroll(rememberScrollState())
//                    .padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(20.dp)
//            ) {
//                SectionTitle("Schools by Region")
//                StaggeredAnimatedCard(index = 0) {
//                    ScrollableDataCard(data = viewModel.schoolsByRegion, icon = Icons.Default.LocationCity)
//                }
//
//                SectionTitle("Students by Gender")
//                StaggeredAnimatedCard(index = 1) {
//                    TwoColumnDataCard(
//                        "Male" to (viewModel.studentsByGender["Male"] ?: "0"),
//                        "Female" to (viewModel.studentsByGender["Female"] ?: "0"),
//                        icon = Icons.Default.Group
//                    )
//                }
//
//                SectionTitle("Differently Abled Students")
//                StaggeredAnimatedCard(index = 2) {
//                    TwoColumnDataCard(
//                        "Male" to (viewModel.differentlyAbledStudents["Male"] ?: "0"),
//                        "Female" to (viewModel.differentlyAbledStudents["Female"] ?: "0"),
//                        icon = Icons.Default.Accessibility
//                    )
//                }
//
//                SectionTitle("Teachers by Gender")
//                StaggeredAnimatedCard(index = 3) {
//                    TwoColumnDataCard(
//                        "Male" to (viewModel.teachersByGender["Male"] ?: "0"),
//                        "Female" to (viewModel.teachersByGender["Female"] ?: "0"),
//                        icon = Icons.Default.Person
//                    )
//                }
//
//                SectionTitle("Number of Guardians")
//                StaggeredAnimatedCard(index = 4) {
//                    SimpleDataCard(
//                        label = "Total Guardians",
//                        value = viewModel.totalGuardian,
//                        icon = Icons.Default.FamilyRestroom
//                    )
//                }
//
//                SectionTitle("Students by Class/Grade/Stream")
//                StaggeredAnimatedCard(index = 5) {
//                    ScrollableDataCard(data = viewModel.studentsByClassStream, icon = Icons.Default.School)
//                }
//            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Confirm Logout") },
                text = { Text("Are you sure you want to log out?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        scope.launch {
                            authViewModel.logout()
                            delay(300)
                            navController.navigate("splash") {
                                popUpTo("dashboard") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }) {
                        Text("Logout", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
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
fun ScrollableDataCard(
    data: Map<String, Int>,
    icon: ImageVector,
    maxHeight: Dp = 300.dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp, max = maxHeight),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1565C0))
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.padding(16.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF1565C0))
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    data.forEach { (label, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = label.toString(), color = Color.Black)
                            Text(
                                text = value.toString(),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1)
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
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

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)



