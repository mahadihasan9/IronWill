package org.ironman.ui.screens

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    val days: Int,
    val titleEn: String,
    val titleBn: String,
    val titleAr: String,
    val titleHi: String,
    val titleUr: String,
    val titleEs: String,
    val titleRu: String,
    val titleZh: String,
    val titleJa: String
) {
    fun getTitle(lang: String): String = when (lang) {
        "bn" -> titleBn
        "ar" -> titleAr
        "hi" -> titleHi
        "ur" -> titleUr
        "es" -> titleEs
        "ru" -> titleRu
        "zh" -> titleZh
        "ja" -> titleJa
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

    val timersList by viewModel.timersList.collectAsState()
    val activeTimer by viewModel.activeTimer.collectAsState()

    val currentDays = streakTime.days

    val milestones = remember(appLanguage) {
        listOf(
            Milestone(3, "3 Days Goal", "৩ দিনের গোল", "هدف 3 أيام", "3 दिनों का लक्ष्य", "3 دن کا ہدف", "Meta de 3 Días", "Цель: 3 дня", "3 天目标", "3日間の目標"),
            Milestone(4, "4 Days Goal", "৪ দিনের গোল", "هدف 4 أيام", "4 दिनों का लक्ष्य", "4 دن का हدف", "Meta de 4 Días", "Цель: 4 дня", "4 天目标", "4日間の目標"),
            Milestone(5, "5 Days Goal", "৫ দিনের গোল", "هدف 5 أيام", "5 दिनों का लक्ष्य", "5 دن کا ہدف", "Meta de 5 Días", "Цель: 5 дней", "5 天目标", "5日間の目標"),
            Milestone(7, "1 Week Goal", "১ সপ্তাহের গোল", "هدف أسبوع", "1 सप्ताह का लक्ष्य", "1 ہفتے کا ہدف", "Meta de 1 Semana", "Цель: 1 неделя", "1 周目标", "1週間の目標"),
            Milestone(14, "2 Weeks Goal", "২ সপ্তাহের গোল", "هدف أسبوعين", "2 सप्ताह का लक्ष्य", "2 ہفتے کا ہدف", "Meta de 2 Semanas", "Цель: 2 недели", "2 周目标", "2週間の目標"),
            Milestone(30, "1 Month Goal", "১ মাসের গোল", "هدف شهر", "1 महीने का लक्ष्य", "1 ماہ کا ہدف", "Meta de 1 Mes", "Цель: 1 месяц", "1 个月目标", "1ヶ月の目標"),
            Milestone(60, "60 Days Goal", "৬০ দিনের গোল", "هدف 60 يوم", "60 दिनों का लक्ष्य", "60 دن کا ہدف", "Meta de 60 Días", "Цель: 60 дней", "60 天目标", "60日間の目標"),
            Milestone(90, "90 Days Goal", "৯০ দিনের গোল", "هدف 90 يوم", "90 दिनों का लक्ष्य", "90 دن کا ہدف", "Meta de 90 Días", "Цель: 90 дней", "90 天目标", "90日間の目標"),
            Milestone(180, "180 Days Goal", "১৮০ দিনের গোল", "هدف 180 يوم", "180 दिनों का लक्ष्य", "180 دن کا ہدف", "Meta de 180 Días", "Цель: 180 дней", "180 天目标", "180日間の目標"),
            Milestone(365, "1 Year Goal", "১ বছরের গোল", "هدف سنة", "1 साल का लक्ष्य", "1 साल का हدف", "Meta de 1 Año", "Цель: 1 год", "1 年目标", "1年間の目標")
        )
    }

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
                "hi" -> "सांख्यिकी और उपलब्धियां"
                "ur" -> "شماریات اور کامیابیاں"
                "es" -> "Estadísticas y Logros"
                "ru" -> "Статистика и достижения"
                "zh" -> "统计与成就"
                "ja" -> "統計と実績"
                else -> "Statistics & Achievements"
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (timersList.size > 1) {
            val timersScrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(timersScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                timersList.forEach { timer ->
                    val isSelected = timer.id == activeTimer.id
                    val displayName = if (timer.id == "main") {
                        when (appLanguage) {
                            "bn" -> "মূল কাউন্টার"
                            "ar" -> "العداد الرئيسي"
                            "hi" -> "मुख्य काउंटर"
                            "ur" -> "بنیادی کاؤنٹر"
                            "es" -> "Contador Principal"
                            "ru" -> "Главный счетчик"
                            "zh" -> "主计时器"
                            "ja" -> "メインタイマー"
                            else -> "Main Counter"
                        }
                    } else timer.name

                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectTimer(timer.id) },
                        label = { Text(displayName, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryCyan,
                            selectedLabelColor = DarkSurface,
                            containerColor = DarkSurfaceVariant,
                            labelColor = TextPrimary
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

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
                            "hi" -> "वर्तमान लक्ष्य: $targetGoalDays दिन"
                            "ur" -> "موجودہ ہدف: $targetGoalDays دن"
                            "es" -> "Meta Actual: $targetGoalDays Días"
                            "ru" -> "Текущая цель: $targetGoalDays дн."
                            "zh" -> "当前目标: $targetGoalDays 天"
                            "ja" -> "現在の目標: $targetGoalDays 日"
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
                            "hi" -> "आपकी स्ट्रिक बढ़ने के साथ लक्ष्य अपने आप बढ़ता है"
                            "ur" -> "سلسلہ بڑھنے کے ساتھ ہدف خود بخود بڑھتا ہے"
                            "es" -> "La meta aumenta automáticamente a medida que crece tu racha"
                            "ru" -> "Цель автоматически увеличивается с ростом серии"
                            "zh" -> "目标随着连胜天数增加而自动提升"
                            "ja" -> "ストリークが伸びると目標が自動的に引き上げられます"
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
                    "hi" -> "सबसे लंबी स्ट्रिक"
                    "ur" -> "سب سے لمبا سلسلہ"
                    "es" -> "Racha Más Larga"
                    "ru" -> "Самая длинная серия"
                    "zh" -> "最长连胜"
                    "ja" -> "最长ストリーク"
                    else -> "Longest Streak"
                },
                value = formatMillisToDaysHours(stats.longestStreakMillis, appLanguage),
                modifier = Modifier.weight(1f)
            )
            StatBox(
                title = when (appLanguage) {
                    "bn" -> "বর্তমান স্ট্রিক"
                    "ar" -> "السلسلة الحالية"
                    "hi" -> "वर्तमान स्ट्रिक"
                    "ur" -> "موجودہ سلسلہ"
                    "es" -> "Racha Actual"
                    "ru" -> "Текущая серия"
                    "zh" -> "当前连胜"
                    "ja" -> "現在のストリーク"
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
                    "hi" -> "कुल रीसेट"
                    "ur" -> "کل ری سیٹ"
                    "es" -> "Reinicios Totales"
                    "ru" -> "Всего сбросов"
                    "zh" -> "重置总次数"
                    "ja" -> "通算リセット回数"
                    else -> "Total Resets"
                },
                value = "${stats.totalResets}",
                modifier = Modifier.weight(1f)
            )
            StatBox(
                title = when (appLanguage) {
                    "bn" -> "গড় স্ট্রিক"
                    "ar" -> "متوسط السلسلة"
                    "hi" -> "औसत स्ट्रिक"
                    "ur" -> "اوسط سلسلہ"
                    "es" -> "Racha Promedio"
                    "ru" -> "Средняя серия"
                    "zh" -> "平均连胜"
                    "ja" -> "平均ストリーク"
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
                "hi" -> "मील के पत्थर और बैज"
                "ur" -> "سنگ میل اور بیجز"
                "es" -> "Hitos y Medallas"
                "ru" -> "Значки и достижения"
                "zh" -> "里程碑与勋章"
                "ja" -> "マイルストーンとバッジ"
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
                                "hi" -> "हासिल कर लिया!"
                                "ur" -> "مکمل ہو گیا!"
                                "es" -> "¡Logro Alcanzado!"
                                "ru" -> "Достигнуто!"
                                "zh" -> "目标已达成！"
                                "ja" -> "達成しました！"
                                else -> "Milestone Reached!"
                            }
                        } else {
                            when (appLanguage) {
                                "bn" -> "${milestone.days} দিনের গোল"
                                "ar" -> "هدف ${milestone.days} يوم"
                                "hi" -> "${milestone.days} दिनों का लक्ष्य"
                                "ur" -> "${milestone.days} دن کا ہدف"
                                "es" -> "Meta de ${milestone.days} Días"
                                "ru" -> "Цель: ${milestone.days} дней"
                                "zh" -> "${milestone.days} 天目标"
                                "ja" -> "${milestone.days} 日間の目標"
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
        "bn" -> if (days > 0) "${days} দিন ${hours} ঘন্টা" else "${hours} ঘন্টা"
        "ar" -> if (days > 0) "${days} يوم ${hours} ساعة" else "${hours} ساعة"
        "hi" -> if (days > 0) "${days} दिन ${hours} घंटे" else "${hours} घंटे"
        "ur" -> if (days > 0) "${days} دن ${hours} گھنٹے" else "${hours} گھنٹے"
        "es" -> if (days > 0) "${days}d ${hours}h" else "${hours}h"
        "ru" -> if (days > 0) "${days}д ${hours}ч" else "${hours}ч"
        "zh" -> if (days > 0) "${days}天 ${hours}小时" else "${hours}小时"
        "ja" -> if (days > 0) "${days}日 ${hours}時間" else "${hours}時間"
        else -> if (days > 0) "${days}d ${hours}h" else "${hours}h"
    }
}
