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
import org.ironman.ui.theme.DarkSurface
import org.ironman.ui.theme.PrimaryCyan
import org.ironman.ui.theme.TextMuted
import org.ironman.ui.theme.TextPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun StartDateTimeDialog(
    initialTimestamp: Long = System.currentTimeMillis(),
    appLanguage: String = "en",
    onDismiss: () -> Unit,
    onConfirm: (newTimestamp: Long) -> Unit
) {
    val context = LocalContext.current
    val now = System.currentTimeMillis()
    var selectedCalendar by remember {
        mutableStateOf(Calendar.getInstance().apply {
            timeInMillis = if (initialTimestamp > now) now else initialTimestamp
        })
    }

    val isFutureTime = selectedCalendar.timeInMillis > System.currentTimeMillis()
    val sdf = remember { SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when (appLanguage) {
                    "bn" -> "স্ট্রিক শুরুর সময় নির্ধারণ করুন"
                    "ar" -> "تحديد وقت بدء العداد"
                    else -> "Set Start Date & Time"
                },
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "আপনার স্ট্রিক কখন শুরু হয়েছে নির্ধারণ করুন (যেমন: এখন থেকে, বা গতকাল থেকে):"
                        "ar" -> "اختر متى بدأ العداد (مثلاً: الآن، أو أمس):"
                        else -> "Pick when your streak started (e.g. right now, yesterday, or past time):"
                    },
                    fontSize = 13.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when (appLanguage) {
                        "bn" -> "নির্বাচিত সময়:\n${sdf.format(selectedCalendar.time)}"
                        "ar" -> "الوقت المحدد:\n${sdf.format(selectedCalendar.time)}"
                        else -> "Selected Start Time:\n${sdf.format(selectedCalendar.time)}"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFutureTime) AccentRed else PrimaryCyan
                )

                if (isFutureTime) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "ভবিষ্যতের সময় নির্ধারণ করা যাবে না!"
                            "ar" -> "لا يمكن تحديد وقت في المستقبل!"
                            else -> "Start time cannot be in the future!"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AccentRed
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick buttons: "Now", "Yesterday"
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
                                "bn" -> "এখন থেকে"
                                "ar" -> "الآن"
                                else -> "Right Now"
                            },
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            selectedCalendar = Calendar.getInstance().apply {
                                add(Calendar.DAY_OF_YEAR, -1)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = when (appLanguage) {
                                "bn" -> "গতকাল"
                                "ar" -> "أمس"
                                else -> "Yesterday"
                            },
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        showDatePicker(context, selectedCalendar) { year, month, day ->
                            showTimePicker(context, selectedCalendar) { hour, minute ->
                                val cal = Calendar.getInstance().apply {
                                    set(year, month, day, hour, minute, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                val currentTime = System.currentTimeMillis()
                                if (cal.timeInMillis > currentTime) {
                                    selectedCalendar = Calendar.getInstance()
                                } else {
                                    selectedCalendar = cal
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "কাস্টম তারিখ ও সময়"
                            "ar" -> "تاريخ ووقت مخصص"
                            else -> "Pick Custom Date & Time"
                        },
                        fontSize = 13.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!isFutureTime) {
                        onConfirm(selectedCalendar.timeInMillis)
                    }
                },
                enabled = !isFutureTime,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
            ) {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "স্টার্ট করুন"
                        "ar" -> "ابدأ العداد"
                        else -> "Start Counter"
                    },
                    color = DarkSurface,
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
