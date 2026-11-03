package org.ironman.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import org.ironman.ui.theme.DarkBackground
import org.ironman.ui.theme.DarkSurface
import org.ironman.ui.theme.PrimaryCyan
import org.ironman.ui.theme.TextMuted
import org.ironman.ui.theme.TextPrimary

@Composable
fun AppLockOverlay(
    correctPin: String,
    appLanguage: String = "en",
    onSaveInitialPin: (newPin: String) -> Unit = {},
    onUnlockSuccess: () -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val isFirstTimeSetup = correctPin.isBlank()

    val firstPinFocusRequester = remember { FocusRequester() }
    val confirmPinFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        firstPinFocusRequester.requestFocus()
        keyboardController?.show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = if (isFirstTimeSetup) Icons.Default.Security else Icons.Default.Lock,
                contentDescription = "App Lock",
                tint = PrimaryCyan,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isFirstTimeSetup) {
                    when (appLanguage) {
                        "bn" -> "৪-ডিজিট পিন সেট করুন"
                        "ar" -> "إنشاء رمز أمان من 4 أرقام"
                        "hi" -> "4-अंकीय पिन बनाएं"
                        "ur" -> "4 ہندسوں کا سیکیورٹی پن بنائیں"
                        "es" -> "CREAR PIN DE 4 DÍGITOS"
                        "ru" -> "СОЗДАТЬ 4-ЗНАЧНЫЙ PIN"
                        "zh" -> "创建 4位数 PIN 码"
                        "ja" -> "4桁のPINコードを作成"
                        else -> "CREATE 4-DIGIT PIN"
                    }
                } else {
                    when (appLanguage) {
                        "bn" -> "আইরন উইল লক করা আছে"
                        "ar" -> "تطبيق IRON WILL مقفل"
                        "hi" -> "आयरन विल लॉक है"
                        "ur" -> "آئرن ول لاک ہے"
                        "es" -> "IRON WILL BLOQUEADO"
                        "ru" -> "IRON WILL ЗАБЛОКИРОВАН"
                        "zh" -> "IRON WILL 已锁定"
                        "ja" -> "IRON WILL ロック中"
                        else -> "IRON WILL LOCKED"
                    }
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isFirstTimeSetup) {
                    when (appLanguage) {
                        "bn" -> "অ্যাপ ও স্ট্রিক নিরাপদ রাখতে ৪-ডিজিট পিন সেট করুন"
                        "ar" -> "قم بتعيين رمز مكون من 4 أرقام لحماية تطبيقك وسجلك"
                        "hi" -> "अपने ऐप और स्ट्रिक को सुरक्षित करने के लिए 4-अंकीय पिन सेट करें"
                        "ur" -> "اپنی ایپ اور اسٹریک کو محفوظ کرنے کے لیے 4 ہندسوں کا پن سیٹ کریں"
                        "es" -> "Configura un PIN de 4 dígitos para asegurar tu app y racha"
                        "ru" -> "Установите 4-значный PIN для защиты приложения и прогресса"
                        "zh" -> "设置 4位数 PIN 码以保护您的应用与记录"
                        "ja" -> "アプリと記録を保護するために4桁のPINを設定してください"
                        else -> "Set up a 4-digit PIN to secure your app & streak"
                    }
                } else {
                    when (appLanguage) {
                        "bn" -> "অ্যাপে প্রবেশ করতে আপনার ৪-ডিজিট পিন দিন"
                        "ar" -> "أدخل رمزك المكون من 4 أرقام لفتح التطبيق"
                        "hi" -> "ऐप खोलने के लिए अपना 4-अंकीय पिन दर्ज करें"
                        "ur" -> "ایپ کھولنے کے لیے اپنا 4 ہندسوں کا پن درج کریں"
                        "es" -> "Introduce tu PIN de 4 dígitos para abrir la app"
                        "ru" -> "Введите ваш 4-значный PIN для входа в приложение"
                        "zh" -> "输入您的 4位数 PIN 码以打开应用"
                        "ja" -> "アプリを開くには4桁のPINを入力してください"
                        else -> "Enter your 4-digit PIN to open the app"
                    }
                },
                fontSize = 14.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (isFirstTimeSetup) {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "নতুন পিন লিখুন:"
                        "ar" -> "أدخل الرمز الجديد:"
                        "hi" -> "नया पिन दर्ज करें:"
                        "ur" -> "نیا پن درج کریں:"
                        "es" -> "Introduce el Nuevo PIN:"
                        "ru" -> "Введите новый PIN:"
                        "zh" -> "输入新 PIN 码:"
                        "ja" -> "新しいPINを入力:"
                        else -> "Enter New PIN:"
                    },
                    fontSize = 13.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                PinBoxInput(
                    pinValue = pinInput,
                    onPinChange = {
                        pinInput = it
                        errorMessage = ""
                        if (it.length == 4) {
                            confirmPinFocusRequester.requestFocus()
                        }
                    },
                    focusRequester = firstPinFocusRequester
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = when (appLanguage) {
                        "bn" -> "নতুন পিন নিশ্চিত করুন:"
                        "ar" -> "تأكيد الرمز الجديد:"
                        "hi" -> "नए पिन की पुष्टि करें:"
                        "ur" -> "نئے پن کی تصدیق کریں:"
                        "es" -> "Confirmar Nuevo PIN:"
                        "ru" -> "Подтвердите новый PIN:"
                        "zh" -> "确认新 PIN 码:"
                        "ja" -> "新しいPINを確認:"
                        else -> "Confirm New PIN:"
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
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage,
                        color = AccentRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (pinInput.length != 4) {
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
                        } else if (pinInput != confirmPinInput) {
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
                            onSaveInitialPin(pinInput)
                            onUnlockSuccess()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "পিন সেভ করুন"
                            "ar" -> "حفظ وتأكيد الرمز"
                            "hi" -> "पिन सहेजें"
                            "ur" -> "پن محفوظ کریں"
                            "es" -> "Guardar y Establecer PIN"
                            "ru" -> "Сохранить PIN"
                            "zh" -> "保存并设置 PIN"
                            "ja" -> "PINを保存"
                            else -> "Save & Set PIN"
                        },
                        color = DarkSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                PinBoxInput(
                    pinValue = pinInput,
                    onPinChange = {
                        pinInput = it
                        errorMessage = ""
                        if (it.length == 4) {
                            if (it == correctPin) {
                                onUnlockSuccess()
                            } else {
                                errorMessage = when (appLanguage) {
                                    "bn" -> "ভুল পিন! আবার চেষ্টা করুন।"
                                    "ar" -> "رمز خاطئ. حاول مرة أخرى."
                                    "hi" -> "गलत पिन! फिर से प्रयास करें।"
                                    "ur" -> "غلط پن! دوبارہ کوشش کریں۔"
                                    "es" -> "PIN incorrecto. Inténtalo de nuevo."
                                    "ru" -> "Неверный PIN. Попробуйте еще раз."
                                    "zh" -> "PIN 码错误，请重试。"
                                    "ja" -> "PINが正しくありません。再試行してください。"
                                    else -> "Incorrect PIN. Try again."
                                }
                            }
                        }
                    },
                    focusRequester = firstPinFocusRequester
                )

                if (errorMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage,
                        color = AccentRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
