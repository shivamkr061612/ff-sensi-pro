package com.example.engine

import com.example.model.DeviceSpecs
import com.example.model.GameType
import com.example.model.PerformanceReport
import com.example.model.PerformanceTier
import com.example.model.QuestionnaireAnswers
import com.example.model.SensitivitySettings

data class RecommendationResult(
    val settings: SensitivitySettings,
    val summaryRationale: String,
    val keyFactors: List<String>
)

class SensitivityEngine {

    fun generateRecommendation(
        specs: DeviceSpecs,
        report: PerformanceReport,
        answers: QuestionnaireAnswers
    ): RecommendationResult {
        // 1. Select base profile according to device performance tier
        var gen = when (report.tier) {
            PerformanceTier.HIGH_END -> 172
            PerformanceTier.MID_RANGE -> 160
            PerformanceTier.LOW_END -> 145
            PerformanceTier.UNKNOWN -> 155
        }
        var red = when (report.tier) {
            PerformanceTier.HIGH_END -> 168
            PerformanceTier.MID_RANGE -> 155
            PerformanceTier.LOW_END -> 140
            PerformanceTier.UNKNOWN -> 150
        }
        var s2x = when (report.tier) {
            PerformanceTier.HIGH_END -> 152
            PerformanceTier.MID_RANGE -> 140
            PerformanceTier.LOW_END -> 125
            PerformanceTier.UNKNOWN -> 135
        }
        var s4x = when (report.tier) {
            PerformanceTier.HIGH_END -> 132
            PerformanceTier.MID_RANGE -> 120
            PerformanceTier.LOW_END -> 110
            PerformanceTier.UNKNOWN -> 115
        }
        var snip = when (report.tier) {
            PerformanceTier.HIGH_END -> 105
            PerformanceTier.MID_RANGE -> 100
            PerformanceTier.LOW_END -> 90
            PerformanceTier.UNKNOWN -> 95
        }
        var free = when (report.tier) {
            PerformanceTier.HIGH_END -> 88
            PerformanceTier.MID_RANGE -> 80
            PerformanceTier.LOW_END -> 75
            PerformanceTier.UNKNOWN -> 78
        }

        val factors = mutableListOf<String>()

        // 2. Adjust for Play Style / Main Aiming Goal
        when {
            answers.playStyle.contains("Headshot", ignoreCase = true) -> {
                gen += 12
                red += 10
                s2x += 5
                factors.add("Elevated General & Red Dot for responsive vertical drag headshots")
            }
            answers.playStyle.contains("Long Range", ignoreCase = true) -> {
                gen -= 10
                red -= 6
                s2x += 6
                s4x += 12
                snip += 10
                factors.add("Stabilized hip-fire with boosted optics for distant engagement")
            }
            answers.playStyle.contains("Aggressive", ignoreCase = true) -> {
                gen += 15
                red += 12
                free += 10
                factors.add("Fast-reaction camera rotation for rapid close-quarter 360° fights")
            }
            else -> {
                factors.add("Neutral balanced distribution across all sight distances")
            }
        }

        // 3. Adjust for Aim Problem
        when {
            answers.mainProblem.contains("does not reach", ignoreCase = true) -> {
                gen += 10
                red += 8
                factors.add("Boosted swipe sensitivity to help crosshair reach head height")
            }
            answers.mainProblem.contains("goes above", ignoreCase = true) -> {
                gen -= 12
                red -= 10
                factors.add("Reduced vertical drag speed to prevent overshooting targets")
            }
            answers.mainProblem.contains("shakes", ignoreCase = true) -> {
                gen -= 8
                red -= 8
                s2x -= 8
                s4x -= 8
                snip -= 6
                factors.add("Dampened sensitivity across the board for steady cursor control")
            }
            answers.mainProblem.contains("Scope", ignoreCase = true) -> {
                s2x -= 14
                s4x -= 16
                snip -= 12
                factors.add("Tightened magnified scope sensitivity to eliminate erratic ADS aim")
            }
            answers.mainProblem.contains("Long-range", ignoreCase = true) -> {
                s4x -= 12
                snip -= 14
                factors.add("Lowered sniper zoom tracking speed for consistent head tracking")
            }
        }

        // 4. Adjust for Aim description
        when {
            answers.aimFeeling.contains("slowly", ignoreCase = true) -> {
                gen += 8
                red += 6
            }
            answers.aimFeeling.contains("fast", ignoreCase = true) -> {
                gen -= 10
                red -= 8
            }
        }

        // 5. Adjust for Control Style (Finger Count)
        if (answers.fingerCount.contains("2", ignoreCase = true)) {
            gen += 4
            red += 3
            factors.add("Thumb-drag optimization for 2-finger layout screen coverage")
        }

        // 6. Gyroscope adaptation
        if (answers.gyroscopePreference.contains("On", ignoreCase = true)) {
            free -= 6
            gen -= 4
            factors.add("Calibrated for active Gyroscope sensor wrist micro-tracking")
        }

        // 7. Game profile nuances
        if (answers.selectedGame == GameType.FREE_FIRE_MAX) {
            gen += 2
            red += 2
            factors.add("Free Fire MAX dynamic rendering compensation")
        }

        // 8. Clamp strictly to supported 0..200 range
        val finalSettings = SensitivitySettings(
            general = gen.coerceIn(0, 200),
            redDot = red.coerceIn(0, 200),
            scope2x = s2x.coerceIn(0, 200),
            scope4x = s4x.coerceIn(0, 200),
            sniper = snip.coerceIn(0, 200),
            freeLook = free.coerceIn(0, 200),
            dpiGuidance = "Keep Existing (Recommended) - No DPI change needed for your device."
        )

        val summary = "Tuned for ${answers.playStyle} on your ${report.tier.label} (${specs.refreshRateHz.toInt()}Hz) with ${answers.fingerCount} setup."

        return RecommendationResult(
            settings = finalSettings,
            summaryRationale = summary,
            keyFactors = factors
        )
    }

