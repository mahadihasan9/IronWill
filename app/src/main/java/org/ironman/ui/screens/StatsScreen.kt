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
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    val titleEn: String,
    val titleBn: String,
    val titleAr: String,
    val days: Int
) {
    fun getTitle(lang: String): String = when (lang) {
        "bn" -> titleBn
        "ar" -> titleAr
        else -> titleEn
    }
}

@Composable
fun StatsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.stats.collectAsState()
    val streakTime by viewModel.currentStreakTime.collectAsState()
    val targetGoalDays by viewModel.targetGoalDays.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()

    val currentDays = streakTime.days

    val milestones = listOf(
        Milestone("3 Days Goal", "৩ দিনের গোল", "هدف 3 أيام", 3),
        Milestone("4 Days Goal", "৪ দিনের গোল", "هدف 4 أيام", 4),
        Milestone("5 Days Goal", "৫ দিনের গোল", "هدف 5 أيام", 5),
        Milestone("1 Week Goal", "১ সপ্তাহের গোল", "هدف أسبوع", 7),
        Milestone("2 Weeks Goal", "২ সপ্তাহের গোল", "هدف أسبوعين", 14),
        Milestone("1 Month Goal", "১ মাসের গোল", "هدف شهر", 30),
        Milestone("60 Days Goal", "৬০ দিনের গোল", "هدف 60 يوم", 60),
        Milestone("90 Days Goal", "৯০ দিনের গোল", "هدف 90 يوم", 90),
        Milestone("180 Days Goal", "১৮০ দিনের গোল", "هدف 180 يوم", 180),
        Milestone("1 Year Goal", "১ বছরের গোল", "هدف سنة", 365)
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = when (appLanguage) {
                "bn" -> "পরিসংখ্যান ও অর্জনসমূহ"
                "ar" -> "الإحصائيات والإنجازات"
                else -> "Statistics & Achievements"
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Current Target Goal Indicator Card (Auto Progressive)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = "Goal",
                    tint = PrimaryCyan,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "বর্তমান টার্গেট গোল: $targetGoalDays দিন"
                            "ar" -> "الهدف الحالي: $targetGoalDays أيام"
                            else -> "Current Target Goal: $targetGoalDays Days"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "স্ট্রিক বজায় রাখলে টার্গেট গোল স্বয়ংক্রিয়ভাবে বৃদ্ধি পাবে"
                            "ar" -> "يزداد الهدف تلقائياً مع استمرارك في العداد"
                            else -> "Target goal automatically increases as your streak grows"
                        },
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stat Cards Grid (2x2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBox(
                title = when (appLanguage) {
                    "bn" -> "সর্বোচ্চ স্ট্রিক"
                    "ar" -> "أطول سلسلة"
                    else -> "Longest Streak"
                },
                value = formatMillisToDaysHours(stats.longestStreakMillis, appLanguage),
                modifier = Modifier.weight(1f)
            )
            StatBox(
                title = when (appLanguage) {
                    "bn" -> "বর্তমান স্ট্রিক"
                    "ar" -> "السلسلة الحالية"
                    else -> "Current Streak"
                },
                value = formatMillisToDaysHours(stats.currentStreakMillis, appLanguage),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBox(
                title = when (appLanguage) {
                    "bn" -> "মোট রিসেট"
                    "ar" -> "إجمالي إعادة الضبط"
                    else -> "Total Resets"
                },
                value = "${stats.totalResets}",
                modifier = Modifier.weight(1f)
            )
            StatBox(
                title = when (appLanguage) {
                    "bn" -> "গড় স্ট্রিক"
                    "ar" -> "متوسط السلسلة"
                    else -> "Average Streak"
                },
                value = formatMillisToDaysHours(stats.averageStreakMillis, appLanguage),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Milestones
        Text(
            text = when (appLanguage) {
                "bn" -> "মাইলেস্টোন ও ব্যাজ"
                "ar" -> "الإنجازات والشارات"
                else -> "Milestones & Badges"
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))

        milestones.forEach { milestone ->
            val isUnlocked = currentDays >= milestone.days
            MilestoneCard(
                milestone = milestone,
                isUnlocked = isUnlocked,
                appLanguage = appLanguage
            )
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
    isUnlocked: Boolean,
    appLanguage: String
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
                        text = milestone.getTitle(appLanguage),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) TextPrimary else TextMuted
                    )
                    Text(
                        text = if (isUnlocked) {
                            when (appLanguage) {
                                "bn" -> "অর্জিত হয়েছে!"
                                "ar" -> "تم الإنجاز!"
                                else -> "Milestone Reached!"
                            }
                        } else {
                            when (appLanguage) {
                                "bn" -> "${milestone.days} দিনের গোল"
                                "ar" -> "هدف ${milestone.days} يوم"
                                else -> "${milestone.days} days goal"
                            }
                        },
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

private fun formatMillisToDaysHours(millis: Long, appLanguage: String): String {
    val days = millis / (1000 * 60 * 60 * 24)
    val hours = (millis / (1000 * 60 * 60)) % 24
    return when (appLanguage) {
        "bn" -> if (days > 0) "${days}দিন ${hours}ঘন্টা" else "${hours}ঘন্টা"
        "ar" -> if (days > 0) "${days} يوم ${hours} ساعة" else "${hours} ساعة"
        else -> if (days > 0) "${days}d ${hours}h" else "${hours}h"
    }
}
