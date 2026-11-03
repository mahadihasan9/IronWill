package org.ironman.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import org.ironman.ui.components.CreatePinDialog
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
    val pinCode by viewModel.pinCode.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()

    val timersList by viewModel.timersList.collectAsState()
    val activeTimer by viewModel.activeTimer.collectAsState()

    val rankInfo = remember(streakTime.days, appLanguage) { viewModel.getRankInfo(streakTime.days) }

    // Quote refreshes on navigation or manual tap (no auto-loop)
    var quoteRefreshTrigger by remember { mutableIntStateOf(0) }
    LaunchedEffect(quoteRefreshTrigger) {
        viewModel.refreshQuote()
    }

    var showPinDialog by remember { mutableStateOf(false) }
    var showCreatePinDialog by remember { mutableStateOf(false) }
    var showRelapseDialog by remember { mutableStateOf(false) }
    var showStartDateTimeDialog by remember { mutableStateOf(false) }

    var showAddTimerDialog by remember { mutableStateOf(false) }
    var timerToDeleteId by remember { mutableStateOf<String?>(null) }

    // Track whether the pin creation / verification is for starting counter or resetting counter
    var isPinActionForStart by remember { mutableStateOf(false) }

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
                onClick = {
                    isPinActionForStart = true
                    if (pinCode.isBlank()) {
                        showCreatePinDialog = true
                    } else {
                        showPinDialog = true
                    }
                },
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
                        "bn" -> "কাউন্টার শুরু করুন (স্টার্ট)"
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
                    isPinActionForStart = false
                    if (pinCode.isBlank()) {
                        showCreatePinDialog = true
                    } else {
                        showPinDialog = true
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
                        "bn" -> "রিসেট করুন (রিল্যাপ্স)"
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
                                "bn" -> "উপদেশ"
                                "ar" -> "نصيحة وحكمة"
                                "hi" -> "उपदेश व विचार"
                                "ur" -> "نصیحت"
                                "es" -> "Consejo y Sabiduría"
                                "ru" -> "Совет и мудрость"
                                "zh" -> "箴言"
                                "ja" -> "助言"
                                else -> "Advice & Wisdom"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    }
                    IconButton(onClick = {
                        quoteRefreshTrigger++
                        viewModel.refreshQuote()
                    }) {
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

        Spacer(modifier = Modifier.height(20.dp))

        // Multi-Timer / Multi-Counter Management Card (Placed Below Advice Card)
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
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Multi Counters",
                            tint = PrimaryCyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (appLanguage) {
                                "bn" -> "টাইমার ও কাউন্টার সমূহ (${timersList.size}/30)"
                                "ar" -> "العدادات المتعددة (${timersList.size}/30)"
                                "hi" -> "टाइमर और मल्टी-काउंटर (${timersList.size}/30)"
                                "ur" -> "ٹائمرز اور ملٹی کاؤنٹرز (${timersList.size}/30)"
                                "es" -> "Temporizadores Múltiples (${timersList.size}/30)"
                                "ru" -> "Мульти-счетчики (${timersList.size}/30)"
                                "zh" -> "多计时器 (${timersList.size}/30)"
                                "ja" -> "マルチタイマー (${timersList.size}/30)"
                                else -> "Timers & Multi-Counters (${timersList.size}/30)"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    if (timersList.size < 30) {
                        IconButton(onClick = { showAddTimerDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Timer",
                                tint = PrimaryCyan
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Timer Cards
                val timersScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(timersScrollState),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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

                        Card(
                            modifier = Modifier
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) PrimaryCyan else CardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.selectTimer(timer.id) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) PrimaryCyan.copy(alpha = 0.15f) else DarkSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = displayName,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) PrimaryCyan else TextPrimary
                                    )
                                    Text(
                                        text = if (timer.isTracking) {
                                            val days = ((System.currentTimeMillis() - timer.startTimestamp).coerceAtLeast(0)) / (1000 * 60 * 60 * 24)
                                            when (appLanguage) {
                                                "bn" -> "$days দিন চলছে"
                                                "ar" -> "$days أيام"
                                                "hi" -> "$days दिन चालू"
                                                "ur" -> "$days دن جاری"
                                                "es" -> "$days días activos"
                                                "ru" -> "$days дн. активно"
                                                "zh" -> "$days 天进行中"
                                                "ja" -> "$days 日経過"
                                                else -> "$days days active"
                                            }
                                        } else {
                                            when (appLanguage) {
                                                "bn" -> "বন্ধ আছে"
                                                "ar" -> "متوقف"
                                                "hi" -> "बंद है"
                                                "ur" -> "روکا ہوا"
                                                "es" -> "Inactivo"
                                                "ru" -> "Неактивен"
                                                "zh" -> "未开启"
                                                "ja" -> "停止中"
                                                else -> "Inactive"
                                            }
                                        },
                                        fontSize = 11.sp,
                                        color = if (timer.isTracking) AccentGold else TextMuted
                                    )
                                }

                                if (timer.id != "main") {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { timerToDeleteId = timer.id },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = AccentRed.copy(alpha = 0.8f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (timersList.size < 30) {
                        OutlinedButton(
                            onClick = { showAddTimerDialog = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (appLanguage) {
                                    "bn" -> "+ নতুন কাউন্টার"
                                    "ar" -> "+ عداد جديد"
                                    "hi" -> "+ नया काउंटर"
                                    "ur" -> "+ نیا کاؤنٹر"
                                    "es" -> "+ Nuevo Contador"
                                    "ru" -> "+ Новый счетчик"
                                    "zh" -> "+ 新计时器"
                                    "ja" -> "+ 新規タイマー"
                                    else -> "+ Add Counter"
                                },
                                color = PrimaryCyan,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreatePinDialog) {
        CreatePinDialog(
            appLanguage = appLanguage,
            onDismiss = { showCreatePinDialog = false },
            onPinCreated = { newPin ->
                viewModel.setPinCode(newPin)
                showCreatePinDialog = false
                if (isPinActionForStart) {
                    showStartDateTimeDialog = true
                } else {
                    showRelapseDialog = true
                }
            }
        )
    }

    if (showPinDialog) {
        PinVerificationDialog(
            correctPin = pinCode,
            appLanguage = appLanguage,
            onDismiss = { showPinDialog = false },
            onPinSuccess = {
                showPinDialog = false
                if (isPinActionForStart) {
                    showStartDateTimeDialog = true
                } else {
                    showRelapseDialog = true
                }
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

    if (showAddTimerDialog) {
        var timerNameInput by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddTimerDialog = false },
            title = {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "নতুন কাউন্টার তৈরি করুন"
                        "ar" -> "إنشاء عداد جديد"
                        "hi" -> "नया काउंटर बनाएं"
                        "ur" -> "نیا کاؤنٹر بنائیں"
                        "es" -> "Crear Nuevo Contador"
                        "ru" -> "Создать новый счетчик"
                        "zh" -> "创建新计时器"
                        "ja" -> "新しいタイマーを作成"
                        else -> "Create New Counter"
                    },
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "কাউন্টারের নাম লিখুন (সর্বোচ্চ ৫০ অক্ষর):"
                            "ar" -> "أدخل اسم العداد (الحد الأقصى 50 حرفًا):"
                            "hi" -> "काउंटर का नाम दर्ज करें (अधिकतम 50 अक्षर):"
                            "ur" -> "کاؤنٹر کا نام درج کریں (زیادہ سے زیادہ 50 حروف):"
                            "es" -> "Introduce el nombre del contador (máx 50 caracteres):"
                            "ru" -> "Введите название счетчика (макс. 50 символов):"
                            "zh" -> "输入计时器名称（最多 50 个字符）:"
                            "ja" -> "タイマー名を入力（最大50文字）:"
                            else -> "Enter counter name (max 50 characters):"
                        },
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = timerNameInput,
                        onValueChange = {
                            if (it.length <= 50) {
                                timerNameInput = it
                                errorMessage = ""
                            }
                        },
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = when (appLanguage) {
                                    "bn" -> "যেমন: নো স্মোকিং / লেখাপড়া"
                                    "ar" -> "مثال: ممارسة الرياضة"
                                    "hi" -> "जैसे: पढ़ाई / वर्कआउट"
                                    "ur" -> "مثال: واک / پڑھائی"
                                    "es" -> "Ej: Sin Fumar / Estudio"
                                    "ru" -> "Например: Без курения / Учеба"
                                    "zh" -> "例如：戒烟 / 读书"
                                    "ja" -> "例：禁煙 / 勉強"
                                    else -> "e.g. Study / Workout"
                                },
                                color = TextMuted.copy(alpha = 0.5f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(errorMessage, color = AccentRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = timerNameInput.trim()
                        if (trimmed.isBlank()) {
                            errorMessage = when (appLanguage) {
                                "bn" -> "কাউন্টারের নাম খালি রাখা যাবে না"
                                "ar" -> "لا يمكن أن يكون اسم العداد فارغاً"
                                "hi" -> "काउंटर का नाम खाली नहीं हो सकता"
                                "ur" -> "کاؤنٹر کا نام خالی نہیں ہو سکتا"
                                "es" -> "El nombre del contador no puede estar vacío"
                                "ru" -> "Название счетчика не может быть пустым"
                                "zh" -> "计时器名称不能为空"
                                "ja" -> "タイマー名を空にすることはできません"
                                else -> "Counter name cannot be empty"
                            }
                        } else {
                            viewModel.addNewTimer(trimmed)
                            showAddTimerDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                ) {
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "তৈরি করুন"
                            "ar" -> "إنشاء"
                            "hi" -> "बनाएं"
                            "ur" -> "بنائیں"
                            "es" -> "Crear"
                            "ru" -> "Создать"
                            "zh" -> "创建"
                            "ja" -> "作成"
                            else -> "Create"
                        },
                        color = DarkSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTimerDialog = false }) {
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "বাতিল"
                            "ar" -> "إلغاء"
                            "hi" -> "রद्द करें"
                            "ur" -> "منسوخ کریں"
                            "es" -> "Cancelar"
                            "ru" -> "Отмена"
                            "zh" -> "取消"
                            "ja" -> "キャンセル"
                            else -> "Cancel"
                        }
                    )
                }
            }
        )
    }

    if (timerToDeleteId != null) {
        val targetId = timerToDeleteId!!
        val timerName = timersList.find { it.id == targetId }?.name ?: ""

        AlertDialog(
            onDismissRequest = { timerToDeleteId = null },
            title = {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "কাউন্টার ডিলিট করুন"
                        "ar" -> "حذف العداد"
                        "hi" -> "काउंटर हटाएं"
                        "ur" -> "کاؤنٹر حذف کریں"
                        "es" -> "Eliminar Contador"
                        "ru" -> "Удалить счетчик"
                        "zh" -> "删除计时器"
                        "ja" -> "タイマーを削除"
                        else -> "Delete Counter"
                    },
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "আপনি কি নিশ্চিত যে '$timerName' কাউন্টারটি এবং এর সমস্ত তথ্য ডিলিট করতে চান?"
                        "ar" -> "هل أنت تأكد من أنك تريد حذف العداد '$timerName' وكافة بياناته؟"
                        "hi" -> "क्या आप सुनिश्चित हैं कि आप '$timerName' काउंटर और इसके सभी डेटा को हटाना चाहते हैं?"
                        "ur" -> "کیا آپ واقعی '$timerName' کاؤنٹر اور اس کا تمام ڈیٹا حذف کرنا چاہتے ہیں؟"
                        "es" -> "¿Estás seguro de que deseas eliminar el contador '$timerName' y todos sus datos?"
                        "ru" -> "Вы уверены, что хотите удалить счетчик '$timerName' и все его данные?"
                        "zh" -> "您确定要删除计时器“$timerName”及其所有数据吗？"
                        "ja" -> "タイマー「$timerName」とそのすべてのデータを削除してもよろしいですか？"
                        else -> "Are you sure you want to delete '$timerName' and all its data?"
                    },
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTimer(targetId)
                        timerToDeleteId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "ডিলিট"
                            "ar" -> "حذف"
                            "hi" -> "हटाएं"
                            "ur" -> "حذف"
                            "es" -> "Eliminar"
                            "ru" -> "Удалить"
                            "zh" -> "删除"
                            "ja" -> "削除"
                            else -> "Delete"
                        },
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { timerToDeleteId = null }) {
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "বাতিল"
                            "ar" -> "إلغاء"
                            "hi" -> "রद्द करें"
                            "ur" -> "منسوخ کریں"
                            "es" -> "Cancelar"
                            "ru" -> "Отмена"
                            "zh" -> "取消"
                            "ja" -> "キャンセル"
                            else -> "Cancel"
                        }
                    )
                }
            }
        )
    }
}
