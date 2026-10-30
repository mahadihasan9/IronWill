package org.ironman.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ironman.ui.theme.AccentGold
import org.ironman.ui.theme.AccentGreen
import org.ironman.ui.theme.DarkSurface
import org.ironman.ui.theme.DarkSurfaceVariant
import org.ironman.ui.theme.PrimaryCyan
import org.ironman.ui.theme.TextMuted
import org.ironman.ui.theme.TextPrimary
import org.ironman.ui.theme.TextSecondary
import org.ironman.ui.viewmodel.MainViewModel

data class Milestone(
    val title: String,
    val days: Int
)

@Composable
fun StatsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.stats.collectAsState()
    val streakTime by viewModel.currentStreakTime.collectAsState()
    val targetGoalDays by viewModel.targetGoalDays.collectAsState()

    val currentDays = streakTime.days

    val milestones = listOf(
        Milestone("1 Day", 1),
        Milestone("3 Days", 3),
        Milestone("1 Week", 7),
        Milestone("2 Weeks", 14),
        Milestone("1 Month", 30),
        Milestone("60 Days", 60),
        Milestone("90 Days", 90),
        Milestone("180 Days", 180),
        Milestone("1 Year", 365)
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Statistics & Achievements",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stat Cards Grid (2x2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBox(
                title = "Longest Streak",
                value = formatMillisToDaysHours(stats.longestStreakMillis),
                modifier = Modifier.weight(1f)
            )
            StatBox(
                title = "Current Streak",
                value = formatMillisToDaysHours(stats.currentStreakMillis),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBox(
                title = "Total Resets",
                value = "${stats.totalResets}",
                modifier = Modifier.weight(1f)
            )
            StatBox(
                title = "Average Streak",
                value = formatMillisToDaysHours(stats.averageStreakMillis),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Target Goal Selector
        Text(
            text = "Set Target Goal",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(7, 30, 90, 365).forEach { days ->
                FilterChip(
                    selected = targetGoalDays == days,
                    onClick = { viewModel.updateTargetGoal(days) },
                    label = { Text("$days Days") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryCyan,
                        selectedLabelColor = DarkSurface,
                        containerColor = DarkSurface,
                        labelColor = TextPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Milestones
        Text(
            text = "Milestones & Badges",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))

        milestones.forEach { milestone ->
            val isUnlocked = currentDays >= milestone.days
            MilestoneCard(milestone = milestone, isUnlocked = isUnlocked)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun StatBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                color = PrimaryCyan,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MilestoneCard(
    milestone: Milestone,
    isUnlocked: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) DarkSurfaceVariant else DarkSurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isUnlocked) AccentGold else TextMuted
                )
                Spacer(modifier = Modifier.padding(start = 12.dp))
                Column {
                    Text(
                        text = milestone.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) TextPrimary else TextMuted
                    )
                    Text(
                        text = if (isUnlocked) "Milestone Reached!" else "${milestone.days} days goal",
                        fontSize = 12.sp,
                        color = if (isUnlocked) AccentGreen else TextSecondary
                    )
                }
            }
            if (isUnlocked) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Unlocked",
                    tint = AccentGreen
                )
            }
        }
    }
}

private fun formatMillisToDaysHours(millis: Long): String {
    val days = millis / (1000 * 60 * 60 * 24)
    val hours = (millis / (1000 * 60 * 60)) % 24
    return if (days > 0) "${days}d ${hours}h" else "${hours}h"
}
