package com.sensipro.app.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sensipro.app.R
import com.sensipro.app.ui.MainViewModel
import com.sensipro.app.ui.components.GlassCard
import com.sensipro.app.ui.theme.DangerRed
import com.sensipro.app.ui.theme.NeonCyan
import com.sensipro.app.ui.theme.TextPrimary
import com.sensipro.app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: MainViewModel) {
    val history by viewModel.history.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.history_title), color = TextPrimary, fontWeight = FontWeight.Bold)
                    if (history.isNotEmpty()) {
                        TextButton(onClick = { showClearDialog = true }) {
                            Text(stringResource(R.string.clear_all), color = DangerRed)
                        }
                    }
                }
            }

            if (history.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.history_empty),
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 40.dp)
                    )
                }
            }

            items(history, key = { it.id }) { entry ->
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.layout.Column {
                            Text(entry.deviceModel, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            Text(entry.profile, color = NeonCyan, style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
                            Text(
                                dateFormat.format(Date(entry.timestampMillis)),
                                color = TextSecondary,
                                style = androidx.compose.material3.MaterialTheme.typography.labelMedium
                            )
                            Text(
                                "G:${entry.general} RD:${entry.redDot} 2x:${entry.scope2x} 4x:${entry.scope4x} Sn:${entry.sniper} FL:${entry.freeLook}",
                                color = TextSecondary,
                                style = androidx.compose.material3.MaterialTheme.typography.labelMedium
                            )
                        }
                        IconButton(onClick = { viewModel.deleteHistoryEntry(entry.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete), tint = DangerRed)
                        }
                    }
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.clear_all)) },
            text = { Text(stringResource(R.string.clear_all_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearHistory()
                    showClearDialog = false
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}
