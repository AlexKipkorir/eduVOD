package com.example.eduvod.ui.screens.systemconfiguration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.eduvod.ui.theme.EduVODTheme

@Composable
fun SystemConfigScreen(
    navController: NavHostController
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF111418)
                )
            }

            Text(
                text = "System Configuration",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111418)
            )
        }

        // Configuration Items
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            ConfigItem(
                icon = Icons.Default.School,
                title = "School Type",
                description = "Manage the types of schools available",
                onClick = { navController.navigate("schoolType") }
            )

            ConfigItem(
                icon = Icons.Default.AccountBalance,
                title = "School Category",
                description = "Manage school categories",
                onClick = { navController.navigate("schoolCategory") }
            )

            ConfigItem(
                icon = Icons.Default.MenuBook,
                title = "Curriculum",
                description = "Manage curricula",
                onClick = { navController.navigate("schoolCurriculum") }
            )

//            ConfigItem(
//                icon = Icons.Default.Place,
//                title = "Region",
//                description = "Manage regional classifications",
//                onClick = { navController.navigate("regionConfig") }
//            )
        }
    }
}

@Composable
private fun ConfigItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
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
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F2F5))
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF111418)
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color(0xFF60758A)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Navigate",
                tint = Color(0xFF111418),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SystemConfigScreenPreview() {
    EduVODTheme {
        SystemConfigScreen(rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun SystemConfigScreenDarkPreview() {
    EduVODTheme(darkTheme = true) {
        SystemConfigScreen(rememberNavController())
    }
}