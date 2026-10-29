package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TierHighEnd
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

// 1. WORD ROTATOR
@Composable
fun WordRotator(
    words: List<String> = listOf("Drag Headshots ⚡", "90 FPS Smoothness 🎯", "Zero Recoil Scope 🔭", "360° Pro Reflex 🔥"),
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2400)
            currentIndex = (currentIndex + 1) % words.size
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        AnimatedContent(
            targetState = words[currentIndex],
            transitionSpec = {
                (slideInVertically { height -> height } + fadeIn()).togetherWith(
                    slideOutVertically { height -> -height } + fadeOut()
                )
            },
            label = "wordRotator"
        ) { word ->
            Text(
                text = word,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }
    }
}

// 2. SALE / STATUS PING (RADAR PULSE)
@Composable
fun PulsingStatusPing(
    label: String,
    dotColor: Color = Color(0xFF10B981),
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(dotColor.copy(alpha = 0.12f))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(10.dp)) {
            // Expanding wave
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(dotColor.copy(alpha = alpha))
            )
            // Core solid dot
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = dotColor
        )
    }
}

// 3. COUNT-UP STATS (SMOOTH NUMBER INTERPOLATION)
@Composable
fun CountUpText(
    targetNumber: Int,
    suffix: String = "",
    durationMs: Int = 800,
    fontSize: Int = 18,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = PrimaryBlue,
    modifier: Modifier = Modifier
) {
    val countAnim = remember { Animatable(0f) }

    LaunchedEffect(targetNumber) {
        countAnim.snapTo(0f)
        countAnim.animateTo(
            targetValue = targetNumber.toFloat(),
            animationSpec = tween(durationMillis = durationMs, easing = FastOutSlowInEasing)
        )
    }

    Text(
        text = "${countAnim.value.roundToInt()}$suffix",
        fontSize = fontSize.sp,
        fontWeight = fontWeight,
        color = color,
        modifier = modifier
    )
}

// 4. CURSOR TOUR (ONBOARDING & TUTORIAL DRAG GUIDE)
@Composable
fun CursorTourGuide(
    title: String = "Drag Headshot Motion",
    subtitle: String = "Swipe upward rapidly from fire button to head level",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursorTransition")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 48f,
        targetValue = -36f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cursorY"
    )
    val handAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cursorAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE8F1FC), Color(0xFFF3F8FF), Color(0xFFFFFFFF))
                )
            )
            .border(1.dp, Color(0x330066FF), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                PulsingStatusPing(label = "AIM TOUR GUIDE", dotColor = PrimaryBlue)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas drag trajectory
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width / 2f
                    val startY = size.height * 0.75f
                    val endY = size.height * 0.2f

                    // Dotted arc trajectory line
                    val path = Path().apply {
                        moveTo(centerX, startY)
                        quadraticTo(centerX + 15f, (startY + endY) / 2f, centerX, endY)
                    }
                    drawPath(
                        path = path,
                        color = Color(0x660066FF),
                        style = Stroke(
                            width = 3.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f),
                            cap = StrokeCap.Round
                        )
                    )

                    // Target head circle
                    drawCircle(
                        color = Color(0xFFFF3B30),
                        radius = 8.dp.toPx(),
                        center = Offset(centerX, endY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = Offset(centerX, endY)
                    )

                    // Origin fire button circle
                    drawCircle(
                        color = Color(0xFF0066FF),
                        radius = 12.dp.toPx(),
                        center = Offset(centerX, startY)
                    )
                }

                // Moving pointer hand
                Box(
                    modifier = Modifier
                        .offset { IntOffset(0, offsetY.roundToInt()) }
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = "Swipe Up Hand",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// 5. PROCESS DRAW (CALCULATION PIPELINE)
@Composable
fun ProcessDrawPipeline(modifier: Modifier = Modifier) {
    val steps = listOf(
        "1. Hardware Scan" to "SoC & Hz",
        "2. ML Calibration" to "Drag Archetype",
        "3. Optimal 0-200" to "Target Ready"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF3F8FF))
            .border(1.dp, Color(0x334A90E2), RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, (stepTitle, stepSub) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(PrimaryBlue, CyanAccent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stepTitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stepSub,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                )
            }

            if (index < steps.size - 1) {
                Text(
                    text = "➔",
                    color = PrimaryBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

// 6. BEFORE / AFTER INTERACTIVE COMPARISON
@Composable
fun BeforeAfterComparison(modifier: Modifier = Modifier) {
    var showTuned by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFFFFFF))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Real Combat Comparison",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Toggle Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F1FC))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!showTuned) Color(0xFFEF4444) else Color.Transparent)
                            .clickable { showTuned = false }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Default",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!showTuned) Color.White else Color(0xFF64748B)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (showTuned) PrimaryBlue else Color.Transparent)
                            .clickable { showTuned = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Calibrated",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showTuned) Color.White else Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Metric 1
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (showTuned) Color(0xFFECFDF5) else Color(0xFFFFF1F2))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Drag Headshot Rate",
                            fontSize = 10.sp,
                            color = Color(0xFF475569)
                        )
                        Text(
                            text = if (showTuned) "88.4%" else "34.2%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (showTuned) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    }
                }

                // Metric 2
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (showTuned) Color(0xFFEFF6FF) else Color(0xFFFFF1F2))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Crosshair Lock Stability",
                            fontSize = 10.sp,
                            color = Color(0xFF475569)
                        )
                        Text(
                            text = if (showTuned) "Smooth Tracking" else "Chest Glitched",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showTuned) PrimaryBlue else Color(0xFFEF4444)
                        )
                    }
                }
            }
        }
    }
}

