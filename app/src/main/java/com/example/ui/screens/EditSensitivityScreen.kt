package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SensitivitySettings
import com.example.ui.components.GameSegmentedControl
import com.example.ui.components.GlassCard
import com.example.ui.components.ScreenHeader
import com.example.ui.components.SensitivitySliderItem
import com.example.ui.theme.PrimaryBlue

@Composable
fun EditSensitivityScreen(
    settings: SensitivitySettings,
    selectedGame: String,
    onGameChange: (String) -> Unit,
    onSettingChange: (String, Int) -> Unit,
    onReset: () -> Unit,
    onSave: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("screen_edit_sensitivity")
    ) {
        ScreenHeader(
            title = "Edit Sensitivity",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            GameSegmentedControl(
                selectedGame = selectedGame,
                onGameSelected = onGameChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Adjust Slider Values (0 - 200)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SensitivitySliderItem(
                        title = "General",
                        value = settings.general,
                        icon = Icons.Default.CenterFocusWeak,
                        onValueChange = { onSettingChange("general", it) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color(0x1F0066FF)
                    )

                    SensitivitySliderItem(
                        title = "Red Dot",
                        value = settings.redDot,
                        icon = Icons.Default.CenterFocusWeak,
                        onValueChange = { onSettingChange("redDot", it) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color(0x1F0066FF)
                    )

                    SensitivitySliderItem(
                        title = "2x Scope",
                        value = settings.scope2x,
                        icon = Icons.Default.CenterFocusWeak,
                        onValueChange = { onSettingChange("scope2x", it) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color(0x1F0066FF)
                    )

                    SensitivitySliderItem(
                        title = "4x Scope",
                        value = settings.scope4x,
                        icon = Icons.Default.CenterFocusWeak,
                        onValueChange = { onSettingChange("scope4x", it) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color(0x1F0066FF)
                    )

                    SensitivitySliderItem(
                        title = "Sniper / AWM",
                        value = settings.sniper,
                        icon = Icons.Default.CenterFocusWeak,
                        onValueChange = { onSettingChange("sniper", it) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color(0x1F0066FF)
                    )

                    SensitivitySliderItem(
                        title = "Free Look",
                        value = settings.freeLook,
                        icon = Icons.Default.RemoveRedEye,
                        onValueChange = { onSettingChange("freeLook", it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons: Reset & Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedButton(
                    onClick = onReset,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("button_reset_sliders")
                ) {
                    Text(
                        text = "Reset",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onSave,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("button_save_sliders")
                ) {
                    Text(
                        text = "Save",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
