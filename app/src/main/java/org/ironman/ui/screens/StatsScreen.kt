/*
 * Iron Will - Streak & Habit Tracker
 * Copyright (C) 2026 Mahadi Hasan <islammdmahadi943@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */


package org.ironman.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ironman.ui.theme.AccentGold
import org.ironman.ui.theme.AccentGreen
import org.ironman.ui.theme.CardBorder
import org.ironman.ui.theme.DarkSurface
import org.ironman.ui.theme.DarkSurfaceVariant
import org.ironman.ui.theme.PrimaryCyan
import org.ironman.ui.theme.TextMuted
import org.ironman.ui.theme.TextPrimary
import org.ironman.ui.theme.TextSecondary
import org.ironman.ui.viewmodel.MainViewModel

enum class BadgeTier(
    val labelEn: String,
    val labelBn: String,
    val borderColor: Color,
    val gradientColors: List<Color>,
    val iconColor: Color
) {
    BRONZE(
        "BRONZE", "ব্রোঞ্জ",
        Color(0xFFE5A059),
        listOf(Color(0xFF3E2723), Color(0xFF2C1C18)),
        Color(0xFFE5A059)
    ),
    SILVER(
        "SILVER", "সিলভার",
        Color(0xFFE0E0E0),
        listOf(Color(0xFF37474F), Color(0xFF1C242B)),
        Color(0xFFE0E0E0)
    ),
    GOLD(
        "GOLD", "গোল্ড",
        Color(0xFFFFD700),
        listOf(Color(0xFF4A3800), Color(0xFF221900)),
        Color(0xFFFFD700)
    ),
    PLATINUM(
        "PLATINUM", "প্ল্যাটিনাম",
        Color(0xFF00E5FF),
        listOf(Color(0xFF004D40), Color(0xFF002220)),
        Color(0xFF00E5FF)
    ),
    DIAMOND(
        "DIAMOND", "ডায়মন্ড",
        Color(0xFF80DEEA),
        listOf(Color(0xFF003D66), Color(0xFF001E33)),
        Color(0xFF80DEEA)
    ),
    TITANIUM(
        "TITANIUM", "টাইটানিয়াম",
        Color(0xFFFF4081),
        listOf(Color(0xFF4A0033), Color(0xFF24001A)),
        Color(0xFFFFD700)
    );

    fun getLabel(lang: String): String = if (lang == "bn") labelBn else labelEn
}

