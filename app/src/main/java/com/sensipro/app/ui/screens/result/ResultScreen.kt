package com.sensipro.app.ui.screens.result

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sensipro.app.R
import com.sensipro.app.sensitivity.SmartTuneOption
import com.sensipro.app.ui.MainViewModel
import com.sensipro.app.ui.components.GlassCard
import com.sensipro.app.ui.components.NeonButton
import com.sensipro.app.ui.components.SecondaryButton
import com.sensipro.app.ui.components.SectionTitle
import com.sensipro.app.ui.components.SensitivityValueChip
import com.sensipro.app.ui.components.StatRow
import com.sensipro.app.ui.theme.TextPrimary

@Composable
fun ResultScreen(viewModel: MainViewModel) {
    val result by viewModel.currentResult.collectAsState()
    val deviceInfo by viewModel.deviceInfo.collectAsState()
    val context = LocalContext.current

    var smartTuneMenuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(stringResource(R.string.result_title), color = TextPrimary, style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        }

        item {
            deviceInfo?.let { info ->
                GlassCard {
                    SectionTitle(stringResource(R.string.device_profile_title))
                    StatRow(stringResource(R.string.model), info.model)
                    StatRow(stringResource(R.string.total_ram), com.sensipro.app.device.DeviceAnalyzer.formatBytes(info.totalRamBytes))
                    StatRow(stringResource(R.string.screen_resolution), "${info.screenWidthPx}x${info.screenHeightPx}")
                    StatRow(stringResource(R.string.refresh_rate), "${info.refreshRateHz.toInt()} Hz")
                }
            }
        }

        item {
            result?.let { res ->
                GlassCard {
                    SectionTitle(stringResource(R.string.result_title))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item { SensitivityValueChip(stringResource(R.string.sensitivity_general), res.values.general) }
                        item { SensitivityValueChip(stringResource(R.string.sensitivity_red_dot), res.values.redDot) }
                        item { SensitivityValueChip(stringResource(R.string.sensitivity_2x), res.values.scope2x) }
                        item { SensitivityValueChip(stringResource(R.string.sensitivity_4x), res.values.scope4x) }
                        item { SensitivityValueChip(stringResource(R.string.sensitivity_sniper), res.values.sniper) }
                        item { SensitivityValueChip(stringResource(R.string.sensitivity_free_look), res.values.freeLook) }
                    }
                }
            }
        }

        item {
            result?.let { res ->
                GlassCard {
                    SectionTitle(stringResource(R.string.dpi_recommendation))
                    StatRow(stringResource(R.string.dpi_current), "${res.dpi.currentDensityDpi}")
                    StatRow(stringResource(R.string.dpi_recommendation), "${res.dpi.recommendedDpi}")
                    StatRow(stringResource(R.string.dpi_range), "${res.dpi.rangeLow} - ${res.dpi.rangeHigh}")
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                SecondaryButton(
                    text = stringResource(R.string.copy_settings),
                    onClick = { result?.let { copyToClipboard(context, it) } },
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.share),
                    onClick = { result?.let { shareResult(context, it) } },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            NeonButton(
                text = stringResource(R.string.save_profile),
                onClick = { viewModel.saveCurrentToHistory() }
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                SecondaryButton(
                    text = stringResource(R.string.recalculate),
                    onClick = { viewModel.generateRecommendation() },
                    modifier = Modifier.weight(1f)
                )
                Row(modifier = Modifier.weight(1f)) {
                    SecondaryButton(
                        text = stringResource(R.string.smart_tune),
                        onClick = { smartTuneMenuExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(expanded = smartTuneMenuExpanded, onDismissRequest = { smartTuneMenuExpanded = false }) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.smart_tune_stable)) }, onClick = {
                            smartTuneMenuExpanded = false
                            viewModel.recalculate(SmartTuneOption.MORE_STABLE)
                        })
                        DropdownMenuItem(text = { Text(stringResource(R.string.smart_tune_faster_drag)) }, onClick = {
                            smartTuneMenuExpanded = false
                            viewModel.recalculate(SmartTuneOption.FASTER_DRAG)
                        })
                        DropdownMenuItem(text = { Text(stringResource(R.string.smart_tune_close_control)) }, onClick = {
                            smartTuneMenuExpanded = false
                            viewModel.recalculate(SmartTuneOption.CLOSE_RANGE_CONTROL)
                        })
                        DropdownMenuItem(text = { Text(stringResource(R.string.smart_tune_long_control)) }, onClick = {
                            smartTuneMenuExpanded = false
                            viewModel.recalculate(SmartTuneOption.LONG_RANGE_CONTROL)
                        })
                        DropdownMenuItem(text = { Text(stringResource(R.string.smart_tune_balanced)) }, onClick = {
                            smartTuneMenuExpanded = false
                            viewModel.recalculate(SmartTuneOption.BALANCED)
                        })
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

private fun copyToClipboard(context: Context, result: com.sensipro.app.sensitivity.SensitivityResult) {
    val text = "General: ${result.values.general}, RedDot: ${result.values.redDot}, " +
        "2x: ${result.values.scope2x}, 4x: ${result.values.scope4x}, " +
        "Sniper: ${result.values.sniper}, FreeLook: ${result.values.freeLook}"
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("SensiPro", text))
}

private fun shareResult(context: Context, result: com.sensipro.app.sensitivity.SensitivityResult) {
    val text = "SensiPro — General: ${result.values.general}, RedDot: ${result.values.redDot}, " +
        "2x: ${result.values.scope2x}, 4x: ${result.values.scope4x}, " +
        "Sniper: ${result.values.sniper}, FreeLook: ${result.values.freeLook}"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, null))
}
