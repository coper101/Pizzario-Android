package com.darealReally.pizzario.ui

import androidx.compose.runtime.Composable
import com.darealReally.pizzario.ui.main.MainScreen
import com.darealReally.pizzario.ui.theme.PizzarioTheme
import com.google.accompanist.insets.ProvideWindowInsets

@Composable
fun PizzarioApp() {
    PizzarioTheme {
        ProvideWindowInsets {
            MainScreen()
        }
    }
}