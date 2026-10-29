package com.example.model

enum class GameType(val displayName: String, val subtitle: String, val isMax: Boolean) {
    FREE_FIRE_MAX(
        displayName = "Free Fire MAX",
        subtitle = "Recommended for better graphics and performance.",
        isMax = true
    ),
    FREE_FIRE_INDIA(
        displayName = "Free Fire India",
        subtitle = "Standard version with optimized settings.",
        isMax = false
    )
}

enum class PerformanceTier(val label: String, val subtitle: String, val colorHex: Long) {
    LOW_END("Low-End", "Optimized Stability", 0xFFEF4444),
    MID_RANGE("Mid-Range", "Balanced Performance", 0xFFF59E0B),
    HIGH_END("High-End", "Maximum Responsiveness", 0xFF10B981),
    UNKNOWN("Unknown", "Pending Hardware Assessment", 0xFF64748B)
}

data class DeviceSpecs(
    val manufacturer: String,
    val brand: String,
    val model: String,
    val deviceName: String,
    val androidVersion: String,
    val sdkInt: Int,
    val socModel: String,
    val socManufacturer: String,
    val cpuCores: Int,
    val cpuArchitecture: String,
    val totalRamBytes: Long,
    val availableRamBytes: Long,
    val totalStorageBytes: Long,
    val availableStorageBytes: Long,
    val screenResolution: String,
    val screenDensityDpi: Int,
    val refreshRateHz: Float,
    val hasGyroscope: Boolean,
    val sensorList: List<String>
) {
    val totalRamGb: Float get() = (totalRamBytes / (1024f * 1024f * 1024f))
    val availableRamGb: Float get() = (availableRamBytes / (1024f * 1024f * 1024f))
    val totalStorageGb: Float get() = (totalStorageBytes / (1024f * 1024f * 1024f))
    val availableStorageGb: Float get() = (availableStorageBytes / (1024f * 1024f * 1024f))
}

data class ClassificationFactor(
    val title: String,
    val detail: String,
    val isPositive: Boolean
)

data class PerformanceReport(
    val tier: PerformanceTier,
    val confidencePercent: Int,
    val gaugeScore: Float, // 0f to 1f
    val factors: List<ClassificationFactor>,
    val explanation: String
)

data class SensitivitySettings(
    val general: Int = 160,
    val redDot: Int = 155,
    val scope2x: Int = 140,
    val scope4x: Int = 120,
    val sniper: Int = 100,
    val freeLook: Int = 80,
    val dpiGuidance: String = "Keep Existing (Recommended) - No DPI change needed for your device."
) {
    fun clampAll(minVal: Int = 0, maxVal: Int = 200): SensitivitySettings {
        return copy(
            general = general.coerceIn(minVal, maxVal),
            redDot = redDot.coerceIn(minVal, maxVal),
            scope2x = scope2x.coerceIn(minVal, maxVal),
            scope4x = scope4x.coerceIn(minVal, maxVal),
            sniper = sniper.coerceIn(minVal, maxVal),
            freeLook = freeLook.coerceIn(minVal, maxVal)
        )
    }
}

data class QuestionnaireAnswers(
    val selectedGame: GameType = GameType.FREE_FIRE_MAX,
    val playStyle: String = "Balanced", // Headshot, Balanced, Long Range, Aggressive
    val fingerCount: String = "2 Finger", // 2 Finger, 3 Finger, 4 Finger, 5+ Finger
    val gyroscopePreference: String = "Off", // On, Off, Not Sure
    val aimFeeling: String = "Comfortable", // Too slowly, Too fast, Inconsistent, Comfortable
    val mainProblem: String = "Crosshair does not reach head", // Crosshair does not reach head, Crosshair goes above head, Aim shakes, Scope control, Unstable, Fresh
    val currentSetup: String = "Default Settings", // Default, Custom, Don't remember
    val controlPreference: String = "Balanced drag and control", // Fast drag, Balanced drag and control, Precise movement
    val customHud: String = "Default HUD", // Default HUD, Custom HUD, Not sure
    val changedDpi: String = "No" // Yes, No, Not sure
)
