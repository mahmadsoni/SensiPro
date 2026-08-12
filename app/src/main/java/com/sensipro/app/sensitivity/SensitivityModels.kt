package com.sensipro.app.sensitivity

enum class SensitivityProfile {
    CLOSE_RANGE,
    LONG_RANGE,
    BALANCED,
    CUSTOM
}

enum class SmartTuneOption {
    MORE_STABLE,
    FASTER_DRAG,
    CLOSE_RANGE_CONTROL,
    LONG_RANGE_CONTROL,
    BALANCED
}

data class SensitivityValues(
    val general: Int,
    val redDot: Int,
    val scope2x: Int,
    val scope4x: Int,
    val sniper: Int,
    val freeLook: Int
)

data class DpiRecommendation(
    val recommendedDpi: Int,
    val currentDensityDpi: Int,
    val rangeLow: Int,
    val rangeHigh: Int,
    val reasonKey: String
)

data class SensitivityResult(
    val profile: SensitivityProfile,
    val values: SensitivityValues,
    val dpi: DpiRecommendation,
    val performanceScore: Int
)
