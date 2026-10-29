package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.RecommendationResult
import com.example.model.GameType
import com.example.ui.components.GameSegmentedControl
import com.example.ui.components.GlassCard
import com.example.ui.components.ScreenHeader
import com.example.ui.components.SensitivitySliderItem
import com.example.ui.theme.PrimaryBlue

@Composable
fun SensitivityResultsScreen(
    recommendation: RecommendationResult?,
    selectedGame: String,
    onGameChange: (String) -> Unit,
    onEditValues: () -> Unit,
    onFineTune: () -> Unit,
    onSaveProfile: () -> Unit,
    onCopyAll: (Context) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val settings = recommendation?.settings

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("screen_sensitivity_results")
    ) {
        ScreenHeader(
            title = "Generated Sensitivity",
            onBackClick = onBackClick,
            trailingAction = {
                OutlinedButton(
                    onClick = onEditValues,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("button_edit_sensitivity_header")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Edit",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Game Segmented Tabs
            GameSegmentedControl(
                selectedGame = selectedGame,
                onGameSelected = onGameChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle info
            Text(
                text = "Based on your device & preferences",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Text(
                text = "Recommended Starting Settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            // 6 Sensitivity Sliders Card
            settings?.let { s ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SensitivitySliderItem(
                            title = "General",
                            value = s.general,
                            icon = Icons.Default.CenterFocusWeak
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = Color(0x1F0066FF)
                        )

                        SensitivitySliderItem(
                            title = "Red Dot",
                            value = s.redDot,
                            icon = Icons.Default.CenterFocusWeak
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = Color(0x1F0066FF)
                        )

                        SensitivitySliderItem(
                            title = "2x Scope",
                            value = s.scope2x,
                            icon = Icons.Default.CenterFocusWeak
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = Color(0x1F0066FF)
                        )

                        SensitivitySliderItem(
                            title = "4x Scope",
                            value = s.scope4x,
                            icon = Icons.Default.CenterFocusWeak
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = Color(0x1F0066FF)
                        )

                        SensitivitySliderItem(
                            title = "Sniper / AWM",
                            value = s.sniper,
                            icon = Icons.Default.CenterFocusWeak
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = Color(0x1F0066FF)
                        )

                        SensitivitySliderItem(
                            title = "Free Look",
                            value = s.freeLook,
                            icon = Icons.Default.RemoveRedEye
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // DPI Guidance Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "DPI",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "DPI: Keep Existing (Recommended)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "No DPI change needed for your device. Retains native touch fidelity.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Personalization Factors explanation
            recommendation?.summaryRationale?.let { rationale ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "💡 $rationale",
                        fontSize = 12.sp,
                        color = Color(0xFF1E3A8A),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main CTA Buttons: Save Profile & Click->Success Copy All
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onSaveProfile,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("button_save_profile")
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save Profile",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }

                com.example.ui.components.ClickSuccessButton(
                    text = "Copy All",
                    successText = "Copied!",
                    onClick = { onCopyAll(context) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    testTag = "button_copy_all_sensitivity"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fine-Tune Button
            Button(
                onClick = onFineTune,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE8F1FC),
                    contentColor = PrimaryBlue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("button_go_to_fine_tune")
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Fine-Tune Based On Gameplay Feedback",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
