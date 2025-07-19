package com.example.eduvod

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
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
import com.example.yourapp.ui.systemconfig.SchoolTypeScreen
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

        setContent {
            EduVODTheme {
                EduVODApp()
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
fun EduVODApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel = remember { AuthViewModel(context) }
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val showBottomBar = currentRoute in listOf("dashboard", "schools", "grades", "users", "config")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                BottomNavigation(
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    val items = listOf(
                        Triple("dashboard", Icons.Default.Home, "Dashboard"),
                        Triple("schools", Icons.Default.Business, "Schools"),
                        Triple("grades", Icons.Default.Grade, "Grades"),
                        Triple("users", Icons.Default.Person, "Users"),
                        Triple("config", Icons.Default.Settings, "Config")
                    )

                    items.forEach { (route, icon, label) ->
                        BottomNavigationItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            selected = currentRoute == route,
                            onClick = {
                                if (currentRoute != route) {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    ) { padding ->
        EduVODNavHost(
            navController = navController,
            contentPadding = padding,
            authViewModel = authViewModel
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EduVODNavHost(
    navController: NavHostController,
    contentPadding: PaddingValues,
    authViewModel: AuthViewModel
) {
    val dashboardViewModel: DashboardViewModel = viewModel()

    AnimatedNavHost(
        navController = navController,
        startDestination = "splash",
        enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) }
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
            val schoolName = backStackEntry.arguments?.getString("schoolName") ?: return@composable
            SchoolDetailsScreen(navController, schoolName)
        }
        composable("manage_admins/{schoolName}") { backStackEntry ->
            val schoolName = backStackEntry.arguments?.getString("schoolName")
            ManageSchoolAdminsScreen(navController, schoolName)
        }
        composable("edit_school/{schoolId}") { backStackEntry ->
            val schoolId = backStackEntry.arguments?.getString("schoolId")?.toIntOrNull() ?: return@composable
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
        composable("edit_school/{schoolId}") { backStackEntry ->
            val schoolId = backStackEntry.arguments?.getString("schoolId")?.toIntOrNull() ?: return@composable
            EditSchoolScreen(navController, schoolId)
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
