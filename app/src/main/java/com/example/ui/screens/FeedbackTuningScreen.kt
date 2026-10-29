package com.example.ui.screens

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
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TierHighEnd
import com.example.ui.theme.TierLowEnd

@Composable
fun FeedbackTuningScreen(
    onSubmitFeedback: (feedbackType: String, specificProblem: String?) -> Unit,
    onSkip: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFeedback by remember { mutableStateOf("just_right") }
    var selectedProblem by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("screen_feedback_tuning")
    ) {
        ScreenHeader(
            title = "Fine-Tune Sensitivity",
            onBackClick = onBackClick
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
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "How is your aim feeling?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Help us fine-tune your sensitivity in small increments.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                // 3 Feeling Cards in a Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeedbackOptionCard(
                        title = "Too Fast",
                        subtitle = "(Decrease)",
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = Color(0xFFEF4444),
                        isSelected = selectedFeedback == "too_fast",
                        onClick = { selectedFeedback = "too_fast" },
                        modifier = Modifier.weight(1f),
                        testTag = "feedback_too_fast"
                    )

                    FeedbackOptionCard(
                        title = "Just Right",
                        subtitle = "(Keep)",
                        icon = Icons.Default.EmojiEmotions,
                        iconTint = Color(0xFF10B981),
                        isSelected = selectedFeedback == "just_right",
                        onClick = { selectedFeedback = "just_right" },
                        modifier = Modifier.weight(1f),
                        testTag = "feedback_just_right"
                    )

                    FeedbackOptionCard(
                        title = "Too Slow",
                        subtitle = "(Increase)",
                        icon = Icons.Default.Speed,
                        iconTint = Color(0xFF3B82F6),
                        isSelected = selectedFeedback == "too_slow",
                        onClick = { selectedFeedback = "too_slow" },
                        modifier = Modifier.weight(1f),
                        testTag = "feedback_too_slow"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Targeted Symptom Adjustment (Optional)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val problems = listOf(
                    "above_head" to "Crosshair goes above the target",
                    "below_head" to "Crosshair stops below the head",
                    "unstable_scope" to "Scope tracking is unstable"
                )

                problems.forEach { (key, label) ->
                    val isChecked = selectedProblem == key
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(
                                width = if (isChecked) 2.dp else 1.dp,
                                color = if (isChecked) PrimaryBlue else Color(0x334A90E2),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                selectedProblem = if (selectedProblem == key) null else key
                            }
                            .testTag("problem_$key")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp,
                                        if (isChecked) PrimaryBlue else Color(0xFFCBD5E1),
                                        CircleShape
                                    )
                                    .background(if (isChecked) PrimaryBlue else Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChecked) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "We'll adjust your sensitivity slightly based on your feedback.",
                        fontSize = 12.sp,
                        color = Color(0xFF1E3A8A),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Bottom CTAs: Submit & Skip
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { onSubmitFeedback(selectedFeedback, selectedProblem) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("button_submit_feedback")
                ) {
                    Text(
                        text = "Submit",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.testTag("button_skip_feedback")
                ) {
                    Text(
                        text = "Skip for now",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FeedbackOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val borderColor = if (isSelected) PrimaryBlue else Color(0x334A90E2)
    val borderWidth = if (isSelected) 2.dp else 1.dp

    GlassCard(
        modifier = modifier
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
            )
        }
    }
}
