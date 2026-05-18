package com.jeeve.jeevabindu.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val EmergencyRed = Color(0xFFC62828)
val ReadyGreen = Color(0xFF2E7D32)
val CreamBackground = Color(0xFFFFF8F0)
val TextPrimary = Color(0xFF1B1B1B)
val TextSecondary = Color(0xFF5C5C5C)

private val LightColors = lightColorScheme(
    primary = EmergencyRed,
    onPrimary = Color.White,
    secondary = ReadyGreen,
    onSecondary = Color.White,
    background = CreamBackground,
    onBackground = TextPrimary,
    surface = Color.White,
    onSurface = TextPrimary,
    error = EmergencyRed
)

@Composable
fun JeevaBinduTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
