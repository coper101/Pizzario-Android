package com.darealReally.pizzario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.darealReally.pizzario.ui.PizzarioApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // extend content to status bar and gesture navigation bar (fullscreen)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PizzarioApp()
        }
    }
}