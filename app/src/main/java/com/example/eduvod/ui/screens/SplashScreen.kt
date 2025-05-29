package com.example.eduvod.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    // Fade animation
    val fadeAnim = rememberInfiniteTransition(label = "fadeAnimation")
    val alpha by fadeAnim.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaAnim"
    )

    // Navigate after 2.5 seconds
    LaunchedEffect(Unit) {
        delay(2500)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1565C0)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "eduVOD",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fading Caption
            Text(
                text = "Equal Education for All",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                ),
                modifier = Modifier.alpha(alpha)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    val navController = rememberNavController()
    SplashScreen(navController = navController)
}
