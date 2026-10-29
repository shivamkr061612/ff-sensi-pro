package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QuestionnaireAnswers
import com.example.ui.components.GlassCard
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.PrimaryBlue

@Composable
fun QuestionnaireScreen(
    currentStep: Int,
    answers: QuestionnaireAnswers,
    onStepChange: (Int) -> Unit,
    onUpdateAnswers: ((QuestionnaireAnswers) -> QuestionnaireAnswers) -> Unit,
    onFinish: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalSteps = 7
    val progress = currentStep.toFloat() / totalSteps.toFloat()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("screen_questionnaire")
    ) {
        ScreenHeader(
            title = "Your Play Style",
            onBackClick = {
                if (currentStep > 1) {
                    onStepChange(currentStep - 1)
                } else {
                    onBackClick()
                }
            },
            stepIndicator = "$currentStep/$totalSteps"
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = PrimaryBlue,
            trackColor = Color(0xFFE2E8F0)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "questionnaireStep"
                ) { step ->
                    when (step) {
                        1 -> Step1AimGoal(answers = answers, onSelect = {
                            onUpdateAnswers { prev -> prev.copy(playStyle = it) }
                        })
                        2 -> Step2Gyroscope(answers = answers, onSelect = {
                            onUpdateAnswers { prev -> prev.copy(gyroscopePreference = it) }
                        })
                        3 -> Step3ControlFingers(answers = answers, onSelect = {
                            onUpdateAnswers { prev -> prev.copy(fingerCount = it) }
                        })
                        4 -> Step4AimPriority(answers = answers, onSelect = {
                            onUpdateAnswers { prev -> prev.copy(controlPreference = it) }
                        })
                        5 -> Step5AimFeeling(answers = answers, onSelect = {
                            onUpdateAnswers { prev -> prev.copy(aimFeeling = it) }
                        })
                        6 -> Step6MainProblem(answers = answers, onSelect = {
                            onUpdateAnswers { prev -> prev.copy(mainProblem = it) }
                        })
                        7 -> Step7CurrentSetupAndDpi(
                            answers = answers,
                            onSetupSelect = { onUpdateAnswers { prev -> prev.copy(currentSetup = it) } },
                            onDpiSelect = { onUpdateAnswers { prev -> prev.copy(changedDpi = it) } }
                        )
                    }
                }
            }

            // Bottom Nav Buttons: Skip & Next
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (currentStep < totalSteps) {
                            onStepChange(currentStep + 1)
                        } else {
                            onFinish()
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("button_questionnaire_skip")
                ) {
                    Text(
                        text = "Skip",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = {
                        if (currentStep < totalSteps) {
                            onStepChange(currentStep + 1)
                        } else {
                            onFinish()
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier
                        .weight(2f)
                        .height(52.dp)
                        .testTag("button_questionnaire_next")
                ) {
                    Text(
                        text = if (currentStep == totalSteps) "Generate Settings" else "Next",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Step1AimGoal(answers: QuestionnaireAnswers, onSelect: (String) -> Unit) {
    Column {
        Text(
            text = "Preferred Play Style",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "What is your primary aiming and movement goal in Free Fire?",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        val options = listOf(
            "Headshot" to "High sensitivity optimized for quick drag headshots",
            "Balanced" to "(Recommended) Neutral balance across all combat ranges",
            "Long Range" to "Low sensitivity focused on stable scope tracking",
            "Aggressive Close-Range" to "Ultra-fast camera swipes for 360° close combat"
        )

        options.forEach { (title, subtitle) ->
            SelectableOptionCard(
                title = title,
                subtitle = subtitle,
                isSelected = answers.playStyle.contains(title, ignoreCase = true) || (title == "Balanced" && answers.playStyle == "Balanced"),
                onClick = { onSelect(title) },
                testTag = "option_$title"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun Step2Gyroscope(answers: QuestionnaireAnswers, onSelect: (String) -> Unit) {
    Column {
        Text(
            text = "Gyroscope Sensor",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Do you tilt your device physically to aim or make micro-adjustments?",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        val options = listOf(
            "On" to "I use gyroscope constantly for recoil control and tracking",
            "Off" to "I aim purely using on-screen thumb / finger swipes",
            "Not Sure" to "I use default settings or haven't calibrated sensors"
        )

        options.forEach { (title, subtitle) ->
            SelectableOptionCard(
                title = title,
                subtitle = subtitle,
                isSelected = answers.gyroscopePreference.equals(title, ignoreCase = true),
                onClick = { onSelect(title) },
                testTag = "option_gyro_$title"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun Step3ControlFingers(answers: QuestionnaireAnswers, onSelect: (String) -> Unit) {
    Column {
        Text(
            text = "Control Style (Finger Count)",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "How many fingers do you place on the screen during gameplay?",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        val options = listOf(
            "2 Finger" to "Standard thumb layout, requires higher drag sensitivity",
            "3 Finger" to "Left index finger for firing / jump, thumb for camera",
            "4 Finger" to "Full claw grip with dedicated aim and trigger fingers",
            "5+ Finger" to "Tablet / multi-finger advanced competitive layout"
        )

        options.forEach { (title, subtitle) ->
            SelectableOptionCard(
                title = title,
                subtitle = subtitle,
                isSelected = answers.fingerCount.equals(title, ignoreCase = true),
                onClick = { onSelect(title) },
                testTag = "option_fingers_$title"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun Step4AimPriority(answers: QuestionnaireAnswers, onSelect: (String) -> Unit) {
    Column {
        Text(
            text = "Screen-Control Preference",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Which screen dragging response best describes your desired feel?",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        val options = listOf(
            "Faster Drag" to "Snappy swipe response for effortless upward head drags",
            "Balanced drag and control" to "Smooth glide with predictable deceleration",
            "Precise, controlled movement" to "Tight cursor movement for pinpoint crosshair placement"
        )

        options.forEach { (title, subtitle) ->
            SelectableOptionCard(
                title = title,
                subtitle = subtitle,
                isSelected = answers.controlPreference.equals(title, ignoreCase = true),
                onClick = { onSelect(title) },
                testTag = "option_control_$title"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun Step5AimFeeling(answers: QuestionnaireAnswers, onSelect: (String) -> Unit) {
    Column {
        Text(
            text = "How would you describe your aim currently?",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Help us calibrate the initial offset against your current experience.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        val options = listOf(
            "My aim moves too slowly" to "I struggle to swipe fast enough to follow moving targets",
            "My aim feels too fast" to "My crosshair flies past opponents when I drag",
            "My aim is inconsistent" to "Some days headshots connect, other days I miss completely",
            "My aim is already comfortable" to "Looking for refined pro-grade optimization"
        )

        options.forEach { (title, subtitle) ->
            SelectableOptionCard(
                title = title,
                subtitle = subtitle,
                isSelected = answers.aimFeeling.equals(title, ignoreCase = true),
                onClick = { onSelect(title) },
                testTag = "option_aim_$title"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun Step6MainProblem(answers: QuestionnaireAnswers, onSelect: (String) -> Unit) {
    Column {
        Text(
            text = "What is your main problem?",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Pinpoint the exact symptom so we can apply targeted compensation.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        val options = listOf(
            "Crosshair does not reach the head" to "Stops at the chest or neck when dragging",
            "Crosshair goes above the head" to "Overshoots the target into the sky",
            "Aim shakes too much" to "Jittery tracking during continuous spray",
            "Scope movement is difficult to control" to "2x or 4x scope flies wildly",
            "Long-range aim is unstable" to "Hard to keep sniper crosshairs aligned",
            "No specific problem; I want a fresh profile" to "Start with a clean calibrated baseline"
        )

        options.forEach { (title, subtitle) ->
            SelectableOptionCard(
                title = title,
                subtitle = subtitle,
                isSelected = answers.mainProblem.equals(title, ignoreCase = true),
                onClick = { onSelect(title) },
                testTag = "option_problem_$title"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun Step7CurrentSetupAndDpi(
    answers: QuestionnaireAnswers,
    onSetupSelect: (String) -> Unit,
    onDpiSelect: (String) -> Unit
) {
    Column {
        Text(
            text = "Current Setup & DPI",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Final preference calibration signals.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Text(
            text = "Current In-Game Setup:",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        listOf("Default settings", "Custom setup", "I do not remember").forEach { item ->
            SelectableOptionCard(
                title = item,
                subtitle = "",
                isSelected = answers.currentSetup.equals(item, ignoreCase = true),
                onClick = { onSetupSelect(item) },
                testTag = "setup_$item"
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Have you changed your phone's DPI in Developer Options?",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        listOf("No", "Yes", "Not sure").forEach { item ->
            SelectableOptionCard(
                title = item,
                subtitle = if (item == "No") "(Recommended - keeps system UI crisp and stable)" else "",
                isSelected = answers.changedDpi.equals(item, ignoreCase = true),
                onClick = { onDpiSelect(item) },
                testTag = "dpi_$item"
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SelectableOptionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String = ""
) {
    val borderColor = if (isSelected) PrimaryBlue else Color(0x334A90E2)
    val borderWidth = if (isSelected) 2.dp else 1.dp

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (subtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(
                        2.dp,
                        if (isSelected) PrimaryBlue else Color(0xFFCBD5E1),
                        CircleShape
                    )
                    .background(if (isSelected) PrimaryBlue else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
