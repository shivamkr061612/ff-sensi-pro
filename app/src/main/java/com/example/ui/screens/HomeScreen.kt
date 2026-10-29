package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdMobBanner
import com.example.model.DeviceSpecs
import com.example.model.PerformanceReport
import com.example.model.PerformanceTier
import com.example.ui.components.AppHomeHeader
import com.example.ui.components.BeforeAfterComparison
import com.example.ui.components.CountUpText
import com.example.ui.components.CursorTourGuide
import com.example.ui.components.GlassCard
import com.example.ui.components.ProcessDrawPipeline
import com.example.ui.components.PulsingStatusPing
import com.example.ui.components.ReviewShuffleCard
import com.example.ui.components.UsVsThemCard
import com.example.ui.components.WordRotator
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TierHighEnd
import com.example.ui.theme.TierLowEnd
import com.example.ui.theme.TierMidRange

@Composable
fun HomeScreen(
    specs: DeviceSpecs?,
    report: PerformanceReport?,
    onNavigateToGenerate: () -> Unit,
    onNavigateToDeviceDetails: () -> Unit,
    onNavigateToPerformance: () -> Unit,
    onNavigateToProfiles: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var isHeroVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isHeroVisible = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .testTag("screen_home")
    ) {
        // App Header
        AppHomeHeader(onSettingsClick = onNavigateToSettings)

        // Hero Reveal Animation
        AnimatedVisibility(
            visible = isHeroVisible,
            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -40 }
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // Word Rotator Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFEBF3FE))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Calibrate: ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        WordRotator()
                    }

                    PulsingStatusPing(
                        label = "${specs?.refreshRateHz?.toInt() ?: 90}Hz ACTIVE",
                        dotColor = Color(0xFF10B981)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bento Item 1: Device Detection Card (Fixed Responsiveness, No Text Wrap Bug)
                specs?.let { device ->
                    DeviceBentoCard(
                        specs = device,
                        report = report,
                        onClick = onNavigateToDeviceDetails
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bento Item 2: Gaming Performance Card
                report?.let { perf ->
                    GamingPerformanceBentoCard(
                        report = perf,
                        onClick = onNavigateToPerformance
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bento Grid Quick Actions Header
        Text(
            text = "Bento Quick Actions",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Bento 2x2 Grid with vibrant multi-colors
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Generate Sensitivity (Primary Neon Blue)
                VibrantBentoActionCard(
                    title = "Generate Sensi",
                    subtitle = "0-200 Calibrated",
                    icon = Icons.Default.AutoAwesome,
                    badge = "AI Engine",
                    gradient = listOf(Color(0xFF0052D4), Color(0xFF4364F7), Color(0xFF6FB1FC)),
                    onClick = onNavigateToGenerate,
                    modifier = Modifier.weight(1f),
                    testTag = "action_generate_sensitivity"
                )

                // 2. Saved Profiles (Royal Purple & Indigo)
                VibrantBentoActionCard(
                    title = "Saved Profiles",
                    subtitle = "Claw & Thumb",
                    icon = Icons.Default.Bookmark,
                    badge = "Local Room",
                    gradient = listOf(Color(0xFF6B21A8), Color(0xFF9333EA), Color(0xFFA855F7)),
                    onClick = onNavigateToProfiles,
                    modifier = Modifier.weight(1f),
                    testTag = "action_saved_profiles"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 3. Device Details (Emerald Teal)
                VibrantBentoActionCard(
                    title = "Device Specs",
                    subtitle = "RAM & Sensors",
                    icon = Icons.Default.PhoneAndroid,
                    badge = "Real Hardware",
                    gradient = listOf(Color(0xFF065F46), Color(0xFF059669), Color(0xFF10B981)),
                    onClick = onNavigateToDeviceDetails,
                    modifier = Modifier.weight(1f),
                    testTag = "action_device_details"
                )

                // 4. Settings (Warm Sunset Coral)
                VibrantBentoActionCard(
                    title = "Preferences",
                    subtitle = "Theme & Limits",
                    icon = Icons.Default.Settings,
                    badge = "Config",
                    gradient = listOf(Color(0xFFC2410C), Color(0xFFEA580C), Color(0xFFFB923C)),
                    onClick = onNavigateToSettings,
                    modifier = Modifier.weight(1f),
                    testTag = "action_settings"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Process Pipeline Draw
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Calculation Pipeline",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ProcessDrawPipeline()
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Before / After Real Combat Comparison Card
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            BeforeAfterComparison()
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Cursor Tour Drag Demonstration Guide
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            CursorTourGuide()
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Us vs Them Feature Comparison
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            UsVsThemCard()
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Review Shuffle Card
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            ReviewShuffleCard()
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Non-intrusive AdMob Banner
        AdMobBanner()

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Fixed Device Bento Card with Clean Overflow & No Word Break Bug
@Composable
fun DeviceBentoCard(
    specs: DeviceSpecs,
    report: PerformanceReport?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tier = report?.tier ?: PerformanceTier.MID_RANGE
    val badgeColor = when (tier) {
        PerformanceTier.HIGH_END -> TierHighEnd
        PerformanceTier.MID_RANGE -> TierMidRange
        PerformanceTier.LOW_END -> TierLowEnd
        PerformanceTier.UNKNOWN -> Color(0xFF64748B)
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("card_your_device")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "DETECTED HARDWARE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = PrimaryBlue
                )

                // Single-line non-wrapping badge!
                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = tier.label,
                        color = badgeColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = "Device",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Title Column with weight(1f) to prevent pushing the badge or text downwards!
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${specs.manufacturer} ${specs.model}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${specs.deviceName} • Android ${specs.sdkInt}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Specs Chips Row with Count-Up Numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BentoSpecChip(
                    text = "Android ${specs.sdkInt}",
                    icon = Icons.Default.PhoneAndroid,
                    color = PrimaryBlue
                )
                BentoSpecChip(
                    text = "${String.format("%.0f", specs.totalRamGb)} GB RAM",
                    icon = Icons.Default.Memory,
                    color = Color(0xFF7C3AED)
                )
                BentoSpecChip(
                    text = "${specs.refreshRateHz.toInt()} Hz Display",
                    icon = Icons.Default.Speed,
                    color = Color(0xFF059669)
                )
            }
        }
    }
}

@Composable
fun GamingPerformanceBentoCard(
    report: PerformanceReport,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val badgeColor = when (report.tier) {
        PerformanceTier.HIGH_END -> TierHighEnd
        PerformanceTier.MID_RANGE -> TierMidRange
        PerformanceTier.LOW_END -> TierLowEnd
        PerformanceTier.UNKNOWN -> Color(0xFF64748B)
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("card_gaming_performance")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(badgeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Gaming Performance",
                        tint = badgeColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = report.tier.label,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(badgeColor)
                        )
                    }
                    Text(
                        text = report.tier.subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                CountUpText(
                    targetNumber = report.confidencePercent,
                    suffix = "%",
                    fontSize = 16,
                    color = badgeColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun BentoSpecChip(
    text: String,
    icon: ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun VibrantBentoActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Box(
        modifier = modifier
            .height(130.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(gradient))
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x2EFFFFFF))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = Color.White,
                    lineHeight = 17.sp
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xE6FFFFFF)
                )
            }
        }
    }
}
