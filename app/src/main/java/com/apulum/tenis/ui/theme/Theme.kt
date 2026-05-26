package com.apulum.tenis.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ApulumGreen,
    onPrimary = Color.White,
    secondary = ApulumOrange,
    background = ApulumBackground,
    surface = ApulumCardBackground,
    onBackground = ApulumTextPrimary,
    onSurface = ApulumTextPrimary,
    error = ApulumError,
    onError = ApulumOnError,
    errorContainer = ApulumErrorContainer,
    onErrorContainer = ApulumOnErrorContainer
)

@Composable
fun ApulumTenisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
