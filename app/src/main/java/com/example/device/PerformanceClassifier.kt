package com.example.device

import com.example.model.ClassificationFactor
import com.example.model.DeviceSpecs
import com.example.model.PerformanceReport
import com.example.model.PerformanceTier

class PerformanceClassifier {

    fun classify(specs: DeviceSpecs): PerformanceReport {
        val factors = mutableListOf<ClassificationFactor>()
        var scorePoints = 0
        var totalPoints = 0

        // 1. RAM capacity evaluation (Max 35 points)
        totalPoints += 35
        val ramGb = specs.totalRamGb
        when {
            ramGb >= 7.5f -> {
                scorePoints += 35
                factors.add(ClassificationFactor("RAM Capacity", "${String.format("%.1f", ramGb)} GB (High)", true))
            }
            ramGb >= 3.8f -> {
                scorePoints += 24
                factors.add(ClassificationFactor("RAM Capacity", "${String.format("%.1f", ramGb)} GB (Moderate)", true))
            }
            ramGb >= 2.5f -> {
                scorePoints += 12
                factors.add(ClassificationFactor("RAM Capacity", "${String.format("%.1f", ramGb)} GB (Limited)", false))
            }
            else -> {
                scorePoints += 6
                factors.add(ClassificationFactor("RAM Capacity", "${String.format("%.1f", ramGb)} GB (Low)", false))
            }
        }

        // 2. CPU Cores & Architecture (Max 30 points)
        totalPoints += 30
        val cores = specs.cpuCores
        when {
            cores >= 8 -> {
                scorePoints += 30
                factors.add(ClassificationFactor("CPU Cores", "$cores Cores (Good)", true))
            }
            cores >= 6 -> {
                scorePoints += 20
                factors.add(ClassificationFactor("CPU Cores", "$cores Cores (Adequate)", true))
            }
            cores >= 4 -> {
                scorePoints += 12
                factors.add(ClassificationFactor("CPU Cores", "$cores Cores (Entry Level)", false))
            }
            else -> {
                scorePoints += 6
                factors.add(ClassificationFactor("CPU Cores", "$cores Cores (Limited)", false))
            }
        }

        // 3. Display Refresh Rate (Max 20 points)
        totalPoints += 20
        val hz = specs.refreshRateHz.toInt()
        when {
            hz >= 120 -> {
                scorePoints += 20
                factors.add(ClassificationFactor("Refresh Rate", "${hz}Hz Ultra Smooth", true))
            }
            hz >= 90 -> {
                scorePoints += 16
                factors.add(ClassificationFactor("Refresh Rate", "${hz}Hz Smooth Display", true))
            }
            else -> {
                scorePoints += 12
                factors.add(ClassificationFactor("Refresh Rate", "${hz}Hz Standard", true))
            }
        }

        // 4. Architecture & 64-bit support (Max 15 points)
        totalPoints += 15
        if (specs.cpuArchitecture.contains("64", ignoreCase = true)) {
            scorePoints += 15
            factors.add(ClassificationFactor("Architecture", "${specs.cpuArchitecture} (64-bit Optimal)", true))
        } else {
            scorePoints += 7
            factors.add(ClassificationFactor("Architecture", "${specs.cpuArchitecture} (32-bit Legacy)", false))
        }

        // Calculate tier and gauge score
        val percentage = (scorePoints.toFloat() / totalPoints.toFloat()).coerceIn(0.1f, 1.0f)
        val (tier, explanation) = when {
            percentage >= 0.82f -> {
                PerformanceTier.HIGH_END to "Capable of handling high graphic presets and ultra-responsive touch sampling without camera stutter."
            }
            percentage >= 0.48f -> {
                PerformanceTier.MID_RANGE to "Balanced performance profile optimized for stable frame delivery and consistent drag headshots."
            }
            else -> {
                PerformanceTier.LOW_END to "Conservative starting profile tuned for controllable swipe acceleration and stable thermal management."
            }
        }

        val confidence = when {
            specs.totalRamBytes > 0 && specs.cpuCores > 0 && specs.refreshRateHz > 0 -> 88
            specs.totalRamBytes > 0 -> 72
            else -> 50
        }

        return PerformanceReport(
            tier = tier,
            confidencePercent = confidence,
            gaugeScore = percentage,
            factors = factors,
            explanation = explanation
        )
    }
}