data class BadgeItem(
    val id: String,
    val days: Int,
    val tier: BadgeTier,
    val icon: ImageVector,
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

    val badgeItems = remember {
        listOf(
            BadgeItem("b3", 3, BadgeTier.BRONZE, Icons.Default.MilitaryTech, "Bronze Initiate (3 Days)", "ব্রোঞ্জ প্রারম্ভিক (৩ দিন)", "مبتدئ برونزي (3 أيام)", "कांस्य आरंभकर्ता (3 दिन)", "کانسی شروعات (3 دن)", "Iniciado de Bronce (3 Días)", "Бронзовый старт (3 дня)", "青铜启航 (3 天)", "ブロンズ・イニシエイト (3日)"),
            BadgeItem("b5", 5, BadgeTier.BRONZE, Icons.Default.WorkspacePremium, "Bronze Willpower (5 Days)", "ব্রোঞ্জ ইচ্ছাশক্তি (৫ দিন)", "إرادة برونزية (5 أيام)", "कांस्य इच्छाशक्ति (5 दिन)", "کانسی قوت ارادی (5 دن)", "Voluntad de Bronce (5 Días)", "Бронзовая воля (5 дней)", "青铜意志 (5 天)", "ブロンズ・ウィル (5日)"),
            BadgeItem("b7", 7, BadgeTier.SILVER, Icons.Default.Shield, "Silver Vanguard (1 Week)", "সিলভার অগ্রদূত (১ সপ্তাহ)", "طليعة فضية (أسبوع)", "रजत अग्रदूत (1 सप्ताह)", "سلور ہر اول دستہ (1 ہفتہ)", "Vanguardia de Plata (1 Semana)", "Серебряный авангард (1 неделя)", "白银先锋 (1 周)", "シルバー・ヴァンガード (1週間)"),
            BadgeItem("b14", 14, BadgeTier.SILVER, Icons.Default.Stars, "Silver Warrior (2 Weeks)", "সিলভার যোদ্ধা (২ সপ্তাহ)", "محارب فضي (أسبوعين)", "रजत योद्धा (2 सप्ताह)", "سلور مجاہد (2 ہفتے)", "Guerrero de Plata (2 Semanas)", "Серебряный воин (2 недели)", "白银战士 (2 周)", "シルバー・ウォリアー (2週間)"),
            BadgeItem("b30", 30, BadgeTier.GOLD, Icons.Default.EmojiEvents, "Gold Conqueror (1 Month)", "গোল্ড বিজয়ী (১ মাস)", "فاتح ذهبي (شهر)", "स्वर्ण विजेता (1 महीना)", "گولڈ فاتح (1 ماہ)", "Conquistador de Oro (1 Mes)", "Золотой победитель (1 месяц)", "黄金征服者 (1 个月)", "ゴールド・コンカラー (1ヶ月)"),
            BadgeItem("b60", 60, BadgeTier.GOLD, Icons.Default.WorkspacePremium, "Gold Guardian (2 Months)", "গোল্ড অভিভাবক (২ মাস)", "حارس ذهبي (شهران)", "स्वर्ण संरक्षक (2 महीने)", "گولڈ نگہبان (2 ماہ)", "Guardián de Oro (2 Meses)", "Золотой страж (2 месяца)", "黄金守护者 (2 个月)", "ゴールド・ガーディアン (2ヶ月)"),
            BadgeItem("b90", 90, BadgeTier.PLATINUM, Icons.Default.MilitaryTech, "Platinum Titan (90 Days)", "প্ল্যাটিনাম টাইটান (৯০ দিন)", "تيتان بلاتيني (90 يوماً)", "प्लेटिनम टाइटन (90 दिन)", "پلیٹینم ٹائٹن (90 دن)", "Titán de Platino (90 Días)", "Платиновый титан (90 дней)", "白金泰坦 (90 天)", "プラチナ・タイタン (90日)"),
            BadgeItem("b180", 180, BadgeTier.DIAMOND, Icons.Default.Stars, "Diamond Master (6 Months)", "ডায়মন্ড মাস্টার (৬ মাস)", "سيد ألماسي (6 أشهر)", "डायमंड मास्टर (6 महीने)", "ڈائمنڈ ماسٹر (6 ماہ)", "Maestro de Diamante (6 Meses)", "Алмазный мастер (6 месяцев)", "钻石大师 (6 个月)", "ダイヤモンド・マスター (6ヶ月)"),
            BadgeItem("b365", 365, BadgeTier.TITANIUM, Icons.Default.EmojiEvents, "Titanium Legend (1 Year)", "টাইটানিয়াম লেজেন্ড (১ বছর)", "أسطورة التيتانيوم (سنة)", "टाइटेनियम लीजेंड (1 वर्ष)", "ٹائٹینیم لیجنڈ (1 سال)", "Leyenda de Titanio (1 Año)", "Титановая легенда (1 год)", "钛金传奇 (1 年)", "チタニウム・レジェンド (1年)")
        )
    }

    val unlockedCount = badgeItems.count { currentDays >= it.days }
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

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(listOf(PrimaryCyan.copy(alpha = 0.5f), Color.Transparent)),
                    shape = RoundedCornerShape(12.dp)
                ),
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
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBox(
                icon = Icons.Default.EmojiEvents,
                iconTint = AccentGold,
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
                icon = Icons.Default.Timer,
                iconTint = PrimaryCyan,
                title = when (appLanguage) {
                    "bn" -> "বর্তমান স্ট্রিক"
                    "ar" -> "السلسلة الحالية"
                    "hi" -> "वर्तमान स्ट्रिक"
                    "ur" -> "موجودہ سلسلہ"
                    "es" -> "Racha Actual"
                    "ru" -> "Текущая серия"
                    "zh" -> "当前连胜"
                    "ja" -> "平均ストリーク"
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
                icon = Icons.Default.Autorenew,
                iconTint = TextMuted,
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
                icon = Icons.Default.BarChart,
                iconTint = AccentGreen,
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (appLanguage) {
                    "bn" -> "মাইলেস্টোন ও অর্জিত ব্যাজসমূহ"
                    "ar" -> "الإنجازات والشارات"
                    "hi" -> "मील के पत्थर और बैज"
                    "ur" -> "سنگ میل اور بیجز"
                    "es" -> "Hitos y Medallas"
                    "ru" -> "Значки и достижения"
                    "zh" -> "里程碑与勋章"
                    "ja" -> "マイルストーンとバッジ"
                    else -> "Milestones & Badges"
                },
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "$unlockedCount / ${badgeItems.size}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryCyan
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        badgeItems.forEach { badge ->
            val isUnlocked = currentDays >= badge.days
            BadgeCard(
                badge = badge,
                currentDays = currentDays,
                isUnlocked = isUnlocked,
                appLanguage = appLanguage
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun StatBox(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(
            width = 1.dp,
            color = CardBorder,
            shape = RoundedCornerShape(12.dp)
        ),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
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
fun BadgeCard(
    badge: BadgeItem,
    currentDays: Long,
    isUnlocked: Boolean,
    appLanguage: String
) {
    val progress = if (isUnlocked) 1f else (currentDays.toFloat() / badge.days.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800),
        label = "badgeProgress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isUnlocked) 1.5.dp else 1.dp,
                color = if (isUnlocked) badge.tier.borderColor else CardBorder,
                shape = RoundedCornerShape(14.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) DarkSurfaceVariant else DarkSurface
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (isUnlocked) 2.dp else 1.dp,
                                color = if (isUnlocked) badge.tier.borderColor else TextMuted.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                            .background(
                                brush = if (isUnlocked) {
                                    Brush.verticalGradient(badge.tier.gradientColors)
                                } else {
                                    Brush.verticalGradient(listOf(DarkSurface, DarkSurfaceVariant))
                                }
                            )
                    ) {
                        Icon(
                            imageVector = badge.icon,
                            contentDescription = badge.getTitle(appLanguage),
                            tint = if (isUnlocked) badge.tier.iconColor else TextMuted.copy(alpha = 0.4f),
                            modifier = Modifier.size(28.dp)
                        )
                        if (!isUnlocked) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(DarkSurface.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = badge.getTitle(appLanguage),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) TextPrimary else TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badge.tier.borderColor.copy(alpha = if (isUnlocked) 0.25f else 0.1f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge.tier.getLabel(appLanguage),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isUnlocked) badge.tier.borderColor else TextMuted
                            )
                        }
                    }
                }

                if (isUnlocked) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AccentGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Unlocked",
                            tint = AccentGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (appLanguage) {
                                "bn" -> "আনলকড"
                                "ar" -> "مفتوح"
                                "hi" -> "अनलॉक्ड"
                                "ur" -> "انلاک"
                                "es" -> "Conseguido"
                                "ru" -> "Открыто"
                                "zh" -> "已解锁"
                                "ja" -> "解除済み"
                                else -> "Unlocked"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen
                        )
                    }
                } else {
                    Text(
                        text = "$currentDays / ${badge.days} ${
                            when (appLanguage) {
                                "bn" -> "দিন"
                                "ar" -> "يوم"
                                "hi" -> "दिन"
                                "ur" -> "دن"
                                "es" -> "días"
                                "ru" -> "дн."
                                "zh" -> "天"
                                "ja" -> "日"
                                else -> "d"
                            }
                        }",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted
                    )
                }
            }

            if (!isUnlocked) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = badge.tier.borderColor.copy(alpha = 0.7f),
                    trackColor = CardBorder.copy(alpha = 0.4f)
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
