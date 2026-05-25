package com.apulum.tenis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.apulum.tenis.ui.navigation.AppNavigation
import com.apulum.tenis.ui.theme.ApulumTenisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
        setContent {
            ApulumTenisTheme {
                AppNavigation()
            }
        }
    }
}
