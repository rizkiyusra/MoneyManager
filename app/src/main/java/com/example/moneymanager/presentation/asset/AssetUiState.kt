package com.example.moneymanager.presentation.asset

data class AssetUiModel(
    val id: Int,
    val name: String,
    val type: String,
    val balance: String,
    val convertedBalance: String
)