package com.example

import com.example.device.PerformanceClassifier
import com.example.engine.SensitivityEngine
import com.example.model.DeviceSpecs
import com.example.model.GameType
import com.example.model.PerformanceReport
import com.example.model.PerformanceTier
import com.example.model.QuestionnaireAnswers
import com.example.model.SensitivitySettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SensitivityEngineUnitTest {

    private val engine = SensitivityEngine()
    private val classifier = PerformanceClassifier()

    private fun createSampleSpecs(
        ramGb: Float = 4f,
        cores: Int = 8,
        refreshHz: Float = 60f,
        hasGyro: Boolean = true
    ): DeviceSpecs {
        val ramBytes = (ramGb * 1024L * 1024L * 1024L).toLong()
        return DeviceSpecs(
            manufacturer = "Realme",
            brand = "Realme",
            model = "RMX3624",
            deviceName = "Realme C33",
            androidVersion = "12 (Snow Cone)",
            sdkInt = 31,
            socModel = "Unisoc T612",
            socManufacturer = "Unisoc",
            cpuCores = cores,
            cpuArchitecture = "arm64-v8a",
            totalRamBytes = ramBytes,
            availableRamBytes = ramBytes / 2,
            totalStorageBytes = 64L * 1024L * 1024L * 1024L,
            availableStorageBytes = 32L * 1024L * 1024L * 1024L,
            screenResolution = "720 × 1600 (HD+)",
            screenDensityDpi = 320,
            refreshRateHz = refreshHz,
            hasGyroscope = hasGyro,
            sensorList = listOf("Accelerometer", "Proximity", "Light")
        )
    }

    @Test
    fun testClassifierTiers() {
        // High-End check
        val highSpecs = createSampleSpecs(ramGb = 12f, cores = 8, refreshHz = 120f)
        val highReport = classifier.classify(highSpecs)
        assertEquals(PerformanceTier.HIGH_END, highReport.tier)
        assertTrue(highReport.gaugeScore >= 0.75f)

        // Mid-Range check
        val midSpecs = createSampleSpecs(ramGb = 4f, cores = 8, refreshHz = 60f)
        val midReport = classifier.classify(midSpecs)
        assertEquals(PerformanceTier.MID_RANGE, midReport.tier)

        // Low-End check
        val lowSpecs = createSampleSpecs(ramGb = 2f, cores = 4, refreshHz = 60f)
        val lowReport = classifier.classify(lowSpecs)
        assertEquals(PerformanceTier.LOW_END, lowReport.tier)
    }

    @Test
    fun testSensitivityBoundaryClamping() {
        val specs = createSampleSpecs()
        val report = classifier.classify(specs)

        // Force extreme values
        val answers = QuestionnaireAnswers(
            selectedGame = GameType.FREE_FIRE_MAX,
            playStyle = "Aggressive Close-Range",
            mainProblem = "Crosshair does not reach the head",
            aimFeeling = "My aim moves too slowly",
            fingerCount = "2 Finger"
        )

        val result = engine.generateRecommendation(specs, report, answers)
        val s = result.settings

        assertTrue(s.general in 0..200)
        assertTrue(s.redDot in 0..200)
        assertTrue(s.scope2x in 0..200)
        assertTrue(s.scope4x in 0..200)
        assertTrue(s.sniper in 0..200)
        assertTrue(s.freeLook in 0..200)
    }

    @Test
    fun testPlayStyleAdjustments() {
        val specs = createSampleSpecs()
        val report = classifier.classify(specs)

        val balanced = engine.generateRecommendation(
            specs, report,
            QuestionnaireAnswers(playStyle = "Balanced", mainProblem = "No specific problem")
        )

        val headshot = engine.generateRecommendation(
            specs, report,
            QuestionnaireAnswers(playStyle = "Headshot", mainProblem = "No specific problem")
        )

        // Headshot should boost general and red dot compared to balanced
        assertTrue(headshot.settings.general > balanced.settings.general)
        assertTrue(headshot.settings.redDot > balanced.settings.redDot)
    }

    @Test
    fun testFeedbackTuningIncrements() {
        val initial = SensitivitySettings(general = 150, redDot = 140)

        val decreased = engine.applyFeedbackAdjustment(initial, "too_fast")
        assertTrue(decreased.general < initial.general)
        assertTrue(decreased.redDot < initial.redDot)

        val increased = engine.applyFeedbackAdjustment(initial, "too_slow")
        assertTrue(increased.general > initial.general)
        assertTrue(increased.redDot > initial.redDot)
    }

    @Test
    fun testTargetedProblemAdjustment() {
        val initial = SensitivitySettings(general = 150, redDot = 140)
        val adjusted = engine.applyFeedbackAdjustment(initial, "just_right", "above_head")

        // Above head should reduce sensitivity slightly
        assertTrue(adjusted.general < initial.general)
    }
}