    fun applyFeedbackAdjustment(
        current: SensitivitySettings,
        feedbackType: String, // "too_fast", "just_right", "too_slow"
        specificProblem: String? = null
    ): SensitivitySettings {
        return when (feedbackType) {
            "too_fast" -> {
                current.copy(
                    general = (current.general - 8).coerceIn(0, 200),
                    redDot = (current.redDot - 8).coerceIn(0, 200),
                    scope2x = (current.scope2x - 6).coerceIn(0, 200),
                    scope4x = (current.scope4x - 6).coerceIn(0, 200),
                    sniper = (current.sniper - 5).coerceIn(0, 200)
                )
            }
            "too_slow" -> {
                current.copy(
                    general = (current.general + 8).coerceIn(0, 200),
                    redDot = (current.redDot + 8).coerceIn(0, 200),
                    scope2x = (current.scope2x + 6).coerceIn(0, 200),
                    scope4x = (current.scope4x + 6).coerceIn(0, 200),
                    sniper = (current.sniper + 5).coerceIn(0, 200)
                )
            }
            else -> {
                // If specific problem is targeted
                when (specificProblem) {
                    "above_head" -> current.copy(
                        general = (current.general - 10).coerceIn(0, 200),
                        redDot = (current.redDot - 8).coerceIn(0, 200)
                    )
                    "below_head" -> current.copy(
                        general = (current.general + 10).coerceIn(0, 200),
                        redDot = (current.redDot + 8).coerceIn(0, 200)
                    )
                    "unstable_scope" -> current.copy(
                        scope2x = (current.scope2x - 12).coerceIn(0, 200),
                        scope4x = (current.scope4x - 14).coerceIn(0, 200),
                        sniper = (current.sniper - 10).coerceIn(0, 200)
                    )
                    else -> current
                }
            }
        }
    }
}
