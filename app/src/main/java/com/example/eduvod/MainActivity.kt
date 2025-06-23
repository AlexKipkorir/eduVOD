package com.example.eduvod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.eduvod.ui.screens.AddSchoolScreen
import com.example.eduvod.ui.screens.DashboardScreen
import com.example.eduvod.ui.screens.EditSchoolScreen
import com.example.eduvod.ui.screens.GradesManagementScreen
import com.example.eduvod.ui.screens.LoginScreen
import com.example.eduvod.ui.screens.ManageSchoolAdminsScreen
import com.example.eduvod.ui.screens.SchoolDetailsScreen
import com.example.eduvod.ui.screens.SchoolManagementScreen
import com.example.eduvod.ui.screens.SplashScreen
import com.example.eduvod.ui.screens.StreamViewScreen
import com.example.eduvod.ui.screens.SystemConfigScreen
import com.example.eduvod.ui.screens.UserManagementScreen
import com.example.eduvod.ui.theme.EduVODTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.example.eduvod.ui.screens.SchoolAdminsScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            EduVODTheme {
                EduVODApp()
            }
        }
    }
}

@Composable
fun EduVODApp() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        EduVODNavHost(navController = navController, contentPadding = padding)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EduVODNavHost(
    navController: NavHostController,
    contentPadding: PaddingValues
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = "splash",
        enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) }
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("dashboard") {
            DashboardScreen(navController)
        }
        composable("schools") {
            SchoolManagementScreen(navController)
        }
        composable("add_school?schoolName={schoolName}") { backStackEntry ->
            val schoolName = backStackEntry.arguments?.getString("schoolName")
            AddSchoolScreen(
                navController = navController,
                prefillSchoolName = schoolName,
                viewModel = viewModel()
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
        composable("edit_school/{schoolName}") { backStackEntry ->
            val schoolName = backStackEntry.arguments?.getString("schoolName") ?: return@composable
            EditSchoolScreen(navController, schoolName)
        }
        composable("users") {
            UserManagementScreen(navController)
        }
        composable("grades") {
            GradesManagementScreen(navController)
        }
        composable("view_streams/{gradeName}") { backStackEntry ->
            val gradeName = backStackEntry.arguments?.getString("gradeName") ?: return@composable
            StreamViewScreen(navController, gradeName)
        }
        composable("school_admins") {
            SchoolAdminsScreen(navController = navController, viewModel = viewModel())
        }
    }
}

