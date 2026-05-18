package com.nammaraste.health

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nammaraste.health.ui.navigation.NammaRasteNavGraph
import com.nammaraste.health.ui.theme.NammaRasteTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point of the application.
 * Sets up the Jetpack Compose UI with the navigation graph.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen BEFORE super.onCreate()
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NammaRasteTheme {
                NammaRasteNavGraph()
            }
        }
    }
}
