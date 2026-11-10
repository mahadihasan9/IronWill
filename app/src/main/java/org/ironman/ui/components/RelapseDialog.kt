package org.ironman.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ironman.ui.theme.AccentRed
import org.ironman.ui.theme.PrimaryCyan
import org.ironman.ui.theme.TextMuted
import org.ironman.ui.theme.TextPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun RelapseDialog(
    startTimestamp: Long = 0L,
    appLanguage: String = "en",
    onDismiss: () -> Unit,
    onConfirm: (resetTimestamp: Long, note: String) -> Unit
) {
    val context = LocalContext.current
    var note by remember { mutableStateOf("") }
    var selectedCalendar by remember {
        mutableStateOf(Calendar.getInstance())
    }

    val now = System.currentTimeMillis()
    val selectedTime = selectedCalendar.timeInMillis

    val isInvalidTime = selectedTime < startTimestamp || selectedTime > now
    val isBeforeStart = selectedTime < startTimestamp
    val isFuture = selectedTime > now

    val sdf = remember { SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when (appLanguage) {
                    "bn" -> "কাউন্টার রিসেট করুন"
                    "ar" -> "إعادة ضبط العداد"
                    "hi" -> "काउंटर रीसेट करें"
                    "ur" -> "کاؤنٹر ری سیٹ کریں"
                    "es" -> "Reiniciar Contador"
                    "ru" -> "Сбросить счетчик"
                    "zh" -> "重置计时器"
                    "ja" -> "カウンターをリセット"
                    else -> "Reset Counter"
                },
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "সবকিছু আবার শুরু করা যায়। প্রতিটি চেষ্টা আপনার ইচ্ছাশক্তিকে আরও মজবুত করে। রিসেটের সময় সিলেক্ট করুন ও একটি নোট লিখুন:"
                        "ar" -> "من الجيد البدء من جديد. حدد وقت إعادة الضبط وأضف ملاحظة (اختياري):"
                        "hi" -> "फिर से शुरू करना ठीक है। रीसेट का समय चुनें और एक त्वरित नोट जोड़ें:"
                        "ur" -> "دوبارہ شروع کرنا ٹھیک ہے۔ ری سیٹ کا وقت اور نوٹ منتخب کریں:"
                        "es" -> "Está bien reiniciar. Elige la hora del reinicio y añade una nota:"
                        "ru" -> "Начать заново — это нормально. Выберите время сброса и заметку:"
                        "zh" -> "重新开始没关系。选择重置时间并添加笔记："
                        "ja" -> "やり直しても大丈夫です。リセット時間を選択し、メモを追加してください："
                        else -> "It's okay to restart. Choose when you relapsed and add an optional note:"
                    },
                    fontSize = 13.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = {
                        Text(
                            text = when (appLanguage) {
                                "bn" -> "কারণ / অনুভূতির নোট (ঐচ্ছিক)"
                                "ar" -> "ملاحظة السبب (اختياري)"
                                "hi" -> "कारण / विचार (वैकल्पिक)"
                                "ur" -> "وجہ / نوٹ (اختیاری)"
                                "es" -> "Nota de Reflexión (Opcional)"
                                "ru" -> "Заметка / Причина (необязательно)"
                                "zh" -> "原因 / 感悟 (可选)"
                                "ja" -> "理由・メモ (任意)"
                                else -> "Reason / Reflection Note (Optional)"
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = when (appLanguage) {
                        "bn" -> "রিসেটের সময়:\n${sdf.format(selectedCalendar.time)}"
                        "ar" -> "وقت إعادة الضبط:\n${sdf.format(selectedCalendar.time)}"
                        "hi" -> "रीसेट का समय:\n${sdf.format(selectedCalendar.time)}"
                        "ur" -> "ری سیٹ کا وقت:\n${sdf.format(selectedCalendar.time)}"
                        "es" -> "Hora del reinicio:\n${sdf.format(selectedCalendar.time)}"
                        "ru" -> "Время сброса:\n${sdf.format(selectedCalendar.time)}"
                        "zh" -> "重置时间:\n${sdf.format(selectedCalendar.time)}"
                        "ja" -> "リセット日時:\n${sdf.format(selectedCalendar.time)}"
                        else -> "Reset Time:\n${sdf.format(selectedCalendar.time)}"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isInvalidTime) AccentRed else PrimaryCyan
                )

                if (isBeforeStart) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "রিসেটের সময় স্ট্রিক শুরুর সময়ের আগের হতে পারবে না!"
                            "ar" -> "لا يمكن أن يكون وقت إعادة الضبط قبل وقت بدء العداد!"
                            "hi" -> "रीसेट का समय स्ट्रिक शुरू होने के समय से पहले नहीं हो सकता!"
                            "ur" -> "ری سیٹ کا وقت شروع ہونے سے پہلے کا نہیں ہو سکتا!"
                            "es" -> "¡El reinicio no puede ser anterior al inicio de la racha!"
                            "ru" -> "Сброс не может быть раньше времени начала серии!"
                            "zh" -> "重置时间不能早于开始时间！"
                            "ja" -> "リセット時間を開始日時より前にすることはできません！"
                            else -> "Reset time cannot be earlier than streak start time!"
                        },
                        fontSize = 12.sp,
                        color = AccentRed
                    )
                } else if (isFuture) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "ভবিষ্যতের সময় নির্ধারণ করা যাবে না!"
                            "ar" -> "لا يمكن تحديد وقت في المستقبل!"
                            "hi" -> "भविष्य का समय सेट नहीं किया जा सकता!"
                            "ur" -> "مستقبل کا وقت مقرر نہیں کیا جا سکتا!"
                            "es" -> "¡No se puede seleccionar una hora futura!"
                            "ru" -> "Время сброса не может быть в будущем!"
                            "zh" -> "不能选择未来的时间！"
                            "ja" -> "未来の時間を設定することはできません！"
                            else -> "Reset time cannot be in the future!"
                        },
                        fontSize = 12.sp,
                        color = AccentRed
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            selectedCalendar = Calendar.getInstance()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = when (appLanguage) {
                                "bn" -> "এখনই (Now)"
                                "ar" -> "الآن"
                                "hi" -> "अभी"
                                "ur" -> "ابھی"
                                "es" -> "Ahora Mismo"
                                "ru" -> "Сейчас"
                                "zh" -> "现在"
                                "ja" -> "今すぐ"
                                else -> "Right Now"
                            },
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            showDatePicker(context, selectedCalendar, startTimestamp) { year, month, day ->
                                showTimePicker(context, selectedCalendar) { hour, minute ->
                                    val cal = Calendar.getInstance().apply {
                                        set(year, month, day, hour, minute, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    selectedCalendar = cal
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = when (appLanguage) {
                                "bn" -> "কাস্টম সময়"
                                "ar" -> "وقت مخصص"
                                "hi" -> "कस्टम समय"
                                "ur" -> "کسٹم وقت"
                                "es" -> "Hora Personalizada"
                                "ru" -> "Выбрать время"
                                "zh" -> "自定义时间"
                                "ja" -> "カスタム日時"
                                else -> "Custom Time"
                            },
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!isInvalidTime) {
                        onConfirm(selectedCalendar.timeInMillis, note)
                    }
                },
                enabled = !isInvalidTime,
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
            ) {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "রিসেট করুন"
                        "ar" -> "إعادة ضبط"
                        "hi" -> "रीसेट करें"
                        "ur" -> "ری سیٹ"
                        "es" -> "Reiniciar"
                        "ru" -> "Сброс"
                        "zh" -> "重置"
                        "ja" -> "リセット"
                        else -> "Reset Counter"
                    },
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "বাতিল"
                        "ar" -> "إلغاء"
                        "hi" -> "ردد करें"
                        "ur" -> "منسوخ"
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

private fun showDatePicker(
    context: Context,
    calendar: Calendar,
    minTimestamp: Long,
    onDatePicked: (year: Int, month: Int, day: Int) -> Unit
) {
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDatePicked(year, month, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.maxDate = System.currentTimeMillis()
        if (minTimestamp > 0) {
            datePicker.minDate = minTimestamp
        }
    }.show()
}

private fun showTimePicker(
    context: Context,
    calendar: Calendar,
    onTimePicked: (hour: Int, minute: Int) -> Unit
) {
    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            onTimePicked(hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    ).show()
}
