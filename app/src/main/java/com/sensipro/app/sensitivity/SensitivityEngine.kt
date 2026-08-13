package com.sensipro.app.sensitivity

import com.sensipro.app.device.DeviceAnalyzer
import com.sensipro.app.device.DeviceInfo
import kotlin.math.roundToInt

/**
 * Deterministic, explainable sensitivity recommendation engine.
 *
 * The engine never claims any output guarantees gameplay results
 * (e.g. headshots). It only maps observable device characteristics
 * plus the user's declared preferences to a sensitivity curve, using
 * fixed, reproducible arithmetic: the same device + the same
 * selections always produce the same numbers.
 *
 * All output values are clamped to the modern Free Fire sensitivity
 * range of 0-200.
 */
object SensitivityEngine {

    private const val MIN_SENS = 1
    private const val MAX_SENS = 200

    fun generate(
        deviceInfo: DeviceInfo,
        profile: SensitivityProfile,
        smartTune: SmartTuneOption = SmartTuneOption.BALANCED
    ): SensitivityResult {
        val performanceScore = DeviceAnalyzer.performanceScore(deviceInfo)

        // Base sensitivity derives from screen size, density and performance:
        // larger / denser screens with strong performance can comfortably
        // support slightly higher base sensitivity for faster tracking.
        // Calibrated so a typical mid-range 60Hz device lands general/red-dot
        // sensitivity in the range competitive Free Fire players commonly use
        // (roughly 80-110 on the modern 0-200 scale), rather than being
        // dragged down by a low refresh rate alone.
        val densityFactor = (deviceInfo.densityDpi.coerceIn(120, 560) / 420.0)
        val screenFactor = (deviceInfo.screenSizeInches.coerceIn(4.5, 8.0) / 6.5)
        // 60Hz devices (still the most common for Free Fire) start near parity
        // (0.90) instead of being penalized to ~0.67; higher refresh rates give
        // a modest bonus for smoother tracking, capping out at 144Hz.
        val refreshFactor = 0.90 + ((deviceInfo.refreshRateHz.coerceIn(60f, 144f) - 60f).toDouble() / 84.0) * 0.30
        val perfFactor = 0.90 + (performanceScore / 100.0) * 0.25 // 0.90 - 1.15

        val base = 100.0 * densityFactor * screenFactor * refreshFactor * perfFactor

        // Profile shaping: close range favors faster general/red-dot turning,
        // long range favors finer scope control (lower scope sensitivity).
        val profileMultipliers = when (profile) {
            SensitivityProfile.CLOSE_RANGE -> Multipliers(
                general = 1.35, redDot = 1.30, scope2x = 1.05,
                scope4x = 0.75, sniper = 0.55, freeLook = 1.25
            )
            SensitivityProfile.LONG_RANGE -> Multipliers(
                general = 0.90, redDot = 0.95, scope2x = 0.85,
                scope4x = 1.05, sniper = 1.15, freeLook = 0.85
            )
            SensitivityProfile.BALANCED -> Multipliers(
                general = 1.10, redDot = 1.05, scope2x = 0.90,
                scope4x = 0.85, sniper = 0.75, freeLook = 1.00
            )
            SensitivityProfile.CUSTOM -> Multipliers(
                general = 1.00, redDot = 1.00, scope2x = 0.90,
                scope4x = 0.80, sniper = 0.70, freeLook = 1.00
            )
        }

        val tuneMultipliers = when (smartTune) {
            SmartTuneOption.MORE_STABLE -> Multipliers(0.88, 0.90, 0.92, 0.92, 0.90, 0.90)
            SmartTuneOption.FASTER_DRAG -> Multipliers(1.18, 1.15, 1.05, 1.00, 0.95, 1.15)
            SmartTuneOption.CLOSE_RANGE_CONTROL -> Multipliers(1.12, 1.15, 1.00, 0.90, 0.80, 1.10)
            SmartTuneOption.LONG_RANGE_CONTROL -> Multipliers(0.92, 0.95, 0.90, 1.08, 1.12, 0.90)
            SmartTuneOption.BALANCED -> Multipliers(1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
        }

        val general = clamp(base * profileMultipliers.general * tuneMultipliers.general)
        val redDot = clamp(base * profileMultipliers.redDot * tuneMultipliers.redDot)
        val scope2x = clamp(base * profileMultipliers.scope2x * tuneMultipliers.scope2x)
        val scope4x = clamp(base * profileMultipliers.scope4x * tuneMultipliers.scope4x)
        val sniper = clamp(base * profileMultipliers.sniper * tuneMultipliers.sniper)
        val freeLook = clamp(base * profileMultipliers.freeLook * tuneMultipliers.freeLook)

        val values = SensitivityValues(
            general = general,
            redDot = redDot,
            scope2x = scope2x,
            scope4x = scope4x,
            sniper = sniper,
            freeLook = freeLook
        )

        val dpi = recommendDpi(deviceInfo, profile)

        return SensitivityResult(
            profile = profile,
            values = values,
            dpi = dpi,
            performanceScore = performanceScore
        )
    }

    /**
     * Software-only DPI suggestion. This never touches system settings;
     * it is purely informational text shown to the user.
     */
    fun recommendDpi(deviceInfo: DeviceInfo, profile: SensitivityProfile): DpiRecommendation {
        val currentDpi = deviceInfo.densityDpi

        if (currentDpi <= 0) {
            // Conservative fallback when density cannot be determined safely.
            return DpiRecommendation(
                recommendedDpi = 400,
                currentDensityDpi = 0,
                rangeLow = 380,
                rangeHigh = 420,
                reasonKey = "dpi_reason_fallback"
            )
        }

        val profileShift = when (profile) {
            SensitivityProfile.CLOSE_RANGE -> 1.04
            SensitivityProfile.LONG_RANGE -> 0.97
            SensitivityProfile.BALANCED -> 1.00
            SensitivityProfile.CUSTOM -> 1.00
        }

        val recommended = (currentDpi * profileShift).roundToInt().coerceIn(160, 640)
        val low = (recommended * 0.95).roundToInt()
        val high = (recommended * 1.05).roundToInt()

        return DpiRecommendation(
            recommendedDpi = recommended,
            currentDensityDpi = currentDpi,
            rangeLow = low,
            rangeHigh = high,
            reasonKey = "dpi_reason_standard"
        )
    }

    private fun clamp(value: Double): Int {
        return value.roundToInt().coerceIn(MIN_SENS, MAX_SENS)
    }

    private data class Multipliers(
        val general: Double,
        val redDot: Double,
        val scope2x: Double,
        val scope4x: Double,
        val sniper: Double,
        val freeLook: Double
    )
}
