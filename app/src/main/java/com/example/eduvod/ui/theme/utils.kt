package com.example.eduvod.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun responsiveFontSize(base: Float): TextUnit {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val scaleFactor = when {
        screenWidth < 360 -> 0.85f
        screenWidth > 600 -> 1.25f
        else -> 1.0f
    }
    return (base * scaleFactor).sp
}