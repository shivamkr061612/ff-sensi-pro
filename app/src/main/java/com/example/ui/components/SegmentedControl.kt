package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PrimaryBlue

@Composable
fun GameSegmentedControl(
    selectedGame: String,
    onGameSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    // Always ensure high-contrast container in both light and dark modes
    val containerBg = if (isDark) Color(0xFF192237) else Color(0xFFE8F1FC)
    val containerBorder = if (isDark) Color(0xFF2A3756) else Color(0xFFC7DCF8)

    val options = listOf(
        "Free Fire MAX" to Icons.Default.SportsEsports,
        "Free Fire India" to Icons.Default.FlashOn
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerBg)
            .border(1.5.dp, containerBorder, RoundedCornerShape(16.dp))
            .padding(4.dp)
            .testTag("game_segmented_control")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEach { (option, icon) ->
                val isSelected = option.equals(selectedGame, ignoreCase = true)

                val selectedBrush = Brush.linearGradient(
                    listOf(Color(0xFF0052D4), Color(0xFF0066FF), Color(0xFF00C8FF))
                )

                // High-contrast text colors - NEVER hide or fade into dark background
                val textColor = if (isSelected) {
                    Color.White
                } else {
                    if (isDark) Color(0xFFE2E8F0) else Color(0xFF0F172A)
                }

                val iconTint = if (isSelected) {
                    Color.White
                } else {
                    PrimaryBlue
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .then(
                            if (isSelected) {
                                Modifier
                                    .shadow(6.dp, RoundedCornerShape(12.dp), spotColor = Color(0x330066FF))
                                    .background(selectedBrush)
                            } else {
                                Modifier.background(Color.Transparent)
                            }
                        )
                        .clickable { onGameSelected(option) }
                        .padding(vertical = 11.dp, horizontal = 8.dp)
                        .testTag("segment_$option"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = option,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            fontSize = 13.sp,
                            color = textColor,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}
