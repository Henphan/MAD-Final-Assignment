package com.example.travelplanner_finalassignment.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE7BC83),
    onPrimary = Color(0xFF241C1A),
    primaryContainer = Color(0xFF594231),
    onPrimaryContainer = Color(0xFFF4E5D2),

    secondary = Color(0xFFD99578),
    onSecondary = Color(0xFF241C1A),
    secondaryContainer = Color(0xFF624136),
    onSecondaryContainer = Color(0xFFF4E5D2),

    tertiary = Color(0xFFB4BE98),
    onTertiary = Color(0xFF241C1A),

    background = Color(0xFF241C1A),
    onBackground = Color(0xFFF4E5D2),

    surface = Color(0xFF342925),
    onSurface = Color(0xFFF4E5D2),

    surfaceVariant = Color(0xFF493832),
    onSurfaceVariant = Color(0xFFE2CDBB),

    surfaceContainerLowest = Color(0xFF241C1A),
    surfaceContainerLow = Color(0xFF2D2320),
    surfaceContainer = Color(0xFF342925),
    surfaceContainerHigh = Color(0xFF40312C),
    surfaceContainerHighest = Color(0xFF493832),

    outline = Color(0xFFAD9180),
    outlineVariant = Color(0xFF665149)
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

)

@Composable
fun TravelPlannerFinalAssignmentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}