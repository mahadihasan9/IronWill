package org.ironman.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ironman.ui.theme.AccentRed
import org.ironman.ui.theme.TextMuted
import org.ironman.ui.theme.TextPrimary

@Composable
fun PinVerificationDialog(
    correctPin: String,
    title: String = "",
    subtitle: String = "",
    appLanguage: String = "en",
    onDismiss: () -> Unit,
    onPinSuccess: () -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val displayTitle = if (title.isNotBlank()) title else when (appLanguage) {
        "bn" -> "সিকিউরিটি পিন দিন"
        "ar" -> "رمز الأمان مطلوب"
        "hi" -> "सुरक्षा पिन आवश्यक"
        "ur" -> "سیکیورٹی پن درکار ہے"
        "es" -> "PIN de Seguridad Requerido"
        "ru" -> "Требуется PIN-код"
        "zh" -> "需要安全 PIN 码"
        "ja" -> "セキュリティPINが必要です"
        else -> "Security PIN Required"
    }

    val displaySubtitle = if (subtitle.isNotBlank()) subtitle else when (appLanguage) {
        "bn" -> "সামনে এগোতে ৪-ডিজিট পিন লিখুন:"
        "ar" -> "أدخل رمز الأمان من 4 أرقام للمتابعة:"
        "hi" -> "आगे बढ़ने के लिए 4-अंकीय पिन दर्ज करें:"
        "ur" -> "آگے بڑھنے کے لیے 4 ہندسوں کا پن درج کریں:"
        "es" -> "Introduce tu PIN de 4 dígitos para continuar:"
        "ru" -> "Введите 4-значный PIN для продолжения:"
        "zh" -> "请输入 4位数 PIN 码以继续:"
        "ja" -> "続行するには4桁のPINを入力してください:"
        else -> "Enter your 4-digit PIN to proceed:"
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = displayTitle, color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = displaySubtitle,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(16.dp))

                PinBoxInput(
                    pinValue = pinInput,
                    onPinChange = {
                        pinInput = it
                        isError = false
                        if (it.length == 4) {
                            if (it == correctPin) {
                                onPinSuccess()
                            } else {
                                isError = true
                            }
                        }
                    },
                    focusRequester = focusRequester
                )

                if (isError) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "ভুল পিন! আবার চেষ্টা করুন।"
                            "ar" -> "رمز خاطئ. حاول مرة أخرى."
                            "hi" -> "गलत पिन! फिर से प्रयास करें।"
                            "ur" -> "غلط پن! دوبارہ کوشش کریں۔"
                            "es" -> "PIN incorrecto. Inténtalo de nuevo."
                            "ru" -> "Неверный PIN. Попробуйте еще раз."
                            "zh" -> "PIN 码错误，请重试。"
                            "ja" -> "PINが正しくありません。再試行してください。"
                            else -> "Incorrect PIN. Try again."
                        },
                        color = AccentRed
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pinInput == correctPin) {
                        onPinSuccess()
                    } else {
                        isError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
            ) {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "নিশ্চিত করুন"
                        "ar" -> "تأكيد"
                        "hi" -> "पुष्टि करें"
                        "ur" -> "تصدیق کریں"
                        "es" -> "Confirmar"
                        "ru" -> "Подтвердить"
                        "zh" -> "确认"
                        "ja" -> "確認"
                        else -> "Confirm"
                    },
                    color = TextPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
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
