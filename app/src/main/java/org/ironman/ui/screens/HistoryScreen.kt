package org.ironman.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ironman.data.StreakHistory
import org.ironman.ui.components.CreatePinDialog
import org.ironman.ui.components.PinVerificationDialog
import org.ironman.ui.theme.AccentGold
import org.ironman.ui.theme.AccentRed
import org.ironman.ui.theme.DarkSurface
import org.ironman.ui.theme.DarkSurfaceVariant
import org.ironman.ui.theme.PrimaryCyan
import org.ironman.ui.theme.TextMuted
import org.ironman.ui.theme.TextPrimary
import org.ironman.ui.theme.TextSecondary
import org.ironman.ui.viewmodel.MainViewModel

@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val historyList by viewModel.historyList.collectAsState()
    val pinCode by viewModel.pinCode.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()

    val timersList by viewModel.timersList.collectAsState()
    val activeTimer by viewModel.activeTimer.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var showPinDialog by remember { mutableStateOf(false) }
    var showCreatePinDialog by remember { mutableStateOf(false) }

    val requirePinThenExecute = { action: () -> Unit ->
        pendingAction = action
        if (pinCode.isBlank()) {
            showCreatePinDialog = true
        } else {
            showPinDialog = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (appLanguage) {
                    "bn" -> "স্ট্রিক হিস্ট্রি"
                    "ar" -> "سجل العداد"
                    "hi" -> "स्ट्रिक इतिहास"
                    "ur" -> "سٹریک تاریخ"
                    "es" -> "Historial de Rachas"
                    "ru" -> "История серий"
                    "zh" -> "连胜历史"
                    "ja" -> "ストリーク履歴"
                    else -> "Streak History"
                },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            if (historyList.isNotEmpty()) {
                OutlinedButton(
                    onClick = {
                        requirePinThenExecute { showClearDialog = true }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "সব মুছুন"
                            "ar" -> "مسح الكل"
                            "hi" -> "सभी हटाएं"
                            "ur" -> "تمام صاف کریں"
                            "es" -> "Borrar Todo"
                            "ru" -> "Очистить всё"
                            "zh" -> "清空全部"
                            "ja" -> "すべて消去"
                            else -> "Clear All"
                        },
                        color = AccentRed
                    )
                }
            }
        }

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

        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "No History",
                        tint = TextMuted,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "কোনো হিস্ট্রি পাওয়া যায়নি"
                            "ar" -> "لا يوجد سجل حتى الآن"
                            "hi" -> "अभी तक कोई इतिहास नहीं है"
                            "ur" -> "ابھی تک کوئی تاریخ نہیں ہے"
                            "es" -> "Aún no hay historial registrado"
                            "ru" -> "История пока пуста"
                            "zh" -> "暂无历史记录"
                            "ja" -> "履歴はまだありません"
                            else -> "No history recorded yet"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "রিসেট করলে আপনার পূর্বের রেকর্ডগুলো এখানে দেখা যাবে।"
                            "ar" -> "ستظهر سجلاتك السابقة هنا عند إعادة الضبط"
                            "hi" -> "रीसेट करने पर आपके पिछले रिकॉर्ड यहां दिखाई देंगे।"
                            "ur" -> "ری سیٹ کرنے پر آپ کا ریکارڈ یہاں نظر آئے گا۔"
                            "es" -> "Tus registros anteriores aparecerán aquí al reiniciar."
                            "ru" -> "Ваши записи появятся здесь после сброса."
                            "zh" -> "重置后您的历史记录将显示在此处。"
                            "ja" -> "リセットすると以前の記録がここに表示されます。"
                            else -> "Your previous streak records will appear here when you reset."
                        },
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historyList, key = { it.id }) { item ->
                    HistoryCard(
                        item = item,
                        appLanguage = appLanguage,
                        onDelete = {
                            requirePinThenExecute { viewModel.deleteHistoryItem(item.id) }
                        }
                    )
                }
            }
        }
    }

    if (showCreatePinDialog) {
        CreatePinDialog(
            appLanguage = appLanguage,
            onDismiss = {
                showCreatePinDialog = false
                pendingAction = null
            },
            onPinCreated = { newPin ->
                viewModel.setPinCode(newPin)
                showCreatePinDialog = false
                pendingAction?.invoke()
                pendingAction = null
            }
        )
    }

    if (showPinDialog) {
        PinVerificationDialog(
            correctPin = pinCode,
            appLanguage = appLanguage,
            subtitle = when (appLanguage) {
                "bn" -> "মুছে ফেলার কাজ সম্পন্ন করতে পিন দিন:"
                "ar" -> "أدخل الرمز لإكمال عملية الحذف:"
                "hi" -> "इस विलोपन को पूरा करने के लिए पिन दर्ज करें:"
                "ur" -> "یہ حذف کا عمل مکمل کرنے کے لیے پن درج کریں:"
                "es" -> "Introduce el PIN para realizar esta eliminación:"
                "ru" -> "Введите PIN для выполнения удаления:"
                "zh" -> "输入 PIN 码以执行此删除操作:"
                "ja" -> "削除を実行するにはPINを入力してください:"
                else -> "Enter PIN to perform this deletion:"
            },
            onDismiss = {
                showPinDialog = false
                pendingAction = null
            },
            onPinSuccess = {
                showPinDialog = false
                pendingAction?.invoke()
                pendingAction = null
            }
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "সমস্ত হিস্ট্রি মুছবেন?"
                        "ar" -> "مسح كافة السجلات؟"
                        "hi" -> "क्या सभी इतिहास हटाएं?"
                        "ur" -> "تمام ہسٹری صاف کریں؟"
                        "es" -> "¿Borrar todo el historial?"
                        "ru" -> "Очистить всю историю?"
                        "zh" -> "清空所有历史记录？"
                        "ja" -> "すべての履歴を消去しますか？"
                        else -> "Clear All History?"
                    },
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "এই কাজটি পুনরায় ফিরিয়ে আনা যাবে না। আপনি কি নিশ্চিত?"
                        "ar" -> "لا يمكن التراجع عن هذا الإجراء. هل أنت تأكد؟"
                        "hi" -> "यह क्रिया पूर्ववत नहीं की जा सकती। क्या आप सुनिश्चित हैं?"
                        "ur" -> "اس عمل کو واپس نہیں لایا جا سکتا۔ کیا آپ کو یقین ہے؟"
                        "es" -> "Esta acción no se puede deshacer. ¿Estás seguro?"
                        "ru" -> "Это действие нельзя отменить. Вы уверены?"
                        "zh" -> "此操作无法撤销。您确定吗？"
                        "ja" -> "この操作は取り消せません。本当によろしいですか？"
                        else -> "This action cannot be undone. Are you sure?"
                    },
                    color = TextMuted
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearHistory()
                    showClearDialog = false
                }) {
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "মুছুন"
                            "ar" -> "مسح"
                            "hi" -> "हटाएं"
                            "ur" -> "صاف کریں"
                            "es" -> "Borrar"
                            "ru" -> "Очистить"
                            "zh" -> "清空"
                            "ja" -> "消去"
                            else -> "Clear"
                        },
                        color = AccentRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "বাতিল"
                            "ar" -> "إلغاء"
                            "hi" -> "रद्द करें"
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

@Composable
fun HistoryCard(
    item: StreakHistory,
    appLanguage: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "মেয়াদ: ${item.getFormattedDuration()}"
                        "ar" -> "المدة: ${item.getFormattedDuration()}"
                        "hi" -> "अवधि: ${item.getFormattedDuration()}"
                        "ur" -> "دورانیہ: ${item.getFormattedDuration()}"
                        "es" -> "Duración: ${item.getFormattedDuration()}"
                        "ru" -> "Длительность: ${item.getFormattedDuration()}"
                        "zh" -> "时长: ${item.getFormattedDuration()}"
                        "ja" -> "期間: ${item.getFormattedDuration()}"
                        else -> "Duration: ${item.getFormattedDuration()}"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryCyan
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = TextMuted
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Started: ${item.getFormattedStartDate()}",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Text(
                text = "Ended:   ${item.getFormattedEndDate()}",
                fontSize = 12.sp,
                color = TextSecondary
            )
            if (item.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Notes,
                        contentDescription = "Note",
                        tint = AccentGold,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = item.note,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
