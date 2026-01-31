package com.example.moneymanager.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

val categoryIconKeys = listOf(
    "fastfood", "restaurant", "cafe", "bar",
    "car", "bike", "bus", "train", "flight", "gas",
    "shopping", "cart", "store", "gift",
    "home", "electricity", "water", "wifi", "phone", "repair",
    "salary", "wallet", "bank", "card", "investment", "briefcase",
    "medical", "fitness", "person", "child",
    "school", "book", "movie", "game", "music", "pet",
    "category", "star", "warning", "info"
)

@Composable
fun getCategoryIcon(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "fastfood", "makanan" -> Icons.Default.Fastfood
        "restaurant", "restoran" -> Icons.Default.Restaurant
        "cafe", "kopi" -> Icons.Default.LocalCafe
        "bar", "minum" -> Icons.Default.LocalBar

        "transport", "kendaraan", "car" -> Icons.Default.DirectionsCar
        "bike", "motor" -> Icons.AutoMirrored.Filled.DirectionsBike
        "bus" -> Icons.Default.DirectionsBus
        "train", "kereta" -> Icons.Default.Train
        "flight", "pesawat", "travel" -> Icons.Default.Flight
        "gas", "bensin" -> Icons.Default.LocalGasStation

        "shopping", "belanja" -> Icons.Default.ShoppingBag
        "cart", "keranjang" -> Icons.Default.ShoppingCart
        "store", "toko" -> Icons.Default.Store
        "gift", "hadiah" -> Icons.Default.CardGiftcard

        "home", "rumah" -> Icons.Default.Home
        "electricity", "listrik" -> Icons.Default.Bolt
        "water", "air" -> Icons.Default.WaterDrop
        "wifi", "internet" -> Icons.Default.Wifi
        "phone", "pulsa" -> Icons.Default.PhoneAndroid
        "repair", "servis" -> Icons.Default.Build

        "salary", "gaji" -> Icons.Default.AttachMoney
        "wallet", "dompet" -> Icons.Default.AccountBalanceWallet
        "bank" -> Icons.Default.AccountBalance
        "card", "kartu" -> Icons.Default.CreditCard
        "investment", "investasi" -> Icons.AutoMirrored.Filled.TrendingUp
        "briefcase", "kerja" -> Icons.Default.Work

        "medical", "kesehatan" -> Icons.Default.MedicalServices
        "fitness", "olahraga" -> Icons.Default.FitnessCenter
        "person", "orang" -> Icons.Default.Person
        "child", "anak" -> Icons.Default.ChildCare

        "school", "sekolah" -> Icons.Default.School
        "book", "buku" -> Icons.AutoMirrored.Filled.MenuBook
        "movie", "nonton" -> Icons.Default.Movie
        "game" -> Icons.Default.SportsEsports
        "music", "musik" -> Icons.Default.MusicNote
        "pet", "hewan" -> Icons.Default.Pets

        "star", "favorit" -> Icons.Default.Star
        "warning" -> Icons.Default.Warning
        "info" -> Icons.Default.Info
        else -> Icons.Default.Category
    }
}