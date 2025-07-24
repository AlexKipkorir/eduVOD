package com.example.eduvod.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.eduvod.ui.theme.Typography
import androidx.compose.material3.Shapes


private val ModernLightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextOnPrimary,
    primaryContainer = LightBlue,
    onPrimaryContainer = DarkBlue,
    secondary = SecondaryBlue,
    onSecondary = TextOnPrimary,
    secondaryContainer = SurfaceBlue,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentBlue,
    onTertiary = TextOnPrimary,
    background = BackgroundBlue,
    onBackground = TextPrimary,
    surface = Color.White,
    onSurface = OnSurfaceBlue,
    surfaceVariant = SurfaceBlue,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = TextOnPrimary,
    outline = TextSecondary.copy(alpha = 0.3f),
    outlineVariant = TextSecondary.copy(alpha = 0.1f)
)

private val ModernDarkColorScheme = darkColorScheme(
    primary = DarkPrimaryBlue,
    onPrimary = TextOnPrimary,
    primaryContainer = DarkBlue,
    onPrimaryContainer = DarkSecondaryBlue,
    secondary = DarkSecondaryBlue,
    onSecondary = TextOnPrimary,
    secondaryContainer = DarkSurfaceBlue,
    onSecondaryContainer = DarkOnSurface,
    tertiary = AccentBlue,
    onTertiary = TextOnPrimary,
    background = DarkBackgroundBlue,
    onBackground = DarkOnSurface,
    surface = DarkSurfaceBlue,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceBlue,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = TextOnPrimary,
    outline = TextSecondary.copy(alpha = 0.4f),
    outlineVariant = TextSecondary.copy(alpha = 0.2f)
)


@Composable
fun EduVODTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to maintain consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ModernDarkColorScheme
        else -> ModernLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ModernTypography,
        shapes = ModernShapes,
        content = content
    )
}
val ModernTypography = Typography(
    // Define custom fonts/sizes here if needed
)

val ModernShapes: Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

