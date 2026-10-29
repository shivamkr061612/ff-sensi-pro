package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IcyBlueBgLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.85f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(700, easing = LinearEasing))
        alpha.animateTo(1f, animationSpec = tween(700))
        delay(1200)
        onTimeout()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F1FC),
                        Color(0xFFF0F6FD),
                        Color(0xFFFFFFFF)
                    )
                )
            )
            .statusBarsPadding()
            .testTag("screen_splash"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // App Emblem with iOS Glass styling
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .scale(scale.value)
                    .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = Color(0x330066FF))
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF6FB1FC),
                                Color(0xFF0066FF),
                                Color(0xFF0052D4)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "F",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 62.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "FF Sensi Pro",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimaryLight
            )

            Text(
                text = "Free Fire Sensitivity Generator",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondaryLight
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Gaming Character Silhouette Badge
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PrimaryBlue.copy(alpha = 0.15f),
                                CyanAccent.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Shield Gaming",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Your Device  •  Your Settings",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondaryLight
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Better Aim",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }
    }
}
