package com.eyt.harrypotter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesSheet(
    onDismiss: () -> Unit,
    fontSize: Int,
    updateFontSize: (Int) -> Unit,
    showBottomBar: Boolean,
    updateShowBottomBar: (Boolean) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { updateFontSize(fontSize - 1) }) {
                Icon(
                    Icons.Default.Remove,
                    "להוריד"
                )
            }
            Text(
                "גודל גופן: $fontSize"
            )
            IconButton(onClick = { updateFontSize(fontSize + 1) }) {
                Icon(
                    Icons.Default.Add,
                    "להוסיף"
                )
            }
        }
        PreferenceSwitch(
            title = "הצג שורת כלים תחתונה",
            isChecked = showBottomBar,
            onClick = {
                updateShowBottomBar(!showBottomBar)
                onDismiss()
            }
        )
    }
}