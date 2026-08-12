package com.sensipro.app.device

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.StatFs
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Analyzes the device using only public, non-sensitive Android APIs.
 * Never touches root, accessibility services, or third-party app data.
 */
object DeviceAnalyzer {

    fun analyze(context: Context): DeviceInfo {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)

        val totalRam = memoryInfo.totalMem
        val availableRam = memoryInfo.availMem

        val internalStat = StatFs(context.filesDir.path)
        val totalStorage = internalStat.blockSizeLong * internalStat.blockCountLong
        val availableStorage = internalStat.blockSizeLong * internalStat.availableBlocksLong

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val metrics = DisplayMetrics()
        val display = windowManager?.defaultDisplay
        @Suppress("DEPRECATION")
        display?.getRealMetrics(metrics)

        val widthPx = metrics.widthPixels
        val heightPx = metrics.heightPixels
        val densityDpi = metrics.densityDpi
        val density = metrics.density

        val screenSizeInches = if (widthPx > 0 && heightPx > 0 && densityDpi > 0) {
            val widthInches = widthPx.toDouble() / densityDpi.toDouble()
            val heightInches = heightPx.toDouble() / densityDpi.toDouble()
            sqrt(widthInches.pow(2) + heightInches.pow(2))
        } else {
            0.0
        }

        @Suppress("DEPRECATION")
        val refreshRate = display?.refreshRate ?: 60f

        val supportedRates: List<Float> = try {
            @Suppress("DEPRECATION")
            display?.supportedRefreshRates?.toList()?.sorted() ?: listOf(refreshRate)
        } catch (e: Exception) {
            listOf(refreshRate)
        }

        val cpuAbi = Build.SUPPORTED_ABIS.firstOrNull() ?: DeviceInfo.NOT_AVAILABLE
        val cpuCores = Runtime.getRuntime().availableProcessors()

        return DeviceInfo(
            manufacturer = Build.MANUFACTURER ?: DeviceInfo.NOT_AVAILABLE,
            model = Build.MODEL ?: DeviceInfo.NOT_AVAILABLE,
            androidVersion = Build.VERSION.RELEASE ?: DeviceInfo.NOT_AVAILABLE,
            sdkInt = Build.VERSION.SDK_INT,
            cpuAbi = cpuAbi,
            cpuCoreCount = cpuCores,
            totalRamBytes = totalRam,
            availableRamBytes = availableRam,
            totalStorageBytes = totalStorage,
            availableStorageBytes = availableStorage,
            screenWidthPx = widthPx,
            screenHeightPx = heightPx,
            densityDpi = densityDpi,
            density = density,
            refreshRateHz = refreshRate,
            supportedRefreshRates = supportedRates,
            screenSizeInches = screenSizeInches
        )
    }

    /**
     * Deterministic 0-100 performance estimate derived only from
     * publicly observable specs (RAM, cores, SDK level, resolution).
     * Same device always yields the same score.
     */
    fun performanceScore(info: DeviceInfo): Int {
        val ramGb = info.totalRamBytes / (1024.0 * 1024.0 * 1024.0)
        val ramScore = (ramGb / 12.0 * 40.0).coerceIn(0.0, 40.0)

        val coreScore = (info.cpuCoreCount / 8.0 * 20.0).coerceIn(0.0, 20.0)

        val sdkScore = ((info.sdkInt - 24) / (34.0 - 24.0) * 20.0).coerceIn(0.0, 20.0)

        val refreshScore = (info.refreshRateHz / 120.0 * 20.0).coerceIn(0.0, 20.0)

        val total = ramScore + coreScore + sdkScore + refreshScore
        return total.coerceIn(0.0, 100.0).toInt()
    }

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return DeviceInfo.NOT_AVAILABLE
        val gb = bytes / (1024.0 * 1024.0 * 1024.0)
        return if (gb >= 1.0) {
            String.format("%.1f GB", gb)
        } else {
            val mb = bytes / (1024.0 * 1024.0)
            String.format("%.0f MB", mb)
        }
    }
}
