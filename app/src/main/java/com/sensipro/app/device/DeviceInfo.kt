package com.sensipro.app.device

/**
 * Immutable snapshot of publicly-readable device characteristics.
 * All values come from public Android APIs only (Build, DisplayMetrics,
 * ActivityManager, StatFs, Display). No root, no private APIs.
 */
data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val androidVersion: String,
    val sdkInt: Int,
    val cpuAbi: String,
    val cpuCoreCount: Int,
    val totalRamBytes: Long,
    val availableRamBytes: Long,
    val totalStorageBytes: Long,
    val availableStorageBytes: Long,
    val screenWidthPx: Int,
    val screenHeightPx: Int,
    val densityDpi: Int,
    val density: Float,
    val refreshRateHz: Float,
    val supportedRefreshRates: List<Float>,
    val screenSizeInches: Double
) {
    companion object {
        const val NOT_AVAILABLE = "N/A"
    }
}
