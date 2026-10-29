package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ads.AdManager
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.BottomTab
import com.example.ui.screens.AboutLegalScreen
import com.example.ui.screens.DeviceDetailsScreen
import com.example.ui.screens.EditSensitivityScreen
import com.example.ui.screens.FeedbackTuningScreen
import com.example.ui.screens.HardwareUnavailableScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OfflineScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PerformanceAnalysisScreen
import com.example.ui.screens.PrivacyConsentDialog
import com.example.ui.screens.QuestionnaireScreen
import com.example.ui.screens.SavedProfilesScreen
import com.example.ui.screens.SelectGameScreen
import com.example.ui.screens.SensitivityResultsScreen
import com.example.ui.screens.SensitivityTipsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.FFSensiProTheme
import com.example.ui.theme.IcyBlueBgDark
import com.example.ui.theme.IcyBlueBgLight
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AdManager.initialize(this)

        setContent {
            val isDarkModePref by viewModel.isDarkMode.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val useDark = isDarkModePref

            FFSensiProTheme(darkTheme = useDark) {
                MainAppContent(
                    viewModel = viewModel,
                    useDark = useDark
                )
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: MainViewModel,
    useDark: Boolean
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "splash"

    val specs by viewModel.deviceSpecs.collectAsState()
    val report by viewModel.performanceReport.collectAsState()
    val recommendation by viewModel.recommendation.collectAsState()
    val editingSettings by viewModel.editingSettings.collectAsState()
    val savedProfiles by viewModel.savedProfiles.collectAsState()
    val answers by viewModel.questionnaireAnswers.collectAsState()
    val currentStep by viewModel.questionnaireStep.collectAsState()
    val hasCompletedOnboarding by viewModel.hasCompletedOnboarding.collectAsState()
    val isPrivacyConsentAccepted by viewModel.isPrivacyConsentAccepted.collectAsState()

    var showPrivacyDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isPrivacyConsentAccepted) {
        if (!isPrivacyConsentAccepted) {
            showPrivacyDialog = true
        }
    }

    val bgGradient = if (useDark) {
        Brush.verticalGradient(
            listOf(Color(0xFF0F172A), IcyBlueBgDark, Color(0xFF020617))
        )
    } else {
        Brush.verticalGradient(
            listOf(Color(0xFFF8FAFD), IcyBlueBgLight, Color(0xFFEDF4FD))
        )
    }

    val showBottomBar = currentRoute in listOf("home", "saved_profiles", "settings")

    val currentTab = when (currentRoute) {
        "saved_profiles" -> BottomTab.PROFILES
        "settings" -> BottomTab.SETTINGS
        else -> BottomTab.HOME
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavBar(
                    selectedTab = currentTab,
                    onTabSelected = { tab ->
                        if (tab.route != currentRoute) {
                            navController.navigate(tab.route) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "splash"
            ) {
                composable("splash") {
                    SplashScreen(
                        onTimeout = {
                            val nextDestination = if (hasCompletedOnboarding) "home" else "onboarding"
                            navController.navigate(nextDestination) {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    )
                }

                composable("onboarding") {
                    OnboardingScreen(
                        onGetStarted = {
                            viewModel.completeOnboarding()
                            navController.navigate("home") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        }
                    )
                }

                composable("home") {
                    HomeScreen(
                        specs = specs,
                        report = report,
                        onNavigateToGenerate = { navController.navigate("select_game") },
                        onNavigateToDeviceDetails = { navController.navigate("device_details") },
                        onNavigateToPerformance = { navController.navigate("performance_analysis") },
                        onNavigateToProfiles = { navController.navigate("saved_profiles") },
                        onNavigateToSettings = { navController.navigate("settings") }
                    )
                }

                composable("device_details") {
                    BackHandler { navController.popBackStack() }
                    DeviceDetailsScreen(
                        specs = specs,
                        onBackClick = { navController.popBackStack() },
                        onCopySpecs = { ctx -> viewModel.copySpecsToClipboard(ctx) }
                    )
                }

                composable("performance_analysis") {
                    BackHandler { navController.popBackStack() }
                    PerformanceAnalysisScreen(
                        report = report,
                        onBackClick = { navController.popBackStack() },
                        onReassess = { viewModel.detectDevice() }
                    )
                }

                composable("select_game") {
                    BackHandler { navController.popBackStack() }
                    SelectGameScreen(
                        selectedGame = answers.selectedGame,
                        onGameSelected = { game -> viewModel.selectGame(game) },
                        onBackClick = { navController.popBackStack() },
                        onNextClick = {
                            viewModel.setQuestionnaireStep(1)
                            navController.navigate("questionnaire")
                        }
                    )
                }

                composable("questionnaire") {
                    BackHandler {
                        if (currentStep > 1) {
                            viewModel.setQuestionnaireStep(currentStep - 1)
                        } else {
                            navController.popBackStack()
                        }
                    }
                    QuestionnaireScreen(
                        currentStep = currentStep,
                        answers = answers,
                        onStepChange = { step -> viewModel.setQuestionnaireStep(step) },
                        onUpdateAnswers = { transform -> viewModel.updateAnswers(transform) },
                        onFinish = {
                            navController.navigate("sensitivity_results") {
                                popUpTo("select_game") { inclusive = false }
                            }
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable("sensitivity_results") {
                    BackHandler { navController.popBackStack() }
                    SensitivityResultsScreen(
                        recommendation = recommendation,
                        selectedGame = answers.selectedGame.displayName,
                        onGameChange = { gameName ->
                            val g = if (gameName.contains("MAX", ignoreCase = true)) {
                                com.example.model.GameType.FREE_FIRE_MAX
                            } else {
                                com.example.model.GameType.FREE_FIRE_INDIA
                            }
                            viewModel.selectGame(g)
                        },
                        onEditValues = { navController.navigate("edit_sensitivity") },
                        onFineTune = { navController.navigate("feedback_tuning") },
                        onSaveProfile = {
                            viewModel.saveProfileToDb {
                                navController.navigate("saved_profiles")
                            }
                        },
                        onCopyAll = { ctx -> viewModel.copySensitivityToClipboard(ctx) },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable("edit_sensitivity") {
                    BackHandler { navController.popBackStack() }
                    EditSensitivityScreen(
                        settings = editingSettings,
                        selectedGame = answers.selectedGame.displayName,
                        onGameChange = { gameName ->
                            val g = if (gameName.contains("MAX", ignoreCase = true)) {
                                com.example.model.GameType.FREE_FIRE_MAX
                            } else {
                                com.example.model.GameType.FREE_FIRE_INDIA
                            }
                            viewModel.selectGame(g)
                        },
                        onSettingChange = { key, value ->
                            viewModel.updateEditingSetting(key, value)
                        },
                        onReset = { viewModel.resetEditingToGenerated() },
                        onSave = {
                            viewModel.saveEditingAsActive()
                            navController.popBackStack()
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable("saved_profiles") {
                    SavedProfilesScreen(
                        profiles = savedProfiles,
                        selectedGame = answers.selectedGame.displayName,
                        onGameChange = { gameName ->
                            val g = if (gameName.contains("MAX", ignoreCase = true)) {
                                com.example.model.GameType.FREE_FIRE_MAX
                            } else {
                                com.example.model.GameType.FREE_FIRE_INDIA
                            }
                            viewModel.selectGame(g)
                        },
                        onCreateNewProfile = { navController.navigate("select_game") },
                        onSelectProfile = { profile ->
                            viewModel.updateAnswers {
                                it.copy(
                                    selectedGame = if (profile.game.contains("MAX", ignoreCase = true)) {
                                        com.example.model.GameType.FREE_FIRE_MAX
                                    } else {
                                        com.example.model.GameType.FREE_FIRE_INDIA
                                    },
                                    playStyle = profile.playStyle,
                                    fingerCount = profile.fingerCount
                                )
                            }
                            viewModel.updateEditingSetting("general", profile.general)
                            viewModel.updateEditingSetting("redDot", profile.redDot)
                            viewModel.updateEditingSetting("scope2x", profile.scope2x)
                            viewModel.updateEditingSetting("scope4x", profile.scope4x)
                            viewModel.updateEditingSetting("sniper", profile.sniper)
                            viewModel.updateEditingSetting("freeLook", profile.freeLook)
                            navController.navigate("sensitivity_results")
                        },
                        onDeleteProfile = { id -> viewModel.deleteProfile(id) },
                        onBackClick = { navController.navigate("home") }
                    )
                }

                composable("feedback_tuning") {
                    BackHandler { navController.popBackStack() }
                    FeedbackTuningScreen(
                        onSubmitFeedback = { feedbackType, specificProblem ->
                            viewModel.applyFeedbackTuning(feedbackType, specificProblem)
                            navController.navigate("sensitivity_results") {
                                popUpTo("sensitivity_results") { inclusive = true }
                            }
                        },
                        onSkip = { navController.popBackStack() },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable("settings") {
                    SettingsScreen(
                        isDarkMode = useDark,
                        onToggleDarkMode = { enabled: Boolean -> viewModel.setDarkMode(enabled) },
                        onNavigateToSelectGame = { navController.navigate("select_game") },
                        onNavigateToSensitivityTips = { navController.navigate("sensitivity_tips") },
                        onNavigateToAbout = { navController.navigate("about_legal") },
                        onNavigateToPrivacy = { showPrivacyDialog = true },
                        onBackClick = { navController.navigate("home") }
                    )
                }

                composable("about_legal") {
                    BackHandler { navController.popBackStack() }
                    AboutLegalScreen(
                        onPrivacyPolicyClick = { showPrivacyDialog = true },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable("sensitivity_tips") {
                    BackHandler { navController.popBackStack() }
                    SensitivityTipsScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable("hardware_unavailable") {
                    BackHandler { navController.popBackStack() }
                    HardwareUnavailableScreen(
                        onRetry = {
                            viewModel.detectDevice()
                            navController.popBackStack()
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable("offline") {
                    BackHandler { navController.popBackStack() }
                    OfflineScreen(
                        onRetry = { navController.popBackStack() },
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            if (showPrivacyDialog) {
                PrivacyConsentDialog(
                    onAccept = {
                        showPrivacyDialog = false
                        viewModel.acceptPrivacyConsent()
                    },
                    onLearnMore = {
                        showPrivacyDialog = false
                        navController.navigate("about_legal")
                    }
                )
            }
        }
    }
}
