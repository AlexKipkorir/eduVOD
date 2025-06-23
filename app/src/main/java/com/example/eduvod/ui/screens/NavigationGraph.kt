package com.example.eduvod.ui.screens

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.NavHostController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(navController: NavHostController) {
    AnimatedNavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = { slideInHorizontally { 300 } + fadeIn() },
        exitTransition = { slideOutHorizontally { -300 } + fadeOut() },
        popEnterTransition = { slideInHorizontally { -300 } + fadeIn() },
        popExitTransition = { slideOutHorizontally { 300 } + fadeOut() }
    ) {
        composable("login") { LoginScreen(navController) }
        composable("dashboard") { DashboardScreen(navController) }
        composable("Schools Management") { SchoolManagementScreen(navController) }
        composable("add_school?schoolName={schoolName}") { backStackEntry ->
            val schoolName = backStackEntry.arguments?.getString("schoolName")
            AddSchoolScreen(
                navController = navController,
                prefillSchoolName = schoolName,
                viewModel = viewModel()
            )
        }
        composable("edit_school/{schoolName}") {
            val schoolName = it.arguments?.getString("schoolName") ?: ""
            EditSchoolScreen(navController, schoolName)
        }
        composable("school_details/{schoolName}") {
            val schoolName = it.arguments?.getString("schoolName") ?: ""
            SchoolDetailsScreen(navController, schoolName)
        }
        composable("manage_admins/{schoolName}") {
            val schoolName = it.arguments?.getString("schoolName") ?: ""
            ManageSchoolAdminsScreen(navController, schoolName)
        }
    }
}