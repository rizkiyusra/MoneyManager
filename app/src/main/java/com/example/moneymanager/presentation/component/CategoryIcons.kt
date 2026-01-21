package com.example.moneymanager.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun getCategoryIcon(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "fastfood", "makanan" -> Icons.Default.Fastfood
        "restaurant", "restoran" -> Icons.Default.Restaurant
        "shopping", "belanja" -> Icons.Default.ShoppingBag
        "transport", "kendaraan" -> Icons.Default.DirectionsCar
        "salary", "gaji" -> Icons.Default.AttachMoney
        "medical", "kesehatan" -> Icons.Default.MedicalServices
        "wallet", "tunai" -> Icons.Default.AccountBalanceWallet
        "category", "umum", "lain-lain" -> Icons.Default.Category
        else -> Icons.Default.Category
    }
}