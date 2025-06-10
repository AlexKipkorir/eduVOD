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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue=DrawerValue.Closed)
    val scope = rememberCoroutineScope()

//    val dashboardItems = listOf(
//        DashboardItem("Schools by Region", Icons.Default.LocationCity, null),
//        DashboardItem("Students by Gender", Icons.Default.Group, null),
//        DashboardItem("Differently Abled Students", Icons.Default.Accessibility, null),
//        DashboardItem("Teachers by Gender", Icons.Default.Person, null),
//        DashboardItem("Number of Guardians", Icons.Default.FamilyRestroom, null),
//        DashboardItem("Students by Class/Grade/Stream", Icons.Default.School, null)
//    )

    val navItems = listOf(
        NavItem("Schools Management", Icons.Default.Business, "schools"),
        NavItem("Grades Management", Icons.Default.Grade, "grades"),
        NavItem("User Management", Icons.Default.AdminPanelSettings, "users"),
        NavItem("Systems Configuration", Icons.Default.Settings, "config")
    )

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Admin Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                navItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        selected = false,
                        onClick = {
                            navController.navigate(item.route)
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
                        Text(
                            "eduVOD Admin Dashboard",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
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
                    ScrollableDataCard(
                        data = listOf(
                            "Nairobi" to "45",
                            "Mombasa" to "30",
                            "Kisumu" to "22",
                            "Nakuru" to "18",
                            "Eldoret" to "10",
                            "Garissa" to "8",
                            "Isiolo" to "3",
                            "Turkana" to "1"
                        ),
                        icon = Icons.Default.LocationCity
                    )
                }

                SectionTitle("Students by Gender")
                StaggeredAnimatedCard(index = 1) {
                    TwoColumnDataCard(
                        "Male" to "12,300",
                        "Female" to "11,800",
                        icon = Icons.Default.Group
                    )
                }

                SectionTitle("Differently Abled Students")
                StaggeredAnimatedCard(index = 2) {
                TwoColumnDataCard(
                    "Male" to "230",
                    "Female" to "210",
                    icon = Icons.Default.Accessibility
                )
                }

                SectionTitle("Teachers by Gender")
                StaggeredAnimatedCard(index = 3) {
                    TwoColumnDataCard(
                        "Male" to "2,500",
                        "Female" to "3,100",
                        icon = Icons.Default.Person
                    )
                }

                SectionTitle("Number of Guardians")
                StaggeredAnimatedCard(index = 4) {
                    SimpleDataCard(
                        label = "Total Guardians",
                        value = "8,000",
                        icon = Icons.Default.FamilyRestroom
                    )
                }

                SectionTitle("Students by Class/Grade/Stream")
                StaggeredAnimatedCard(index = 5) {
                    ScrollableDataCard(
                        data = listOf(
                            "Grade 1 – Stream A" to "300",
                            "Grade 2 – Stream B" to "280",
                            "Grade 3 – Stream A" to "295",
                            "Grade 4 – Stream C" to "310",
                            "Grade 5 – Stream A" to "290"
                        ),
                        icon = Icons.Default.School
                    )
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
                .height(140.dp), // ⬆️ Increase height
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
                    Icon(icon, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(label, fontSize = 14.sp, color = Color.Gray)
                    Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                }
            }
        }
    }
}



@Composable
fun TwoColumnDataCard(vararg values: Pair<String, String>, icon: ImageVector) {
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
                            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF0D47A1))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScrollableDataCard(
    data: List<Pair<String, String>>,
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
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                data.forEach { (label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, color = Color.Black)
                        Text(value, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                    }
                    Divider(modifier = Modifier.padding(vertical = 6.dp))
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
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

//@Composable
//fun DashboardCard(item: DashboardItem) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(150.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.SpaceBetween
//        ) {
//            Icon(
//                imageVector = item.icon,
//                contentDescription =  item.label,
//                tint = Color(0xFF1565C0),
//                modifier = Modifier.size(28.dp)
//            )
//            Text(
//                text = item.label,
//                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
//                color = Color.Black
//            )
//            Text(
//                text = item.value ?: "Loading...",
//                style = MaterialTheme.typography.headlineSmall,
//                color = Color(0xFF1565C0)
//            )
//        }
//    }
//}

//data class DashboardItem(
//    val label: String,
//    val icon: androidx.compose.ui.graphics.vector.ImageVector,
//    val value: String?
//)


