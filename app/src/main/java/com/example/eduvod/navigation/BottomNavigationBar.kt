package com.example.eduvod.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.eduvod.viewmodel.AuthViewModel
import com.example.eduvod.viewmodel.SystemConfigViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.eduvod.EduVODNavHost

// Sealed class for screens
sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Dashboard : Screen("dashboard", Icons.Default.Home, "Home")
    object Schools : Screen("schools", Icons.Default.School, "Schools")
    object Grades : Screen("grades", Icons.Default.List, "Grades")
    object Users : Screen("users", Icons.Default.People, "Users")
    object Config : Screen("config", Icons.Default.Settings, "Config")
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Dashboard,
        Screen.Schools,
        Screen.Grades,
        Screen.Users,
        Screen.Config
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun EduVODApp(
    navController: NavHostController = rememberNavController(),
    viewModel: SystemConfigViewModel = viewModel(),
    authViewModel: AuthViewModel
) {
    // List of main screen routes where bottom bar should be visible
    val mainScreens = listOf(
        Screen.Dashboard.route,
        Screen.Schools.route,
        Screen.Grades.route,
        Screen.Users.route,
        Screen.Config.route
    )

    // Get current route to determine if bottom bar should be shown
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in mainScreens

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        EduVODNavHost(
            navController = navController,
            contentPadding = innerPadding,
            authViewModel = authViewModel
        )
    }
}