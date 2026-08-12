package com.sensipro.app.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sensipro.app.data.AppContainer
import com.sensipro.app.device.DeviceAnalyzer
import com.sensipro.app.device.DeviceInfo
import com.sensipro.app.history.HistoryEntry
import com.sensipro.app.sensitivity.SensitivityEngine
import com.sensipro.app.sensitivity.SensitivityProfile
import com.sensipro.app.sensitivity.SensitivityResult
import com.sensipro.app.sensitivity.SmartTuneOption
import com.sensipro.app.settings.AppLanguage
import com.sensipro.app.settings.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val appContext: Context,
    private val container: AppContainer
) : ViewModel() {

    private val _deviceInfo = MutableStateFlow<DeviceInfo?>(null)
    val deviceInfo: StateFlow<DeviceInfo?> = _deviceInfo

    private val _currentResult = MutableStateFlow<SensitivityResult?>(null)
    val currentResult: StateFlow<SensitivityResult?> = _currentResult

    private val _selectedProfile = MutableStateFlow(SensitivityProfile.BALANCED)
    val selectedProfile: StateFlow<SensitivityProfile> = _selectedProfile

    private val _selectedSmartTune = MutableStateFlow(SmartTuneOption.BALANCED)
    val selectedSmartTune: StateFlow<SmartTuneOption> = _selectedSmartTune

    val settings: StateFlow<AppSettings> = container.settingsRepository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    val history: StateFlow<List<HistoryEntry>> = container.historyRepository.historyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun analyzeDevice() {
        val info = DeviceAnalyzer.analyze(appContext)
        _deviceInfo.value = info
    }

    fun selectProfile(profile: SensitivityProfile) {
        _selectedProfile.value = profile
    }

    fun selectSmartTune(option: SmartTuneOption) {
        _selectedSmartTune.value = option
    }

    fun generateRecommendation() {
        val info = _deviceInfo.value ?: DeviceAnalyzer.analyze(appContext).also { _deviceInfo.value = it }
        val result = SensitivityEngine.generate(info, _selectedProfile.value, _selectedSmartTune.value)
        _currentResult.value = result
    }

    fun recalculate(smartTune: SmartTuneOption) {
        _selectedSmartTune.value = smartTune
        generateRecommendation()
    }

    fun saveCurrentToHistory() {
        val result = _currentResult.value ?: return
        val info = _deviceInfo.value ?: return
        viewModelScope.launch {
            val entry = HistoryEntry(
                id = container.historyRepository.newId(),
                timestampMillis = System.currentTimeMillis(),
                deviceModel = info.model,
                profile = result.profile.name,
                general = result.values.general,
                redDot = result.values.redDot,
                scope2x = result.values.scope2x,
                scope4x = result.values.scope4x,
                sniper = result.values.sniper,
                freeLook = result.values.freeLook
            )
            container.historyRepository.addEntry(entry)
        }
    }

    fun deleteHistoryEntry(id: String) {
        viewModelScope.launch { container.historyRepository.deleteEntry(id) }
    }

    fun clearHistory() {
        viewModelScope.launch { container.historyRepository.clearAll() }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { container.settingsRepository.setLanguage(language) }
    }

    fun setDefaultProfile(profile: SensitivityProfile) {
        viewModelScope.launch { container.settingsRepository.setDefaultProfile(profile.name) }
    }

    fun setHapticEnabled(enabled: Boolean) {
        viewModelScope.launch { container.settingsRepository.setHapticEnabled(enabled) }
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        viewModelScope.launch { container.settingsRepository.setAnimationsEnabled(enabled) }
    }

    class Factory(
        private val appContext: Context,
        private val container: AppContainer
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(appContext, container) as T
        }
    }
}
