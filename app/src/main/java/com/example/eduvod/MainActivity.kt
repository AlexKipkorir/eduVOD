package com.example.eduvod

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eduvod.datastore.UserPreferences
import com.example.eduvod.retrofit.ApiClient
import com.example.eduvod.ui.screens.*
import com.example.eduvod.ui.screens.gradesmanagement.GradesManagementScreen
import com.example.eduvod.ui.screens.schoolmanagement.*
import com.example.eduvod.ui.screens.systemconfiguration.CurriculumScreen
import com.example.eduvod.ui.screens.systemconfiguration.RegionScreen
import com.example.eduvod.ui.screens.systemconfiguration.SchoolCategoryScreen
import com.example.eduvod.ui.screens.systemconfiguration.SystemConfigScreen
import com.example.eduvod.ui.screens.usermanagement.UserManagementScreen
import com.example.eduvod.ui.theme.EduVODTheme
import com.example.eduvod.viewmodel.AuthViewModel
import com.example.eduvod.viewmodel.DashboardViewModel
import com.example.eduvod.viewmodel.NavItem
import com.example.eduvod.viewmodel.SystemConfigViewModel
import com.example.eduvod.ui.screens.systemconfiguration.SchoolTypeScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val userPreferences = UserPreferences(applicationContext)

        lifecycleScope.launch {
            val token = userPreferences.authToken.firstOrNull()
            if (!token.isNullOrEmpty()) {
                ApiClient.setAuthToken(token)
            }
        }


        val authFactory = AuthViewModelFactory(applicationContext)
        val authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class.java]

        setContent {
            EduVODTheme {
                EduVODApp(authViewModel = authViewModel)
            }
        }
    }
}



class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentRoute = currentRoute(navController)

    val items = listOf(
        NavItem("Home", Icons.Default.Home, "Dashboard"),
        NavItem("schools", Icons.Default.Business, "Schools"),
        NavItem("grades", Icons.Default.Star, "Grades"),
        NavItem("users", Icons.Default.Person, "Users"),
        NavItem("config", Icons.Default.Settings, "Config")
    )

    val selectedColor = Color(0xFF1565C0)
    val unselectedColor = Color.Gray
    val backgroundColor = Color.White

    NavigationBar(
        containerColor = backgroundColor,
        contentColor = selectedColor
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(text = item.label) },
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    indicatorColor = selectedColor.copy(alpha = 0.1f),
                    unselectedIconColor = unselectedColor,
                    unselectedTextColor = unselectedColor
                )
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

    val currentRoute = currentRoute(navController)
    val baseRoutesWithBottomBar = listOf("dashboard", "schools", "grades", "users", "config")
    val showBottomBar = currentRoute != null &&
            baseRoutesWithBottomBar.any { currentRoute.startsWith(it) } &&
            !currentRoute.startsWith("login") &&
            !currentRoute.startsWith("splash")

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
@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EduVODNavHost(
    navController: NavHostController,
    contentPadding: PaddingValues,
    authViewModel: AuthViewModel
) {
    val dashboardViewModel: DashboardViewModel = viewModel()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = contentPadding.calculateBottomPadding())
    ) {
        AnimatedNavHost(
            navController = navController,
            startDestination = "splash",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                )
            }
        ) {
            composable("splash") {
                SplashScreen(navController = navController, authViewModel = authViewModel)
            }
            composable("login") {
                LoginScreen(navController = navController, authViewModel = authViewModel)
            }
            composable("dashboard") {
                DashboardScreen(
                    navController = navController,
                    viewModel = dashboardViewModel,
                    authViewModel = authViewModel
                )
            }
            composable("schools") {
                SchoolManagementScreen(navController)
            }
            composable("add_school?schoolName={schoolName}") { backStackEntry ->
                val schoolName = backStackEntry.arguments?.getString("schoolName")
                AddSchoolScreen(
                    navController = navController,
                    prefillSchoolName = schoolName,
                    schoolViewModel = viewModel()
                )
            }
            composable("config") {
                SystemConfigScreen(navController)
            }
            composable("school_details/{schoolName}") { backStackEntry ->
                val schoolName =
                    backStackEntry.arguments?.getString("schoolName") ?: return@composable
                SchoolDetailsScreen(navController, schoolName)
            }
            composable("manage_admins/{schoolName}") { backStackEntry ->
                val schoolName = backStackEntry.arguments?.getString("schoolName")
                ManageSchoolAdminsScreen(navController, schoolName)
            }
            composable("edit_school/{schoolId}") { backStackEntry ->
                val schoolId = backStackEntry.arguments?.getString("schoolId")?.toIntOrNull()
                    ?: return@composable
                EditSchoolScreen(navController, schoolId)
            }
            composable("users") {
                UserManagementScreen(navController)
            }
            composable("grades") {
                GradesManagementScreen(navController)
            }
//        composable("view_streams/{gradeName}") { backStackEntry ->
//            val gradeName = backStackEntry.arguments?.getString("gradeName") ?: return@composable
//            StreamViewScreen(navController, gradeName)
//        }
            composable("school_admins") {
                SchoolAdminsScreen(navController = navController, viewModel = viewModel())
            }
            composable("schoolType") {
                SchoolTypeScreen(viewModel = viewModel(), navController = navController)
            }
            composable("schoolCategory") {
                SchoolCategoryScreen(viewModel = viewModel(), navController = navController)
            }
            composable("schoolCurriculum") {
                CurriculumScreen(viewModel = viewModel(), navController = navController)
            }
            composable("regionConfig") {
                RegionScreen(viewModel = viewModel(), navController = navController)
            }
        }
    }


}