// 7. US VS THEM COMPARISON TABLE
@Composable
fun UsVsThemCard(modifier: Modifier = Modifier) {
    val features = listOf(
        Triple("Full 0-200 Granular Scale", true, false),
        Triple("Physical Hardware & Refresh Rate Check", true, false),
        Triple("Play Style & Finger Layout Tuning", true, false),
        Triple("No In-Game Hacks (100% Ban Safe)", true, true),
        Triple("Zero Remote Account or Data Tracking", true, false)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFFFFFF))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Feature Matrix",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Sensi Pro",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Text(
                        text = "Others",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            features.forEach { (feature, us, them) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = feature,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(36.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (us) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (us) Color(0xFF10B981) else Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                        Icon(
                            imageVector = if (them) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (them) Color(0xFF10B981) else Color(0xFFCBD5E1),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                HorizontalDivider(color = Color(0xFFF1F5F9))
            }
        }
    }
}

// 8. REVIEW SHUFFLE (TAP TO SHUFFLE COMMUNITY REVIEWS)
@Composable
fun ReviewShuffleCard(modifier: Modifier = Modifier) {
    val reviews = listOf(
        Triple("Sameer Roy (OnePlus 11R)", "5.0", "Red dot sensitivity at 155 with fast drag fixed my chest stuck problem immediately!"),
        Triple("Arjun Patel (Redmi Note 12)", "5.0", "My 60Hz display feels so responsive now. Drag headshots are super smooth."),
        Triple("Vikram S. (iQOO Neo 7)", "5.0", "Using 4-finger claw settings. Sniper 4x tracking is laser accurate!"),
        Triple("Deepak Rawat (Realme C55)", "5.0", "Low-end device preset works wonders without thermal lag. Recommended!")
    )

    var reviewIndex by remember { mutableIntStateOf(0) }
    val current = reviews[reviewIndex]

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFF8FAFD), Color(0xFFEFF6FF))
                )
            )
            .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(18.dp))
            .clickable { reviewIndex = (reviewIndex + 1) % reviews.size }
            .padding(16.dp)
            .testTag("card_review_shuffle")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Verified Review",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )
                }

                Text(
                    text = "Tap to Shuffle 🔀",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(
                targetState = current,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "reviewContent"
            ) { review ->
                Column {
                    Text(
                        text = "\"${review.third}\"",
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "— ${review.first}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }
        }
    }
}

// 9. CLICK → SUCCESS BUTTON (SPRING SCALE & CHECKMARK ANIMATION)
@Composable
fun ClickSuccessButton(
    text: String,
    successText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    var isSuccess by remember { mutableStateOf(false) }
    val scale = remember { Animatable(1f) }

    Button(
        onClick = {
            isSuccess = true
            onClick()
        },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSuccess) Color(0xFF10B981) else PrimaryBlue
        ),
        modifier = modifier
            .scale(scale.value)
            .testTag(testTag)
    ) {
        AnimatedContent(
            targetState = isSuccess,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "successTransform"
        ) { success ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (success) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = successText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
