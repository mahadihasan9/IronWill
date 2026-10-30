package org.ironman.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ironman.ui.components.PinVerificationDialog
import org.ironman.ui.components.RelapseDialog
import org.ironman.ui.components.StartDateTimeDialog
import org.ironman.ui.theme.AccentGold
import org.ironman.ui.theme.AccentRed
import org.ironman.ui.theme.CardBorder
import org.ironman.ui.theme.DarkSurface
import org.ironman.ui.theme.DarkSurfaceVariant
import org.ironman.ui.theme.PrimaryCyan
import org.ironman.ui.theme.TextMuted
import org.ironman.ui.theme.TextPrimary
import org.ironman.ui.theme.TextSecondary
import org.ironman.ui.viewmodel.MainViewModel
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val streakTime by viewModel.currentStreakTime.collectAsState()
    val isTracking by viewModel.isTracking.collectAsState()
    val quote by viewModel.currentQuote.collectAsState()
    val targetGoalDays by viewModel.targetGoalDays.collectAsState()
    val pinEnabled by viewModel.pinEnabled.collectAsState()
    val pinCode by viewModel.pinCode.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()

    val rankInfo = remember(streakTime.days) { viewModel.getRankInfo(streakTime.days) }

    var showPinDialog by remember { mutableStateOf(false) }
    var showRelapseDialog by remember { mutableStateOf(false) }
    var showStartDateTimeDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Rank Banner Card with Gradient Border
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(listOf(PrimaryCyan.copy(alpha = 0.4f), Color.Transparent)),
                    shape = RoundedCornerShape(16.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = "Rank",
                    tint = AccentGold,
                    modifier = Modifier.size(38.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rankInfo.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "পরবর্তী র্যাঙ্ক: ${rankInfo.nextRankTitle} (${rankInfo.daysToNextRank} দিন বাকি)"
                            "ar" -> "الرتبة التالية: ${rankInfo.nextRankTitle} (${rankInfo.daysToNextRank} أيام متبقية)"
                            "hi" -> "अगली रैंक: ${rankInfo.nextRankTitle} (${rankInfo.daysToNextRank} दिन शेष)"
                            "ur" -> "اگلی رینک: ${rankInfo.nextRankTitle} (${rankInfo.daysToNextRank} دن باقی)"
                            "es" -> "Siguiente Rango: ${rankInfo.nextRankTitle} (faltan ${rankInfo.daysToNextRank}d)"
                            "ru" -> "След. ранг: ${rankInfo.nextRankTitle} (осталось ${rankInfo.daysToNextRank}д)"
                            "zh" -> "下一等级: ${rankInfo.nextRankTitle} (还剩 ${rankInfo.daysToNextRank} 天)"
                            "ja" -> "次のランク: ${rankInfo.nextRankTitle} (あと ${rankInfo.daysToNextRank} 日)"
                            else -> "Next Rank: ${rankInfo.nextRankTitle} (${rankInfo.daysToNextRank}d left)"
                        },
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main Circular Timer Card with Shield Emblem Watermark Background
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(260.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = PrimaryCyan.copy(alpha = 0.05f),
                modifier = Modifier
                    .size(180.dp)
                    .alpha(0.6f)
            )

            val progress = if (isTracking) ((streakTime.days.toFloat() / targetGoalDays.toFloat())).coerceIn(0f, 1f) else 0f
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(1000),
                label = "progress"
            )

            Canvas(modifier = Modifier.size(240.dp)) {
                val strokeWidth = 14.dp.toPx()
                drawCircle(
                    color = CardBorder,
                    style = Stroke(width = strokeWidth)
                )
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(PrimaryCyan, AccentGold, PrimaryCyan)
                    ),
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${streakTime.days}",
                    fontSize = 52.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryCyan
                )
                Text(
                    text = when (appLanguage) {
                        "bn" -> "দিন"
                        "ar" -> "أيام"
                        "hi" -> "दिन"
                        "ur" -> "دن"
                        "es" -> "DÍAS"
                        "ru" -> "ДНЕЙ"
                        "zh" -> "天"
                        "ja" -> "日"
                        else -> if (streakTime.days == 1L) "DAY" else "DAYS"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "%02d : %02d : %02d",
                        streakTime.hours,
                        streakTime.minutes,
                        streakTime.seconds
                    ),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Goal Progress Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "টার্গেট গোল: $targetGoalDays দিন"
                            "ar" -> "الهدف: $targetGoalDays أيام"
                            "hi" -> "लक्ष्य: $targetGoalDays दिन"
                            "ur" -> "ہدف: $targetGoalDays دن"
                            "es" -> "Meta: $targetGoalDays Días"
                            "ru" -> "Цель: $targetGoalDays дн."
                            "zh" -> "目标: $targetGoalDays 天"
                            "ja" -> "目標: $targetGoalDays 日"
                            else -> "Target Goal: $targetGoalDays Days"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${((streakTime.days.toFloat() / targetGoalDays) * 100).toInt().coerceIn(0, 100)}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { if (isTracking) (streakTime.days.toFloat() / targetGoalDays.toFloat()).coerceIn(0f, 1f) else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = PrimaryCyan,
                    trackColor = CardBorder
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons: Start Counter (if not tracking) OR Reset/Relapse (if tracking)
        if (!isTracking) {
            Button(
                onClick = { showStartDateTimeDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    tint = DarkSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (appLanguage) {
                        "bn" -> "স্টার্ট (কাউন্টার শুরু করুন)"
                        "ar" -> "ابدأ العداد"
                        "hi" -> "काउंटर शुरू करें"
                        "ur" -> "کاؤنٹر شروع کریں"
                        "es" -> "Iniciar Contador"
                        "ru" -> "Запустить счетчик"
                        "zh" -> "开始计时"
                        "ja" -> "カウンターを開始"
                        else -> "Start Counter"
                    },
                    color = DarkSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        } else {
            Button(
                onClick = {
                    if (pinEnabled) {
                        showPinDialog = true
                    } else {
                        showRelapseDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = "Reset"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (appLanguage) {
                        "bn" -> "রিসেট / রিল্যাপ্স"
                        "ar" -> "إعادة ضبط"
                        "hi" -> "रीसेट करें"
                        "ur" -> "ری سیٹ کریں"
                        "es" -> "Reiniciar / Reset"
                        "ru" -> "Сброс"
                        "zh" -> "重置"
                        "ja" -> "リセット"
                        else -> "Relapse / Reset"
                    },
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Motivational Quote Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = CardBorder,
                    shape = RoundedCornerShape(16.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "Quote",
                            tint = AccentGold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (appLanguage) {
                                "bn" -> "দৈনিক অনুপ্রেরণা"
                                "ar" -> "حكمة اليوم"
                                "hi" -> "दैनिक प्रेरणा"
                                "ur" -> "روزانہ کی ترغیب"
                                "es" -> "Inspiración Diaria"
                                "ru" -> "Вдохновение дня"
                                "zh" -> "每日感悟"
                                "ja" -> "今日の言葉"
                                else -> "Daily Inspiration"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    }
                    IconButton(onClick = { viewModel.refreshQuote() }) {
                        Icon(
                            imageVector = Icons.Default.Autorenew,
                            contentDescription = "Refresh",
                            tint = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"${quote.getText(appLanguage)}\"",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "— ${quote.author}",
                    fontSize = 12.sp,
                    color = AccentGold,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }
    }

    if (showPinDialog) {
        PinVerificationDialog(
            correctPin = pinCode,
            onDismiss = { showPinDialog = false },
            onPinSuccess = {
                showPinDialog = false
                showRelapseDialog = true
            }
        )
    }

    if (showRelapseDialog) {
        RelapseDialog(
            onDismiss = { showRelapseDialog = false },
            onConfirm = { note ->
                viewModel.resetStreak(note)
                showRelapseDialog = false
            }
        )
    }

    if (showStartDateTimeDialog) {
        StartDateTimeDialog(
            initialTimestamp = System.currentTimeMillis(),
            appLanguage = appLanguage,
            onDismiss = { showStartDateTimeDialog = false },
            onConfirm = { newTimestamp ->
                viewModel.startStreak(newTimestamp)
                showStartDateTimeDialog = false
            }
        )
    }
}
