package com.sensipro.app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sensipro.app.R
import com.sensipro.app.settings.AppLanguage
import com.sensipro.app.ui.MainViewModel
import com.sensipro.app.ui.components.GlassCard
import com.sensipro.app.ui.components.SectionTitle
import com.sensipro.app.ui.theme.NeonCyan
import com.sensipro.app.ui.theme.TextPrimary
import com.sensipro.app.ui.theme.TextSecondary

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(stringResource(R.string.settings_title), color = TextPrimary, fontWeight = FontWeight.Bold)
        }

        item {
            GlassCard {
                SectionTitle(stringResource(R.string.language))
                val languages = listOf(
                    AppLanguage.TAJIK to stringResource(R.string.lang_tajik),
                    AppLanguage.RUSSIAN to stringResource(R.string.lang_russian),
                    AppLanguage.ENGLISH to stringResource(R.string.lang_english)
                )
                languages.forEach { (lang, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, color = TextPrimary)
                        RadioButton(
                            selected = settings.language == lang,
                            onClick = { viewModel.setLanguage(lang) },
                            colors = androidx.compose.material3.RadioButtonDefaults.colors(selectedColor = NeonCyan)
                        )
                    }
                }
            }
        }

        item {
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.haptic_feedback), color = TextPrimary)
                    Switch(
                        checked = settings.hapticEnabled,
                        onCheckedChange = { viewModel.setHapticEnabled(it) },
                        colors = SwitchDefaults.colors(checkedTrackColor = NeonCyan)
                    )
                }
            }
        }

        item {
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.animations), color = TextPrimary)
                    Switch(
                        checked = settings.animationsEnabled,
                        onCheckedChange = { viewModel.setAnimationsEnabled(it) },
                        colors = SwitchDefaults.colors(checkedTrackColor = NeonCyan)
                    )
                }
            }
        }

        item {
            GlassCard {
                SectionTitle(stringResource(R.string.privacy_info))
                Text(stringResource(R.string.onboarding_privacy_body), color = TextSecondary)
            }
        }

        item {
            GlassCard {
                SectionTitle(stringResource(R.string.about_app))
                Text(stringResource(R.string.disclaimer), color = TextSecondary)
            }
        }
    }
}
