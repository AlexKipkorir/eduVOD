package com.example.eduvod

import android.content.Context
import android.os.Bundle
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.eduvod.datastore.UserPreferences
import com.example.eduvod.retrofit.ApiClient
import com.example.eduvod.ui.screens.dashboard.DashboardScreen
import com.example.eduvod.navigation.EduVODApp
import com.example.eduvod.ui.screens.auth.LoginScreen
import com.example.eduvod.ui.screens.auth.SplashScreen
import com.example.eduvod.ui.screens.gradesmanagement.GradesManagementScreen
import com.example.eduvod.ui.screens.schoolmanagement.AddSchoolScreen
import com.example.eduvod.ui.screens.schoolmanagement.EditSchoolScreen
import com.example.eduvod.ui.screens.schoolmanagement.ManageSchoolAdminsScreen
import com.example.eduvod.ui.screens.schoolmanagement.SchoolAdminsScreen
import com.example.eduvod.ui.screens.schoolmanagement.SchoolDetailsScreen
import com.example.eduvod.ui.screens.schoolmanagement.SchoolManagementScreen
import com.example.eduvod.ui.screens.systemconfiguration.CurriculumScreen
import com.example.eduvod.ui.screens.systemconfiguration.RegionScreen
import com.example.eduvod.ui.screens.systemconfiguration.SchoolCategoryScreen
import com.example.eduvod.ui.screens.systemconfiguration.SchoolTypeScreen
import com.example.eduvod.ui.screens.systemconfiguration.SystemConfigScreen
import com.example.eduvod.ui.screens.usermanagement.UserManagementScreen
import com.example.eduvod.ui.theme.EduVODTheme
import com.example.eduvod.viewmodel.AuthViewModel
import com.example.eduvod.viewmodel.DashboardViewModel
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
                LoginScreen(
                    navController = navController,
                    authViewModel = authViewModel)
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
                SchoolDetailsScreen(navController, schoolName, viewModel = viewModel())
            }
            composable("manage_admins/{schoolName}") { backStackEntry ->
                val schoolName = backStackEntry.arguments?.getString("schoolName")
                ManageSchoolAdminsScreen(navController, schoolName, viewModel = viewModel())
            }
            composable(
                "edit_school/{schoolId}",
                arguments = listOf(navArgument("schoolId") { type = NavType.IntType })
            ) { backStackEntry ->
                val schoolId = backStackEntry.arguments?.getInt("schoolId") ?: 0
                EditSchoolScreen(
                    navController,
                    schoolId,
                    viewModel = viewModel(),
                    configViewModel = viewModel()
                )
            }
            composable("users") {
                UserManagementScreen(navController, viewModel = viewModel() )
            }
            composable("grades") {
                GradesManagementScreen(navController, viewModel = viewModel())
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
