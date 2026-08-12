package com.sensipro.app.data

import android.content.Context
import com.sensipro.app.history.HistoryRepository
import com.sensipro.app.settings.SettingsRepository

/**
 * Lightweight manual dependency container. Avoids pulling in a DI
 * framework for a small, single-module app.
 */
class AppContainer(context: Context) {
    val settingsRepository: SettingsRepository = SettingsRepository(context.applicationContext)
    val historyRepository: HistoryRepository = HistoryRepository(context.applicationContext)
}
