package org.ironman.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ironman.ui.components.CreatePinDialog
import org.ironman.ui.components.PinBoxInput
import org.ironman.ui.components.PinVerificationDialog
import org.ironman.ui.theme.AccentRed
import org.ironman.ui.theme.DarkSurface
import org.ironman.ui.theme.PrimaryCyan
import org.ironman.ui.theme.TextMuted
import org.ironman.ui.theme.TextPrimary
import org.ironman.ui.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentLanguage by viewModel.appLanguage.collectAsState()
    val pinCode by viewModel.pinCode.collectAsState()
    val appLockEnabled by viewModel.appLockEnabled.collectAsState()

    var showChangePinDialog by remember { mutableStateOf(false) }
    var showCreatePinDialog by remember { mutableStateOf(false) }
    var showPinDialogForToggle by remember { mutableStateOf(false) }
    var targetToggleState by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val languageScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = when (currentLanguage) {
                "bn" -> "সেটিংস ও নিরাপত্তা"
                "ar" -> "الإعدادات والأمان"
                "hi" -> "सेटिंग्स और सुरक्षा"
                "ur" -> "سیٹنگز اور سیکیورٹی"
                "es" -> "Ajustes y Seguridad"
                "ru" -> "Настройки и безопасность"
                "zh" -> "设置与安全"
                "ja" -> "設定とセキュリティ"
                else -> "Settings & Security"
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Language Selector Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = PrimaryCyan
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "App Language / ভাষা",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(languageScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "en" to "English",
                        "bn" to "বাংলা",
                        "ar" to "العربية",
                        "hi" to "हिन्दी",
                        "ur" to "اردو",
                        "es" to "Español",
                        "ru" to "Русский",
                        "zh" to "中文",
                        "ja" to "日本語"
                    ).forEach { (langCode, label) ->
                        FilterChip(
                            selected = currentLanguage == langCode,
                            onClick = { viewModel.setLanguage(langCode) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCyan,
                                selectedLabelColor = DarkSurface,
                                containerColor = DarkSurface,
                                labelColor = TextPrimary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security PIN Lock & App Startup Lock Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Toggle for App Startup Lock
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "PIN Lock",
                            tint = PrimaryCyan
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = when (currentLanguage) {
                                    "bn" -> "অ্যাপ ওপেন পিন লক"
                                    "ar" -> "قفل فتح التطبيق"
                                    "hi" -> "ऐप ओपन पिन लॉक"
                                    "ur" -> "ایپ اوپن پن لاک"
                                    "es" -> "Bloqueo de Apertura de App"
                                    "ru" -> "Блокировка входа в приложение"
                                    "zh" -> "应用启动 PIN 码锁"
                                    "ja" -> "アプリ起動PINロック"
                                    else -> "Lock App on Startup"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = when (currentLanguage) {
                                    "bn" -> "প্রতিবার অ্যাপ খোলার সময় পিন প্রয়োজন (ডিফল্ট: অফ)"
                                    "ar" -> "يتطلب الرمز في كل مرة يتم فيها فتح التطبيق"
                                    "hi" -> "हर बार ऐप खोलने पर पिन चाहिए (डिफ़ॉल्ट: बंद)"
                                    "ur" -> "ہر بار ایپ کھولنے پر پن درکار ہے"
                                    "es" -> "Requiere PIN cada vez que se abre la app"
                                    "ru" -> "Запрашивать PIN при каждом входе"
                                    "zh" -> "每次打开应用均需输入 PIN 码"
                                    "ja" -> "アプリ起動ごとにPINを要求"
                                    else -> "Require 4-digit PIN every time app opens"
                                },
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Switch(
                        checked = appLockEnabled,
                        onCheckedChange = { newState ->
                            targetToggleState = newState
                            if (pinCode.isBlank()) {
                                showCreatePinDialog = true
                            } else {
                                showPinDialogForToggle = true
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PrimaryCyan,
                            checkedTrackColor = DarkSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = {
                        if (pinCode.isBlank()) {
                            showCreatePinDialog = true
                        } else {
                            showChangePinDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Password, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (currentLanguage) {
                            "bn" -> if (pinCode.isBlank()) "৪-ডিজিট পিন সেট করুন" else "৪-ডিজিট পিন পরিবর্তন করুন"
                            "ar" -> if (pinCode.isBlank()) "تعيين الرمز" else "تغيير رمز الـ 4 أرقام"
                            "hi" -> if (pinCode.isBlank()) "4-अंकीय पिन सेट करें" else "4-अंकीय पिन बदलें"
                            "ur" -> if (pinCode.isBlank()) "4 ہندسوں کا پن سیٹ کریں" else "4 ہندسوں کا پن تبدیل کریں"
                            "es" -> if (pinCode.isBlank()) "Establecer PIN de 4 dígitos" else "Cambiar PIN de 4 dígitos"
                            "ru" -> if (pinCode.isBlank()) "Установить 4-значный PIN" else "Изменить 4-значный PIN"
                            "zh" -> if (pinCode.isBlank()) "设置 4位数 PIN 码" else "修改 4位数 PIN 码"
                            "ja" -> if (pinCode.isBlank()) "4桁のPINを設定" else "4桁のPINを変更"
                            else -> if (pinCode.isBlank()) "Set 4-Digit PIN" else "Change 4-Digit PIN"
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // About Developer / GitHub Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Developer",
                        tint = PrimaryCyan
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = when (currentLanguage) {
                            "bn" -> "ডেভেলপার সম্পর্কে"
                            "ar" -> "عن المطور"
                            "hi" -> "डेवलपर के बारे में"
                            "ur" -> "ڈویلپر کے بارے میں"
                            "es" -> "Acerca del Desarrollador"
                            "ru" -> "О разработчике"
                            "zh" -> "关于开发者"
                            "ja" -> "開発者について"
                            else -> "About Developer"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Mahadi Hasan",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Text(
                    text = when (currentLanguage) {
                        "bn" -> "আইরন উইল অ্যাপের ক্রিয়েটর ও ডেভেলপার"
                        "ar" -> "مطور تطبيق Iron Will"
                        "hi" -> "आयरन विल ऐप के निर्माता और डेवलपर"
                        "ur" -> "آئرن ول ایپ کے کریئیٹر اور ڈویلپر"
                        "es" -> "Creador y Desarrollador de la App Iron Will"
                        "ru" -> "Создатель и разработчик приложения Iron Will"
                        "zh" -> "Iron Will 应用的创作者与开发者"
                        "ja" -> "Iron Will アプリの制作者・開発者"
                        else -> "Creator & Developer of Iron Will App"
                    },
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                val context = LocalContext.current
                val githubUrl = "https://github.com/mahadihasan9"

                OutlinedButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "GitHub",
                        tint = PrimaryCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "github.com/mahadihasan9",
                        color = PrimaryCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    if (showCreatePinDialog) {
        CreatePinDialog(
            appLanguage = currentLanguage,
            onDismiss = { showCreatePinDialog = false },
            onPinCreated = { newPin ->
                viewModel.setPinCode(newPin)
                viewModel.setAppLockEnabled(targetToggleState)
                showCreatePinDialog = false
            }
        )
    }

    if (showPinDialogForToggle) {
        PinVerificationDialog(
            correctPin = pinCode,
            title = "Security PIN Required",
            subtitle = "Enter PIN to change App Lock setting:",
            onDismiss = { showPinDialogForToggle = false },
            onPinSuccess = {
                showPinDialogForToggle = false
                viewModel.setAppLockEnabled(targetToggleState)
            }
        )
    }

    if (showChangePinDialog) {
        ChangePinDialog(
            currentPin = pinCode,
            onDismiss = { showChangePinDialog = false },
            onSavePin = { newPin ->
                viewModel.setPinCode(newPin)
                showChangePinDialog = false
            }
        )
    }
}

@Composable
fun ChangePinDialog(
    currentPin: String,
    onDismiss: () -> Unit,
    onSavePin: (newPin: String) -> Unit
) {
    var oldPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val isOldPinNeeded = currentPin.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isOldPinNeeded) "Change 4-Digit PIN" else "Set 4-Digit PIN",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isOldPinNeeded) {
                    Text("Enter Current PIN:", fontSize = 13.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    PinBoxInput(
                        pinValue = oldPinInput,
                        onPinChange = {
                            oldPinInput = it
                            errorMessage = ""
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                Text("Enter New 4-Digit PIN:", fontSize = 13.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                PinBoxInput(
                    pinValue = newPinInput,
                    onPinChange = {
                        newPinInput = it
                        errorMessage = ""
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Confirm New 4-Digit PIN:", fontSize = 13.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                PinBoxInput(
                    pinValue = confirmPinInput,
                    onPinChange = {
                        confirmPinInput = it
                        errorMessage = ""
                    }
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
                    if (isOldPinNeeded && oldPinInput != currentPin) {
                        errorMessage = "Current PIN is incorrect"
                    } else if (newPinInput.length != 4) {
                        errorMessage = "New PIN must be 4 digits"
                    } else if (newPinInput != confirmPinInput) {
                        errorMessage = "New PINs do not match"
                    } else {
                        onSavePin(newPinInput)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
            ) {
                Text("Save PIN", color = DarkSurface, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
