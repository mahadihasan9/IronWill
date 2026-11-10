package org.ironman.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SystemUpdate
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ironman.ui.components.CreatePinDialog
import org.ironman.ui.components.PinBoxInput
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
import org.ironman.utils.LauncherAliasManager
import org.ironman.utils.UpdateStatus

enum class BackupActionType {
    EXPORT,
    IMPORT
}

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLanguage by viewModel.appLanguage.collectAsState()
    val pinCode by viewModel.pinCode.collectAsState()
    val appLockEnabled by viewModel.appLockEnabled.collectAsState()
    val updateStatus by viewModel.updateStatus.collectAsState()

    val currentVersionName = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkForUpdates(context)
    }

    val currentIconId by viewModel.selectedIconId.collectAsState()
    val currentNameIndex by viewModel.selectedNameIndex.collectAsState()

    var chosenIconId by remember(currentIconId) { mutableIntStateOf(currentIconId) }
    var chosenNameIndex by remember(currentNameIndex) { mutableIntStateOf(currentNameIndex) }

    var showChangePinDialog by remember { mutableStateOf(false) }
    var showCreatePinDialog by remember { mutableStateOf(false) }
    var showPinDialogForToggle by remember { mutableStateOf(false) }
    var targetToggleState by remember { mutableStateOf(false) }

    var showPinDialogForIconChange by remember { mutableStateOf(false) }
    var showCreatePinDialogForIconChange by remember { mutableStateOf(false) }

    var showPinDialogForBackup by remember { mutableStateOf(false) }
    var pendingBackupAction by remember { mutableStateOf(BackupActionType.EXPORT) }

    var pendingExportJson by remember { mutableStateOf<String?>(null) }
    var showImportWarningDialog by remember { mutableStateOf(false) }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { targetUri ->
            pendingExportJson?.let { json ->
                try {
                    context.contentResolver.openOutputStream(targetUri)?.use { os ->
                        os.write(json.toByteArray())
                    }
                    Toast.makeText(context, when (currentLanguage) {
                        "bn" -> "ব্যাকআপ ফাইল এক্সপোর্ট সফল হয়েছে"
                        "ar" -> "تم تصدير ملف النسخ الاحتياطي بنجاح"
                        "hi" -> "बैकअप फ़ाइल सफलतापूर्वक निर्यात की गई"
                        "ur" -> "بیک اپ فائل ایکسپورٹ ہو گئی"
                        "es" -> "Copia de seguridad exportada con éxito"
                        "ru" -> "Резервная копия успешно экспортирована"
                        "zh" -> "备份文件导出成功"
                        "ja" -> "バックアップファイルがエクスポートされました"
                        else -> "Backup file exported successfully"
                    }, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { sourceUri ->
            try {
                val jsonString = context.contentResolver.openInputStream(sourceUri)?.bufferedReader()?.use { it.readText() }
                if (!jsonString.isNullOrBlank()) {
                    viewModel.importBackupJson(jsonString) { success ->
                        if (success) {
                            Toast.makeText(context, when (currentLanguage) {
                                "bn" -> "ব্যাকআপ ফাইল ইমপোর্ট সফল হয়েছে"
                                "ar" -> "تم استيراد النسخة الاحتياطية بنجاح"
                                "hi" -> "बैकअप फ़ाइल सफलतापूर्वक आयात की गई"
                                "ur" -> "بیک اپ فائل امپورٹ ہو گئی"
                                "es" -> "Copia de seguridad importada con éxito"
                                "ru" -> "Резервная копия успешно импортирована"
                                "zh" -> "备份文件导入成功"
                                "ja" -> "バックアップファイルがインポートされました"
                                else -> "Backup file imported successfully"
                            }, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, when (currentLanguage) {
                                "bn" -> "ইমপোর্ট ব্যর্থ হয়েছে: ফাইলটি সঠিক নয়"
                                "ar" -> "فشل الاستيراد: ملف غير صالح"
                                "hi" -> "आयात विफल: अमान्य फ़ाइल"
                                "ur" -> "امپورٹ ناکام: غیر موزوں فائل"
                                "es" -> "Error al importar: Archivo no válido"
                                "ru" -> "Ошибка импорта: неверный файл"
                                "zh" -> "导入失败：文件格式无效"
                                "ja" -> "インポート失敗：無効なファイル"
                                else -> "Import failed: Invalid file"
                            }, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val triggerExport = {
        viewModel.exportBackupJson { json ->
            pendingExportJson = json
            createDocumentLauncher.launch("iron_will_backup.json")
        }
    }

    val triggerImport = {
        showImportWarningDialog = true
    }

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

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = "App Icon & Name",
                        tint = PrimaryCyan
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = when (currentLanguage) {
                                "bn" -> "অ্যাপ আইকন ও নাম পরিবর্তন"
                                "ar" -> "تغيير أيقونة واسم التطبيق"
                                "hi" -> "ऐप आइकन और नाम बदलें"
                                "ur" -> "ایپ آئیکن اور نام تبدیل کریں"
                                "es" -> "Cambiar Icono y Nombre de App"
                                "ru" -> "Сменить иконку и название"
                                "zh" -> "更改图标与应用名称"
                                "ja" -> "アイコンとアプリ名の変更"
                                else -> "App Icon & Name Disguise"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = when (currentLanguage) {
                                "bn" -> "ইচ্ছামত আলাদাভাবে আইকন ও নাম সিলেক্ট করুন (পিন প্রয়োজন)"
                                "ar" -> "اختر أي أيقونة واسم بشكل مستقل (يلزم وجود رمز PIN)"
                                "hi" -> "स्वतंत्र रूप से कोई भी आइकन और नाम चुनें (पिन आवश्यक)"
                                "ur" -> "آزادانہ طور پر کوئی بھی آئیکن اور نام منتخب کریں (پن ضروری)"
                                "es" -> "Elige cualquier icono y nombre independientemente (PIN requerido)"
                                "ru" -> "Выберите любую иконку и название (требуется PIN)"
                                "zh" -> "独立选择任意图标和名称（需要 PIN 码）"
                                "ja" -> "アイコンと名前を独立して選択（PIN必須）"
                                else -> "Pick any icon & name independently (PIN required)"
                            },
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when (currentLanguage) {
                        "bn" -> "১. অ্যাপের আইকন বেছে নিন:"
                        "ar" -> "١. اختر أيقونة التطبيق:"
                        "hi" -> "1. ऐप का आइकन चुनें:"
                        "ur" -> "1. ایپ آئیکن منتخب کریں:"
                        "es" -> "1. Elige el Icono de la App:"
                        "ru" -> "1. Выберите иконку приложения:"
                        "zh" -> "1. 选择应用图标:"
                        "ja" -> "1. アプリのアイコンを選択:"
                        else -> "1. Choose App Icon:"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                val iconScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(iconScrollState),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LauncherAliasManager.AVAILABLE_ICONS.forEach { iconPreset ->
                        val isSelected = chosenIconId == iconPreset.id
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) PrimaryCyan else TextMuted.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .background(
                                    color = if (isSelected) PrimaryCyan.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { chosenIconId = iconPreset.id }
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = iconPreset.iconResId),
                                contentDescription = iconPreset.title,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when (currentLanguage) {
                        "bn" -> "২. অ্যাপের নাম বেছে নিন:"
                        "ar" -> "٢. اختر اسم التطبيق:"
                        "hi" -> "2. ऐप का नाम चुनें:"
                        "ur" -> "2. ایپ کا نام منتخب کریں:"
                        "es" -> "2. Elige el Nombre de la App:"
                        "ru" -> "2. Выберите название приложения:"
                        "zh" -> "2. 选择应用名称:"
                        "ja" -> "2. アプリの名前を選択:"
                        else -> "2. Choose App Name:"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                val nameScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(nameScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LauncherAliasManager.AVAILABLE_NAMES.forEachIndexed { index, name ->
                        val isSelected = chosenNameIndex == index
                        FilterChip(
                            selected = isSelected,
                            onClick = { chosenNameIndex = index },
                            label = { Text(name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCyan,
                                selectedLabelColor = DarkSurface,
                                containerColor = DarkSurfaceVariant,
                                labelColor = TextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                val selectedName = LauncherAliasManager.AVAILABLE_NAMES.getOrNull(chosenNameIndex) ?: "Iron Will"
                Button(
                    onClick = {
                        if (pinCode.isBlank()) {
                            showCreatePinDialogForIconChange = true
                        } else {
                            showPinDialogForIconChange = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = when (currentLanguage) {
                            "bn" -> "রূপ পরিবর্তন করুন ($selectedName + আইকন $chosenIconId)"
                            "ar" -> "تطبيق التمويه ($selectedName + أيقونة $chosenIconId)"
                            "hi" -> "भेष लागू करें ($selectedName + आइकन $chosenIconId)"
                            "ur" -> "روپ تبدیل کریں ($selectedName + آئیکن $chosenIconId)"
                            "es" -> "Aplicar Disfraz ($selectedName + Icono $chosenIconId)"
                            "ru" -> "Применить маскировку ($selectedName + Иконка $chosenIconId)"
                            "zh" -> "应用伪装 ($selectedName + 图标 $chosenIconId)"
                            "ja" -> "偽装を適用 ($selectedName + アイコン $chosenIconId)"
                            else -> "Apply Disguise ($selectedName + Icon $chosenIconId)"
                        },
                        color = DarkSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                        text = "App Language",
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

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = "Backup",
                        tint = PrimaryCyan
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = when (currentLanguage) {
                                "bn" -> "ব্যাকআপ ও রিস্টোর"
                                "ar" -> "النسخ الاحتياطي والاستعادة"
                                "hi" -> "बैकअप और रीस्टोर"
                                "ur" -> "بیک اپ اور ریسٹور"
                                "es" -> "Copia de Seguridad y Restauración"
                                "ru" -> "Резервное копирование и восстановление"
                                "zh" -> "备份与恢复"
                                "ja" -> "バックアップと復元"
                                else -> "Backup & Restore"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = when (currentLanguage) {
                                "bn" -> "অ্যাপের সব ডেটা JSON ফাইল হিসেবে ব্যাকআপ ও ইমপোর্ট করুন"
                                "ar" -> "تصدير واستيراد بيانات التطبيق في ملف JSON"
                                "hi" -> "JSON फ़ाइल के रूप में ऐप डेटा निर्यात और आयात करें"
                                "ur" -> "JSON فائل کے طور پر ایپ ڈیٹا ایکسپورٹ اور امپورٹ کریں"
                                "es" -> "Exporta e importa datos de la app en archivo JSON"
                                "ru" -> "Экспорт и импорт данных приложения в JSON"
                                "zh" -> "将应用数据导出和导入为 JSON 文件"
                                "ja" -> "アプリデータをJSONファイルでエクスポート・インポート"
                                else -> "Export and import app data in a JSON file"
                            },
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (pinCode.isBlank()) {
                                triggerExport()
                            } else {
                                pendingBackupAction = BackupActionType.EXPORT
                                showPinDialogForBackup = true
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FileUpload, contentDescription = "Export")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (currentLanguage) {
                                "bn" -> "এক্সপোর্ট (Export)"
                                "ar" -> "تصدير (Export)"
                                "hi" -> "निर्यात (Export)"
                                "ur" -> "ایکسپورٹ (Export)"
                                "es" -> "Exportar (Export)"
                                "ru" -> "Экспорт (Export)"
                                "zh" -> "导出 (Export)"
                                "ja" -> "エクスポート (Export)"
                                else -> "Export JSON"
                            },
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            if (pinCode.isBlank()) {
                                triggerImport()
                            } else {
                                pendingBackupAction = BackupActionType.IMPORT
                                showPinDialogForBackup = true
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = "Import")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (currentLanguage) {
                                "bn" -> "ইমপোর্ট (Import)"
                                "ar" -> "استيراد (Import)"
                                "hi" -> "आयात (Import)"
                                "ur" -> "امپورٹ (Import)"
                                "es" -> "Importar (Import)"
                                "ru" -> "Импорт (Import)"
                                "zh" -> "导入 (Import)"
                                "ja" -> "インポート (Import)"
                                else -> "Import JSON"
                            },
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SystemUpdate,
                            contentDescription = "App Update",
                            tint = PrimaryCyan
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = when (currentLanguage) {
                                    "bn" -> "অ্যাপ আপডেট"
                                    "ar" -> "تحديثات التطبيق"
                                    "hi" -> "ऐप अपडेट"
                                    "ur" -> "ایپ اپڈیٹس"
                                    "es" -> "Actualizaciones de App"
                                    "ru" -> "Обновления приложения"
                                    "zh" -> "应用更新"
                                    "ja" -> "アプリの更新"
                                    else -> "App Updates"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = when (currentLanguage) {
                                    "bn" -> "বর্তমান সংস্করণ: v$currentVersionName"
                                    "ar" -> "الإصدار الحالي: v$currentVersionName"
                                    "hi" -> "वर्तमान संस्करण: v$currentVersionName"
                                    "ur" -> "موجودہ ورژن: v$currentVersionName"
                                    "es" -> "Versión Actual: v$currentVersionName"
                                    "ru" -> "Текущая версия: v$currentVersionName"
                                    "zh" -> "当前版本: v$currentVersionName"
                                    "ja" -> "現在のバージョン: v$currentVersionName"
                                    else -> "Current Version: v$currentVersionName"
                                },
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.checkForUpdates(context) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = when (currentLanguage) {
                                "bn" -> "আপডেট চেক করুন"
                                "ar" -> "التحقق من التحديث"
                                "hi" -> "अपडेट जांचें"
                                "ur" -> "اپڈیٹ چیک کریں"
                                "es" -> "Buscar Actualización"
                                "ru" -> "Проверить"
                                "zh" -> "检查更新"
                                "ja" -> "更新を確認"
                                else -> "Check Update"
                            },
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                when (val status = updateStatus) {
                    is UpdateStatus.NewVersionAvailable -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    try {
                                        val targetUrl = status.releaseUrl.ifBlank { "https://github.com/mahadihasan9/IronWill/releases" }
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = PrimaryCyan.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = when (currentLanguage) {
                                            "bn" -> "নতুন সংস্করণ পাওয়া গেছে (${status.version})!"
                                            "ar" -> "يتوفر إصدار جديد (${status.version})!"
                                            "hi" -> "नया संस्करण उपलब्ध है (${status.version})!"
                                            "ur" -> "نیا ورژن دستیاب ہے (${status.version})!"
                                            "es" -> "¡Nueva versión disponible (${status.version})!"
                                            "ru" -> "Доступна новая версия (${status.version})!"
                                            "zh" -> "有新版本可用 (${status.version})!"
                                            "ja" -> "新しいバージョンが利用可能です (${status.version})!"
                                            else -> "New version available (${status.version})!"
                                        },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryCyan
                                    )
                                    Text(
                                        text = when (currentLanguage) {
                                            "bn" -> "GitHub releases থেকে ডাউনলোড করতে ট্যাপ করুন"
                                            "ar" -> "انقر للتنزيل من الإصدارات"
                                            "hi" -> "डाउनलोड करने के लिए टैप करें"
                                            "ur" -> "ڈاؤن لوڈ کرنے کے لیے ٹیپ کریں"
                                            "es" -> "Toca para descargar desde GitHub"
                                            "ru" -> "Нажмите для скачивания с GitHub"
                                            "zh" -> "点击前往 GitHub 下载"
                                            "ja" -> "GitHubからダウンロードするにはタップ"
                                            else -> "Tap to download from GitHub releases"
                                        },
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "Update",
                                    tint = PrimaryCyan
                                )
                            }
                        }
                    }
                    is UpdateStatus.LatestVersion -> {
                        Text(
                            text = when (currentLanguage) {
                                "bn" -> "আপনি সর্বশেষ সংস্করণ (v${status.version}) ব্যবহার করছেন।"
                                "ar" -> "أنت تستخدم أحدث إصدار (v${status.version})."
                                "hi" -> "आप नवीनतम संस्करण (v${status.version}) का उपयोग कर रहे हैं।"
                                "ur" -> "آپ تازہ ترین ورژن (v${status.version}) استعمال کر رہے ہیں۔"
                                "es" -> "Estás utilizando la última versión (v${status.version})."
                                "ru" -> "Вы используете последнюю версию (v${status.version})."
                                "zh" -> "您正在使用最新版本 (v${status.version})。"
                                "ja" -> "最新バージョン (v${status.version}) を使用しています。"
                                else -> "You are using the latest version (v${status.version})."
                            },
                            fontSize = 12.sp,
                            color = AccentGold,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    is UpdateStatus.Checking -> {
                        Text(
                            text = when (currentLanguage) {
                                "bn" -> "নতুন আপডেট চেক করা হচ্ছে..."
                                "ar" -> "جاري التحقق من التحديثات..."
                                "hi" -> "अपडेट की जांच की जा रही है..."
                                "ur" -> "اپڈیٹس چیک کی جا رہی ہیں..."
                                "es" -> "Comprobando actualizaciones..."
                                "ru" -> "Проверка обновлений..."
                                "zh" -> "正在检查更新..."
                                "ja" -> "更新を確認中..."
                                else -> "Checking for updates..."
                            },
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                    is UpdateStatus.Error -> {
                        Text(
                            text = when (currentLanguage) {
                                "bn" -> "আপডেট চেক করা সম্ভব হয়নি"
                                "ar" -> "تعذر التحقق من التحديثات"
                                "hi" -> "अपडेट की जांच नहीं की जा सकी"
                                "ur" -> "اپڈیٹ چیک نہیں ہو سکی"
                                "es" -> "No se pudo comprobar la actualización"
                                "ru" -> "Не удалось проверить обновления"
                                "zh" -> "无法检查更新"
                                "ja" -> "更新を確認できませんでした"
                                else -> "Could not check for update"
                            },
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                    else -> {}
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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

                val sourceCodeUrl = "https://github.com/mahadihasan9/IronWill"
                OutlinedButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sourceCodeUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Source Code",
                        tint = PrimaryCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (currentLanguage) {
                            "bn" -> "সোর্স কোড (Source Code)"
                            "ar" -> "الشفرة المصدرية (Source Code)"
                            "hi" -> "सोर्स कोड (Source Code)"
                            "ur" -> "سورس کوڈ (Source Code)"
                            "es" -> "Código Fuente (Source Code)"
                            "ru" -> "Исходный код (Source Code)"
                            "zh" -> "源代码 (Source Code)"
                            "ja" -> "ソースコード (Source Code)"
                            else -> "Source Code (GitHub)"
                        },
                        color = PrimaryCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val issueUrl = "https://github.com/mahadihasan9/IronWill/issues"
                OutlinedButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(issueUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = "Report Bug",
                        tint = PrimaryCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (currentLanguage) {
                            "bn" -> "বাগ রিপোর্ট / ইস্যু জানান (Bug Report)"
                            "ar" -> "الإبلاغ عن مشكلة (Report Issue)"
                            "hi" -> "समस्या की रिपोर्ट करें (Bug Report)"
                            "ur" -> "بگ رپورٹ (Report Issue)"
                            "es" -> "Reportar un Error (Bug Report)"
                            "ru" -> "Сообщить об ошибке (Report Bug)"
                            "zh" -> "报告问题 (Report Bug)"
                            "ja" -> "バグを報告 (Report Bug)"
                            else -> "Report an Issue / Bug"
                        },
                        color = PrimaryCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    if (showImportWarningDialog) {
        AlertDialog(
            onDismissRequest = { showImportWarningDialog = false },
            title = {
                Text(
                    text = when (currentLanguage) {
                        "bn" -> "ব্যাকআপ রিস্টোর করবেন?"
                        "ar" -> "استعادة النسخة الاحتياطية؟"
                        "hi" -> "क्या बैकअप रिस्टोर करें?"
                        "ur" -> "بیک اپ ریسٹور کریں؟"
                        "es" -> "¿Restaurar copia de seguridad?"
                        "ru" -> "Восстановить резервную копию?"
                        "zh" -> "恢复备份？"
                        "ja" -> "バックアップを復元しますか？"
                        else -> "Restore Backup?"
                    },
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = when (currentLanguage) {
                        "bn" -> "সতর্কতা: ব্যাকআপ রিস্টোর করলে বর্তমান সব কাউন্টার ও সেটিংস মুছে গিয়ে ব্যাকআপের ফাইল দ্বারা পরিবর্তিত হবে। আপনি কি নিশ্চিত?"
                        "ar" -> "تحذير: ستؤدي استعادة النسخة الاحتياطية إلى استبدال كافة عداداتك وإعداداتك الحالية. هل أنت تأكد؟"
                        "hi" -> "चेतावनी: बैकअप रिस्टोर करने से आपके वर्तमान सभी काउंटर और सेटिंग्स बदल जाएंगी। क्या आप सुनिश्चित हैं?"
                        "ur" -> "انتباہ: بیک اپ ریسٹور کرنے سے آپ کے موجودہ تمام کاؤنٹرز اور سیٹنگز تبدیل ہو جائیں گی۔ کیا آپ کو یقین ہے؟"
                        "es" -> "Advertencia: Restaurar una copia de seguridad sobrescribirá todos tus contadores y ajustes actuales. ¿Deseas continuar?"
                        "ru" -> "Предупреждение: восстановление резервной копии перезапишет все ваши текущие счетчики и настройки. Вы уверены?"
                        "zh" -> "警告：恢复备份将覆盖并替换您当前所有的计时器和设置。您确定要继续吗？"
                        "ja" -> "警告：バックアップを復元すると、現在のすべてのタイマーと設定が上書きされます。続行しますか？"
                        else -> "Warning: Restoring a backup will overwrite and replace all your current counters, streak history, and settings. Are you sure you want to proceed?"
                    },
                    color = TextMuted,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showImportWarningDialog = false
                        openDocumentLauncher.launch(arrayOf("application/json", "*/*"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text(
                        text = when (currentLanguage) {
                            "bn" -> "রিস্টোর করুন"
                            "ar" -> "استعادة"
                            "hi" -> "रिस्टोर करें"
                            "ur" -> "ریسٹور"
                            "es" -> "Restaurar"
                            "ru" -> "Восстановить"
                            "zh" -> "确认恢复"
                            "ja" -> "復元する"
                            else -> "Restore & Overwrite"
                        },
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportWarningDialog = false }) {
                    Text(
                        text = when (currentLanguage) {
                            "bn" -> "বাতিল"
                            "ar" -> "إلغاء"
                            "hi" -> "रद्द करें"
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

    if (showPinDialogForBackup) {
        PinVerificationDialog(
            correctPin = pinCode,
            appLanguage = currentLanguage,
            subtitle = when (currentLanguage) {
                "bn" -> "ব্যাকআপ অপশন ব্যবহার করতে পিন লিখুন:"
                "ar" -> "أدخل الرمز لاستخدام خيار النسخ الاحتياطي:"
                "hi" -> "बैकअप विकल्प का उपयोग करने के लिए पिन दर्ज करें:"
                "ur" -> "بیک اپ کا استعمال کرنے کے لیے پن درج کریں:"
                "es" -> "Introduce el PIN para la copia de seguridad:"
                "ru" -> "Введите PIN для использования резервного копирования:"
                "zh" -> "输入 PIN 码以使用备份功能:"
                "ja" -> "バックアップ機能を使用するにはPINを入力してください:"
                else -> "Enter PIN for Backup options:"
            },
            onDismiss = { showPinDialogForBackup = false },
            onPinSuccess = {
                showPinDialogForBackup = false
                if (pendingBackupAction == BackupActionType.EXPORT) {
                    triggerExport()
                } else {
                    triggerImport()
                }
            }
        )
    }

    if (showCreatePinDialogForIconChange) {
        CreatePinDialog(
            appLanguage = currentLanguage,
            onDismiss = { showCreatePinDialogForIconChange = false },
            onPinCreated = { newPin ->
                viewModel.setPinCode(newPin)
                showCreatePinDialogForIconChange = false
                viewModel.applyLauncherDisguise(context, chosenIconId, chosenNameIndex)
                Toast.makeText(context, "App Disguise Updated", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showPinDialogForIconChange) {
        PinVerificationDialog(
            correctPin = pinCode,
            appLanguage = currentLanguage,
            subtitle = when (currentLanguage) {
                "bn" -> "রূপ পরিবর্তন করতে পিন লিখুন:"
                "ar" -> "أدخل الرمز لتغيير تمويه التطبيق:"
                "hi" -> "ऐप का भेष बदलने के लिए पिन दर्ज करें:"
                "ur" -> "ایپ کا روپ تبدیل کرنے کے لیے پن درج کریں:"
                "es" -> "Introduce el PIN para cambiar el disfraz:"
                "ru" -> "Введите PIN для смены маскировки:"
                "zh" -> "输入 PIN 码以更改应用伪装:"
                "ja" -> "偽装を変更するにはPINを入力してください:"
                else -> "Enter PIN to change App Disguise:"
            },
            onDismiss = { showPinDialogForIconChange = false },
            onPinSuccess = {
                showPinDialogForIconChange = false
                viewModel.applyLauncherDisguise(context, chosenIconId, chosenNameIndex)
                Toast.makeText(context, when (currentLanguage) {
                    "bn" -> "অ্যাপ ডিসগাইজ আপডেট হয়েছে"
                    "ar" -> "تم تحديث تمويه التطبيق"
                    "hi" -> "ऐप का भेष अपडेट किया गया"
                    "ur" -> "ایپ کی سیکیورٹی تبدیل ہو گئی"
                    "es" -> "Disfraz de la App Actualizado"
                    "ru" -> "Маскировка приложения обновлена"
                    "zh" -> "应用伪装已更新"
                    "ja" -> "アプリの偽装を更新しました"
                    else -> "App Disguise Updated"
                }, Toast.LENGTH_SHORT).show()
            }
        )
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
            appLanguage = currentLanguage,
            subtitle = when (currentLanguage) {
                "bn" -> "অ্যাপ লক সেটিং পরিবর্তন করতে পিন লিখুন:"
                "ar" -> "أدخل الرمز لتغيير إعداد قفل التطبيق:"
                "hi" -> "ऐप लॉक सेटिंग बदलने के लिए पिन दर्ज करें:"
                "ur" -> "ایپ لاک کی سیٹنگ تبدیل کرنے کے لیے پن درج کریں:"
                "es" -> "Introduce el PIN para cambiar el bloqueo de la app:"
                "ru" -> "Введите PIN для изменения настроек блокировки:"
                "zh" -> "输入 PIN 码以更改应用锁设置:"
                "ja" -> "アプリのロック設定を変更するにはPINを入力してください:"
                else -> "Enter PIN to change App Lock setting:"
            },
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
            appLanguage = currentLanguage,
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
    appLanguage: String = "en",
    onDismiss: () -> Unit,
    onSavePin: (newPin: String) -> Unit
) {
    var oldPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val isOldPinNeeded = currentPin.isNotBlank()

    val oldPinFocusRequester = remember { FocusRequester() }
    val newPinFocusRequester = remember { FocusRequester() }
    val confirmPinFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        if (isOldPinNeeded) {
            oldPinFocusRequester.requestFocus()
        } else {
            newPinFocusRequester.requestFocus()
        }
        keyboardController?.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isOldPinNeeded) {
                    when (appLanguage) {
                        "bn" -> "৪-ডিজিট পিন পরিবর্তন করুন"
                        "ar" -> "تغيير رمز الأمان المكون من 4 أرقام"
                        "hi" -> "4-अंकीय पिन बदलें"
                        "ur" -> "4 ہندسوں کا پن تبدیل کریں"
                        "es" -> "Cambiar PIN de 4 Dígitos"
                        "ru" -> "Изменить 4-значный PIN"
                        "zh" -> "修改 4位数 PIN 码"
                        "ja" -> "4桁のPINを変更"
                        else -> "Change 4-Digit PIN"
                    }
                } else {
                    when (appLanguage) {
                        "bn" -> "৪-ডিজিট পিন সেট করুন"
                        "ar" -> "تعيين رمز أمان من 4 أرقام"
                        "hi" -> "4-अंकीय पिन सेट करें"
                        "ur" -> "4 ہندسوں کا پن سیٹ کریں"
                        "es" -> "Establecer PIN de 4 Dígitos"
                        "ru" -> "Установите 4-значный PIN"
                        "zh" -> "设置 4位数 PIN 码"
                        "ja" -> "4桁のPINを設定"
                        else -> "Set 4-Digit PIN"
                    }
                },
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isOldPinNeeded) {
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "বর্তমান পিন লিখুন:"
                            "ar" -> "أدخل الرمز الحالي:"
                            "hi" -> "वर्तमान पिन दर्ज करें:"
                            "ur" -> "موجودہ پن درج کریں:"
                            "es" -> "Introduce el PIN Actual:"
                            "ru" -> "Введите текущий PIN:"
                            "zh" -> "输入当前 PIN 码:"
                            "ja" -> "現在のPINを入力:"
                            else -> "Enter Current PIN:"
                        },
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    PinBoxInput(
                        pinValue = oldPinInput,
                        onPinChange = {
                            oldPinInput = it
                            errorMessage = ""
                            if (it.length == 4) {
                                if (it != currentPin) {
                                    errorMessage = when (appLanguage) {
                                        "bn" -> "বর্তমান পিনটি সঠিক নয়"
                                        "ar" -> "الرمز الحالي غير صحيح"
                                        "hi" -> "वर्तमान पिन गलत है"
                                        "ur" -> "موجودہ پن غلط ہے"
                                        "es" -> "El PIN actual es incorrecto"
                                        "ru" -> "Текущий PIN неверный"
                                        "zh" -> "当前 PIN 码不正确"
                                        "ja" -> "現在のPINが正しくありません"
                                        else -> "Current PIN is incorrect"
                                    }
                                    oldPinInput = ""
                                } else {
                                    newPinFocusRequester.requestFocus()
                                }
                            }
                        },
                        focusRequester = oldPinFocusRequester
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                Text(
                    text = when (appLanguage) {
                        "bn" -> "নতুন ৪-ডিজিট পিন লিখুন:"
                        "ar" -> "أدخل الرمز الجديد من 4 أرقام:"
                        "hi" -> "नया 4-अंकीय पिन दर्ज करें:"
                        "ur" -> "نیا 4 ہندسوں کا پن درج کریں:"
                        "es" -> "Introduce el Nuevo PIN de 4 Dígitos:"
                        "ru" -> "Введите новый 4-значный PIN:"
                        "zh" -> "输入新 4位数 PIN 码:"
                        "ja" -> "新しい4桁のPINを入力:"
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
                        "es" -> "Confirmar Nuevo PIN de 4 Dígitos:"
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
                    if (isOldPinNeeded && oldPinInput != currentPin) {
                        errorMessage = when (appLanguage) {
                            "bn" -> "বর্তমান পিনটি সঠিক নয়"
                            "ar" -> "الرمز الحالي غير صحيح"
                            "hi" -> "वर्तमान पिन गलत है"
                            "ur" -> "موجودہ پن غلط ہے"
                            "es" -> "El PIN actual es incorrecto"
                            "ru" -> "Текущий PIN неверный"
                            "zh" -> "当前 PIN 码不正确"
                            "ja" -> "現在のPINが正しくありません"
                            else -> "Current PIN is incorrect"
                        }
                        oldPinInput = ""
                    } else if (newPinInput.length != 4) {
                        errorMessage = when (appLanguage) {
                            "bn" -> "নতুন পিন অবশ্যই ৪ সংখ্যার হতে হবে"
                            "ar" -> "يجب أن يتكون الرمز الجديد من 4 أرقام"
                            "hi" -> "नया पिन 4 अंकों का होना चाहिए"
                            "ur" -> "نیا پن 4 ہندسوں کا ہونا ضروری ہے"
                            "es" -> "El nuevo PIN debe tener 4 dígitos"
                            "ru" -> "Новый PIN должен состоять из 4 цифр"
                            "zh" -> "新 PIN 码必须为 4 位数字"
                            "ja" -> "新しいPINは4桁で指定してください"
                            else -> "New PIN must be 4 digits"
                        }
                    } else if (newPinInput != confirmPinInput) {
                        errorMessage = when (appLanguage) {
                            "bn" -> "নতুন পিন দুটি মিলছে না"
                            "ar" -> "الرمز الجديد غير متطابق"
                            "hi" -> "नए पिन मेल नहीं खा रहे हैं"
                            "ur" -> "نئے پن میچ نہیں کر رہے"
                            "es" -> "Los nuevos PIN no coinciden"
                            "ru" -> "Новые PIN-коды не совпадают"
                            "zh" -> "两次输入的新 PIN 码不一致"
                            "ja" -> "新しいPINが一致しません"
                            else -> "New PINs do not match"
                        }
                    } else {
                        onSavePin(newPinInput)
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
