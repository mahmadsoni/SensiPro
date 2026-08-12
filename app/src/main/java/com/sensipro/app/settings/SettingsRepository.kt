package com.sensipro.app.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "sensipro_settings")

enum class AppLanguage(val tag: String) {
    TAJIK("tg"),
    RUSSIAN("ru"),
    ENGLISH("en")
}

data class AppSettings(
    val language: AppLanguage = AppLanguage.TAJIK,
    val defaultProfile: String = "BALANCED",
    val hapticEnabled: Boolean = true,
    val animationsEnabled: Boolean = true
)

class SettingsRepository(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val DEFAULT_PROFILE = stringPreferencesKey("default_profile")
        val HAPTIC = booleanPreferencesKey("haptic_enabled")
        val ANIMATIONS = booleanPreferencesKey("animations_enabled")
    }

    val settingsFlow: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            language = AppLanguage.entries.firstOrNull { it.tag == prefs[Keys.LANGUAGE] }
                ?: AppLanguage.TAJIK,
            defaultProfile = prefs[Keys.DEFAULT_PROFILE] ?: "BALANCED",
            hapticEnabled = prefs[Keys.HAPTIC] ?: true,
            animationsEnabled = prefs[Keys.ANIMATIONS] ?: true
        )
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.settingsDataStore.edit { it[Keys.LANGUAGE] = language.tag }
    }

    suspend fun setDefaultProfile(profile: String) {
        context.settingsDataStore.edit { it[Keys.DEFAULT_PROFILE] = profile }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.HAPTIC] = enabled }
    }

    suspend fun setAnimationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.ANIMATIONS] = enabled }
    }
}
