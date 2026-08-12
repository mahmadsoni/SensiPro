package com.sensipro.app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sensipro.app.R
import com.sensipro.app.device.DeviceAnalyzer
import com.sensipro.app.sensitivity.SensitivityProfile
import com.sensipro.app.ui.MainViewModel
import com.sensipro.app.ui.components.GlassCard
import com.sensipro.app.ui.components.NeonButton
import com.sensipro.app.ui.components.SectionTitle
import com.sensipro.app.ui.components.StatRow
import com.sensipro.app.ui.theme.NeonCyan
import com.sensipro.app.ui.theme.TextPrimary

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onSeeResult: () -> Unit
) {
    val deviceInfo by viewModel.deviceInfo.collectAsState()
    val selectedProfile by viewModel.selectedProfile.collectAsState()

    LaunchedEffect(Unit) {
        if (deviceInfo == null) viewModel.analyzeDevice()
    }

    val profiles = listOf(
        SensitivityProfile.CLOSE_RANGE to stringResource(R.string.profile_close_range),
        SensitivityProfile.LONG_RANGE to stringResource(R.string.profile_long_range),
        SensitivityProfile.BALANCED to stringResource(R.string.profile_balanced),
        SensitivityProfile.CUSTOM to stringResource(R.string.profile_custom)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.app_name),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
            Text(
                text = stringResource(R.string.tagline),
                color = NeonCyan,
                fontSize = 14.sp
            )
        }

        item {
            GlassCard {
                SectionTitle(stringResource(R.string.device_profile_title))
                deviceInfo?.let { info ->
                    val score = DeviceAnalyzer.performanceScore(info)
                    StatRow(stringResource(R.string.manufacturer), info.manufacturer)
                    StatRow(stringResource(R.string.model), info.model)
                    StatRow(stringResource(R.string.android_version), info.androidVersion)
                    StatRow(stringResource(R.string.sdk_version), info.sdkInt.toString())
                    StatRow(stringResource(R.string.total_ram), DeviceAnalyzer.formatBytes(info.totalRamBytes))
                    StatRow(
                        stringResource(R.string.screen_resolution),
                        "${info.screenWidthPx}x${info.screenHeightPx}"
                    )
                    StatRow(stringResource(R.string.refresh_rate), "${info.refreshRateHz.toInt()} Hz")
                    StatRow(stringResource(R.string.performance_score), "$score / 100")
                } ?: Text(stringResource(R.string.not_available), color = TextPrimary)
            }
        }

        item {
            GlassCard {
                SectionTitle(stringResource(R.string.select_play_style))
                Column {
                    profiles.chunked(2).forEach { rowItems ->
                        androidx.compose.foundation.layout.Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {
                            rowItems.forEach { (profile, label) ->
                                FilterChip(
                                    selected = selectedProfile == profile,
                                    onClick = { viewModel.selectProfile(profile) },
                                    label = { Text(label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeonCyan,
                                        selectedLabelColor = androidx.compose.ui.graphics.Color.Black
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            NeonButton(
                text = stringResource(R.string.generate_button),
                onClick = {
                    viewModel.generateRecommendation()
                    onSeeResult()
                }
            )
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}
