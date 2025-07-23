package com.example.eduvod.ui.screens.systemconfiguration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eduvod.ui.screens.AppScaffold

@Composable
fun SystemConfigScreen(
    navController: NavHostController
) {
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

    val configSections = listOf(
        "School Type" to Pair(Icons.Default.School, "schoolType"),
        "School Category" to Pair(Icons.Default.AccountBalance, "schoolCategory"),
        "Curriculum" to Pair(Icons.Default.MenuBook, "schoolCurriculum"),
        "Region" to Pair(Icons.Default.Place, "regionConfig")
    )

    AppScaffold(
        title = "System Configuration",
        snackbarHostState = snackbarHostState,
        showTopBar = true,
        showBackButton = true,
        onBack = { navController.popBackStack() },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            configSections.forEach { (title, iconAndRoute) ->
                val (icon, route) = iconAndRoute
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { navController.navigate(route) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = Color(0xFF0D47A1),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0D47A1)
                        )
                    }
                }
            }
        }
    }
}




