package com.example.eduvod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eduvod.ui.screens.AddSchoolScreen
import com.example.eduvod.ui.screens.DashboardScreen
import com.example.eduvod.ui.screens.SplashScreen
import com.example.eduvod.ui.screens.LoginScreen
import com.example.eduvod.ui.screens.ManageSchoolAdminsScreen
import com.example.eduvod.ui.screens.SchoolManagementScreen
import com.example.eduvod.ui.screens.SchoolDetailsScreen
import com.example.eduvod.ui.screens.SystemConfigScreen
import com.example.eduvod.ui.theme.EduVODTheme

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

@Composable
fun EduVODNavHost(
    navController: NavHostController,
    contentPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = Modifier.padding(contentPadding) // apply it if needed
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
            AddSchoolScreen(navController, prefillSchoolName = schoolName)
        }

        composable("config") {
            SystemConfigScreen(navController)
        }
        composable("school_details/{schoolName}") { backStackEntry ->
            val schoolName = backStackEntry.arguments?.getString("schoolName")
            SchoolDetailsScreen(navController, schoolName)
        }
        composable("manage_admins/{schoolName}") { backStackEntry ->
            val schoolName = backStackEntry.arguments?.getString("schoolName")
            ManageSchoolAdminsScreen(navController, schoolName)
        }



    }
}

