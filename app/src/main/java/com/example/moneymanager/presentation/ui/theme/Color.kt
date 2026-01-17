package com.example.moneymanager.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Custom semantic colors
private val GreenLight = Color(0xFF81C784)  // For dark theme
private val GreenDark = Color(0xFF4CAF50)   // For light theme
private val RedLight = Color(0xFFE57373)    // For dark theme
private val RedDark = Color(0xFFF44336)     // For light theme
private val BlueLight = Color(0xFF64B5F6)   // For dark theme
private val BlueDark = Color(0xFF2196F3)    // For light theme
private val OrangeLight = Color(0xFFFFB74D) // For dark theme
private val OrangeDark = Color(0xFFFF9800)  // For light theme

// Semantic color extensions
val ColorScheme.income: Color
    @Composable
    get() = if (isSystemInDarkTheme()) GreenLight else GreenDark

val ColorScheme.expense: Color
    @Composable
    get() = if (isSystemInDarkTheme()) RedLight else RedDark

val ColorScheme.transfer: Color
    @Composable
    get() = if (isSystemInDarkTheme()) BlueLight else BlueDark

val ColorScheme.warning: Color
    @Composable
    get() = if (isSystemInDarkTheme()) OrangeLight else OrangeDark