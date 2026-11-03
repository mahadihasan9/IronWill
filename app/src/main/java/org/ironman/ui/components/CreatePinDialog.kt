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
import androidx.compose.ui.unit.sp
import org.ironman.ui.theme.AccentRed
import org.ironman.ui.theme.DarkSurface
import org.ironman.ui.theme.PrimaryCyan
import org.ironman.ui.theme.TextMuted
import org.ironman.ui.theme.TextPrimary

@Composable
fun CreatePinDialog(
    appLanguage: String = "en",
    onDismiss: () -> Unit,
    onPinCreated: (newPin: String) -> Unit
) {
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val newPinFocusRequester = remember { FocusRequester() }
    val confirmPinFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        newPinFocusRequester.requestFocus()
        keyboardController?.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when (appLanguage) {
                    "bn" -> "৪-ডিজিট সিকিউরিটি পিন সেট করুন"
                    "ar" -> "تعيين رمز أمان من 4 أرقام"
                    "hi" -> "4-अंकीय सुरक्षा पिन सेट करें"
                    "ur" -> "4 ہندسوں کا سیکیورٹی پن سیٹ کریں"
                    "es" -> "Establecer PIN de Seguridad de 4 Dígitos"
                    "ru" -> "Установите 4-значный PIN-код"
                    "zh" -> "设置 4位数 安全 PIN 码"
                    "ja" -> "4桁のセキュリティPINを設定"
                    else -> "Set 4-Digit Security PIN"
                },
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "নতুন ৪-ডিজিট পিন লিখুন:"
                        "ar" -> "أدخل رمز جديد من 4 أرقام:"
                        "hi" -> "नया 4-अंकीय पिन दर्ज करें:"
                        "ur" -> "نیا 4 ہندسوں کا پن درج کریں:"
                        "es" -> "Introduce un nuevo PIN de 4 dígitos:"
                        "ru" -> "Введите новый 4-значный PIN:"
                        "zh" -> "输入新 4位数 PIN 码:"
                        "ja" -> "新しい4桁のPINを入力してください:"
                        else -> "Enter New 4-Digit PIN:"
                    },
                    fontSize = 13.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                PinBoxInput(
                    pinValue = newPinInput,
                    onPinChange = {
                        newPinInput = it
                        errorMessage = ""
                        if (it.length == 4) {
                            confirmPinFocusRequester.requestFocus()
                        }
                    },
                    focusRequester = newPinFocusRequester
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = when (appLanguage) {
                        "bn" -> "নতুন পিন নিশ্চিত করুন:"
                        "ar" -> "تأكيد الرمز الجديد:"
                        "hi" -> "नए पिन की पुष्टि करें:"
                        "ur" -> "نئے پن کی تصدیق کریں:"
                        "es" -> "Confirmar nuevo PIN:"
                        "ru" -> "Подтвердите новый PIN:"
                        "zh" -> "确认新 PIN 码:"
                        "ja" -> "新しいPINを確認:"
                        else -> "Confirm New 4-Digit PIN:"
                    },
                    fontSize = 13.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                PinBoxInput(
                    pinValue = confirmPinInput,
                    onPinChange = {
                        confirmPinInput = it
                        errorMessage = ""
                    },
                    focusRequester = confirmPinFocusRequester
                )

                if (errorMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(errorMessage, color = AccentRed, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPinInput.length != 4) {
                        errorMessage = when (appLanguage) {
                            "bn" -> "পিন অবশ্যই ৪ সংখ্যার হতে হবে"
                            "ar" -> "يجب أن يتكون الرمز من 4 أرقام"
                            "hi" -> "पिन 4 अंकों का होना चाहिए"
                            "ur" -> "پن 4 ہندسوں کا ہونا ضروری ہے"
                            "es" -> "El PIN debe tener 4 dígitos"
                            "ru" -> "PIN должен состоять из 4 цифр"
                            "zh" -> "PIN 码必须为 4 位数字"
                            "ja" -> "PINは4桁で指定してください"
                            else -> "PIN must be 4 digits"
                        }
                    } else if (newPinInput != confirmPinInput) {
                        errorMessage = when (appLanguage) {
                            "bn" -> "পিন দুটো মিলছে না"
                            "ar" -> "الرمز غير متطابق"
                            "hi" -> "पिन मेल नहीं खा रहे हैं"
                            "ur" -> "پن میچ نہیں کر رہے"
                            "es" -> "Los PIN no coinciden"
                            "ru" -> "PIN-коды не совпадают"
                            "zh" -> "两次输入的 PIN 码不一致"
                            "ja" -> "PINが一致しません"
                            else -> "PINs do not match"
                        }
                    } else {
                        onPinCreated(newPinInput)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
            ) {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "পিন সেভ করুন"
                        "ar" -> "حفظ الرمز"
                        "hi" -> "पिन सहेजें"
                        "ur" -> "پن محفوظ کریں"
                        "es" -> "Guardar PIN"
                        "ru" -> "Сохранить PIN"
                        "zh" -> "保存 PIN"
                        "ja" -> "PINを保存"
                        else -> "Save PIN"
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
