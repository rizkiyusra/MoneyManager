package com.example.moneymanager.presentation.transaction.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.moneymanager.common.extension.toRupiah
import com.example.moneymanager.domain.model.Asset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDropdown(
    label: String,
    selectedAsset: Asset?,
    assets: List<Asset>,
    onSelect: (Asset) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedAsset?.name ?: label,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (assets.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Tidak ada aset tersedia") },
                    onClick = { expanded = false }
                )
            } else {
                assets.forEach { asset ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(asset.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "Saldo: ${asset.balance.toRupiah()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        },
                        onClick = {
                            onSelect(asset)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}