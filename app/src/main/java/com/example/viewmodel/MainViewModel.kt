package com.example.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ProfileRepository
import com.example.data.SensitivityProfileEntity
import com.example.data.UserPreferencesRepository
import com.example.device.DeviceDetector
import com.example.device.PerformanceClassifier
import com.example.engine.RecommendationResult
import com.example.engine.SensitivityEngine
import com.example.model.DeviceSpecs
import com.example.model.GameType
import com.example.model.PerformanceReport
import com.example.model.PerformanceTier
import com.example.model.QuestionnaireAnswers
import com.example.model.SensitivitySettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val detector = DeviceDetector(application)
    private val classifier = PerformanceClassifier()
    private val engine = SensitivityEngine()

    private val db = AppDatabase.getInstance(application)
    private val profileRepo = ProfileRepository(db.profileDao())
    private val prefsRepo = UserPreferencesRepository(application)

    // Device & Performance
    private val _deviceSpecs = MutableStateFlow<DeviceSpecs?>(null)
    val deviceSpecs: StateFlow<DeviceSpecs?> = _deviceSpecs.asStateFlow()

    private val _performanceReport = MutableStateFlow<PerformanceReport?>(null)
    val performanceReport: StateFlow<PerformanceReport?> = _performanceReport.asStateFlow()

    // Questionnaire
    private val _questionnaireStep = MutableStateFlow(1)
    val questionnaireStep: StateFlow<Int> = _questionnaireStep.asStateFlow()

    private val _questionnaireAnswers = MutableStateFlow(QuestionnaireAnswers())
    val questionnaireAnswers: StateFlow<QuestionnaireAnswers> = _questionnaireAnswers.asStateFlow()

    // Sensitivity Recommendation
    private val _recommendation = MutableStateFlow<RecommendationResult?>(null)
    val recommendation: StateFlow<RecommendationResult?> = _recommendation.asStateFlow()

    // Editing Sensitivity
    private val _editingSettings = MutableStateFlow(SensitivitySettings())
    val editingSettings: StateFlow<SensitivitySettings> = _editingSettings.asStateFlow()

    // Saved Profiles from Room
    val savedProfiles: StateFlow<List<SensitivityProfileEntity>> = profileRepo.getAllProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Preferences
    val isDarkMode: StateFlow<Boolean> = prefsRepo.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val selectedGamePref: StateFlow<String> = prefsRepo.selectedGame
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Free Fire MAX")

    val hasCompletedOnboarding: StateFlow<Boolean> = prefsRepo.hasCompletedOnboarding
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isPrivacyConsentAccepted: StateFlow<Boolean> = prefsRepo.isPrivacyConsentAccepted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        detectDevice()
        ensureDefaultProfiles()
    }

    fun detectDevice() {
        val specs = detector.detectDevice()
        val report = classifier.classify(specs)
        _deviceSpecs.value = specs
        _performanceReport.value = report

        // Default initial recommendation
        val rec = engine.generateRecommendation(specs, report, _questionnaireAnswers.value)
        _recommendation.value = rec
        _editingSettings.value = rec.settings
    }

    private var hasSeeded = false

    private fun ensureDefaultProfiles() {
        viewModelScope.launch {
            if (hasSeeded) return@launch
            hasSeeded = true
            val list = profileRepo.getAllProfiles().first()
            if (list.isEmpty()) {
                profileRepo.saveProfile(
                    SensitivityProfileEntity(
                        name = "Default Profile",
                        game = "Free Fire MAX",
                        general = 160,
                        redDot = 155,
                        scope2x = 140,
                        scope4x = 120,
                        sniper = 100,
                        freeLook = 80,
                        playStyle = "Balanced",
                        fingerCount = "2 Finger",
                        deviceCategory = "Mid-Range",
                        dpiGuidance = "Keep Existing"
                    )
                )
                profileRepo.saveProfile(
                    SensitivityProfileEntity(
                        name = "Headshot Profile",
                        game = "Free Fire MAX",
                        general = 175,
                        redDot = 168,
                        scope2x = 145,
                        scope4x = 125,
                        sniper = 100,
                        freeLook = 85,
                        playStyle = "Headshot",
                        fingerCount = "3 Finger",
                        deviceCategory = "Mid-Range",
                        dpiGuidance = "Keep Existing"
                    )
                )
                profileRepo.saveProfile(
                    SensitivityProfileEntity(
                        name = "Long Range Profile",
                        game = "Free Fire India",
                        general = 145,
                        redDot = 140,
                        scope2x = 145,
                        scope4x = 132,
                        sniper = 110,
                        freeLook = 80,
                        playStyle = "Long Range",
                        fingerCount = "4 Finger",
                        deviceCategory = "Low-End",
                        dpiGuidance = "Keep Existing"
                    )
                )
            }
        }
    }

    fun setQuestionnaireStep(step: Int) {
        _questionnaireStep.value = step.coerceIn(1, 10)
    }

    fun updateAnswers(transform: (QuestionnaireAnswers) -> QuestionnaireAnswers) {
        val updated = transform(_questionnaireAnswers.value)
        _questionnaireAnswers.value = updated
        val specs = _deviceSpecs.value ?: return
        val report = _performanceReport.value ?: return
        val rec = engine.generateRecommendation(specs, report, updated)
        _recommendation.value = rec
        _editingSettings.value = rec.settings
    }

    fun selectGame(gameType: GameType) {
        updateAnswers { it.copy(selectedGame = gameType) }
        viewModelScope.launch {
            prefsRepo.setSelectedGame(gameType.displayName)
        }
    }

    fun updateEditingSetting(key: String, value: Int) {
        val current = _editingSettings.value
        _editingSettings.value = when (key) {
            "general" -> current.copy(general = value)
            "redDot" -> current.copy(redDot = value)
            "scope2x" -> current.copy(scope2x = value)
            "scope4x" -> current.copy(scope4x = value)
            "sniper" -> current.copy(sniper = value)
            "freeLook" -> current.copy(freeLook = value)
            else -> current
        }.clampAll()
    }

    fun resetEditingToGenerated() {
        val rec = _recommendation.value?.settings ?: return
        _editingSettings.value = rec
    }

    fun saveEditingAsActive() {
        val currentRec = _recommendation.value ?: return
        _recommendation.value = currentRec.copy(settings = _editingSettings.value)
    }

    fun saveProfileToDb(customName: String? = null, onSaved: (() -> Unit)? = null) {
        viewModelScope.launch {
            val rec = _recommendation.value?.settings ?: SensitivitySettings()
            val answers = _questionnaireAnswers.value
            val tier = _performanceReport.value?.tier?.label ?: "Mid-Range"
            val name = customName?.takeIf { it.isNotBlank() } ?: "${answers.playStyle} Profile"

            profileRepo.saveProfile(
                SensitivityProfileEntity(
                    name = name,
                    game = answers.selectedGame.displayName,
                    general = rec.general,
                    redDot = rec.redDot,
                    scope2x = rec.scope2x,
                    scope4x = rec.scope4x,
                    sniper = rec.sniper,
                    freeLook = rec.freeLook,
                    playStyle = answers.playStyle,
                    fingerCount = answers.fingerCount,
                    deviceCategory = tier,
                    dpiGuidance = rec.dpiGuidance
                )
            )
            onSaved?.invoke()
        }
    }

    fun deleteProfile(id: Long) {
        viewModelScope.launch {
            profileRepo.deleteProfileById(id)
        }
    }

    fun applyFeedbackTuning(feedbackType: String, specificProblem: String? = null) {
        val current = _recommendation.value?.settings ?: SensitivitySettings()
        val adjusted = engine.applyFeedbackAdjustment(current, feedbackType, specificProblem)
        val currentRec = _recommendation.value ?: return
        _recommendation.value = currentRec.copy(settings = adjusted)
        _editingSettings.value = adjusted
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepo.setDarkMode(enabled)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            prefsRepo.setOnboardingCompleted(true)
        }
    }

    fun acceptPrivacyConsent() {
        viewModelScope.launch {
            prefsRepo.setPrivacyConsentAccepted(true)
        }
    }

    fun copySpecsToClipboard(context: Context) {
        val specs = _deviceSpecs.value ?: return
        val text = """
            Device: ${specs.manufacturer} ${specs.model} (${specs.deviceName})
            Android: ${specs.androidVersion}
            Processor: ${specs.socModel}
            CPU Cores: ${specs.cpuCores} (${specs.cpuArchitecture})
            RAM: ${String.format("%.1f", specs.totalRamGb)} GB (Available: ${String.format("%.1f", specs.availableRamGb)} GB)
            Storage: ${String.format("%.1f", specs.totalStorageGb)} GB
            Display: ${specs.screenResolution} @ ${specs.refreshRateHz.toInt()}Hz
            Gyroscope: ${if (specs.hasGyroscope) "Available" else "Not Available"}
        """.trimIndent()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Device Specs", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Hardware specifications copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    fun copySensitivityToClipboard(context: Context) {
        val rec = _recommendation.value?.settings ?: return
        val game = _questionnaireAnswers.value.selectedGame.displayName
        val text = """
            FF Sensi Pro - $game Settings
            • General: ${rec.general}
            • Red Dot: ${rec.redDot}
            • 2x Scope: ${rec.scope2x}
            • 4x Scope: ${rec.scope4x}
            • Sniper/AWM: ${rec.sniper}
            • Free Look: ${rec.freeLook}
            • DPI: ${rec.dpiGuidance}
        """.trimIndent()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Sensitivity Settings", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "All sensitivity values copied to clipboard!", Toast.LENGTH_SHORT).show()
    }
}
