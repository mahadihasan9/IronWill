package org.ironman.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import org.ironman.data.BackupCryptoManager
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

    var pendingExportBytes by remember { mutableStateOf<ByteArray?>(null) }
    var pendingRawImportBytes by remember { mutableStateOf<ByteArray?>(null) }
    var pendingExportTime by remember { mutableLongStateOf(0L) }
    var showImportOptionsDialog by remember { mutableStateOf(false) }
    var showDecryptPinDialog by remember { mutableStateOf(false) }
    var selectedBackupBytes by remember { mutableStateOf<ByteArray?>(null) }

    val processImportBytes = { bytes: ByteArray, passPin: String, onComplete: ((Boolean) -> Unit)? ->
        viewModel.readBackupInfo(bytes, passPin) { info ->
            if (info != null) {
                pendingRawImportBytes = info.rawDbBytes
                pendingExportTime = info.exportTime
                showImportOptionsDialog = true
                showDecryptPinDialog = false
                onComplete?.invoke(true)
            } else {
                onComplete?.invoke(false)
            }
        }
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        uri?.let { targetUri ->
            pendingExportBytes?.let { bytes ->
                try {
                    context.contentResolver.openOutputStream(targetUri)?.use { os ->
                        os.write(bytes)
                    }
                    Toast.makeText(context, when (currentLanguage) {
                        "bn" -> "এনক্রিপ্টেড ব্যাকআপ ফাইল (.db) এক্সপোর্ট সফল হয়েছে"
                        "ar" -> "تم تصدير ملف النسخ الاحتياطي المشفر بنجاح"
                        "hi" -> "एनक्रिप्टेड बैकअप फ़ाइल (.db) सफलतापूर्वक निर्यात की गई"
                        "ur" -> "انکرپٹڈ بیک اپ فائل ایکسپورٹ ہو گئی"
                        "es" -> "Copia de seguridad encriptada exportada con éxito"
                        "ru" -> "Зашифрованная резервная копия успешно экспортирована"
                        "zh" -> "加密备份文件 (.db) 导出成功"
                        "ja" -> "暗号化されたバックアップファイル (.db) がエクスポートされました"
                        else -> "Encrypted backup file (.db) exported successfully"
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
                val bytes = context.contentResolver.openInputStream(sourceUri)?.use { it.readBytes() }
                if (bytes != null && bytes.isNotEmpty()) {
                    selectedBackupBytes = bytes
                    if (BackupCryptoManager.isEncryptedBackup(bytes)) {
                        viewModel.readBackupInfo(bytes, pinCode) { info ->
                            if (info != null) {
                                pendingRawImportBytes = info.rawDbBytes
                                pendingExportTime = info.exportTime
                                showImportOptionsDialog = true
                            } else {
                                showDecryptPinDialog = true
                            }
                        }
                    } else {
                        processImportBytes(bytes, "", null)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val triggerExport = {
        viewModel.exportEncryptedBackup(pinCode) { bytes ->
            pendingExportBytes = bytes
            createDocumentLauncher.launch("iron_will_backup.db")
        }
    }

    val triggerImport = {
        openDocumentLauncher.launch(arrayOf("application/octet-stream", "application/x-sqlite3", "*/*"))
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
                            "ja" -> "伪装を適用 ($selectedName + アイコン $chosenIconId)"
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
                                "bn" -> "ব্যাকআপ ও রিস্টোর (Encrypted SQLite DB)"
                                "ar" -> "النسخ الاحتياطي والاستعادة المشفرة"
                                "hi" -> "एनक्रिप्टेड बैकअप और रीस्टोर"
                                "ur" -> "انکرپٹڈ بیک اپ اور ریسٹور"
                                "es" -> "Copia de Seguridad Encriptada y Restauración"
                                "ru" -> "Зашифрованное резервное копирование"
                                "zh" -> "加密 SQLite 数据库备份与恢复"
                                "ja" -> "暗号化SQLite DBバックアップと復元"
                                else -> "Encrypted SQLite Backup & Restore"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = when (currentLanguage) {
                                "bn" -> "অ্যাপের সব ডাটা এনক্রিপ্টেড SQLite ডাটাবেস (.db) ফাইল হিসেবে ব্যাকআপ ও ইমপোর্ট করুন"
                                "ar" -> "تصدير واستيراد بيانات التطبيق في ملف قاعدة بيانات SQLite مشفر (.db)"
                                "hi" -> "एनक्रिप्टेड SQLite डेटाबेस (.db) फ़ाइल के रूप में ऐप डेटा निर्यात और आयात करें"
                                "ur" -> "انکرپٹڈ SQLite ڈیٹا بیس (.db) فائل کے طور پر ایپ ڈیٹا ایکسپورٹ اور امپورٹ کریں"
                                "es" -> "Exporta e importa datos de la app en archivo SQLite encriptado (.db)"
                                "ru" -> "Экспорт и импорт данных приложения в зашифрованном файле SQLite (.db)"
                                "zh" -> "将应用数据导出和导入为加密的 SQLite 数据库 (.db) 文件"
                                "ja" -> "アプリデータを暗号化されたSQLite DBファイル(.db)でエクスポート・インポート"
                                else -> "Export and import app data in an encrypted SQLite database (.db) file"
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
                                "bn" -> "এক্সপোর্ট (.db)"
                                "ar" -> "تصدير (.db)"
                                "hi" -> "निर्यात (.db)"
                                "ur" -> "ایکسپورٹ (.db)"
                                "es" -> "Exportar (.db)"
                                "ru" -> "Экспорт (.db)"
                                "zh" -> "导出 (.db)"
                                "ja" -> "エクスポート (.db)"
                                else -> "Export (.db)"
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
                                "bn" -> "ইমপোর্ট (.db)"
                                "ar" -> "استيراد (.db)"
                                "hi" -> "आयात (.db)"
                                "ur" -> "امپورٹ (.db)"
                                "es" -> "Importar (.db)"
                                "ru" -> "Импорт (.db)"
                                "zh" -> "导入 (.db)"
                                "ja" -> "インポート (.db)"
                                else -> "Import (.db)"
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

    if (showDecryptPinDialog && selectedBackupBytes != null) {
        BackupDecryptPinDialog(
            appLanguage = currentLanguage,
            onDismiss = {
                showDecryptPinDialog = false
                selectedBackupBytes = null
            },
            onPinSubmitted = { enteredPin, onResult ->
                val bytes = selectedBackupBytes!!
                processImportBytes(bytes, enteredPin) { success ->
                    onResult(success)
                }
            }
        )
    }

    if (showImportOptionsDialog && pendingRawImportBytes != null) {
        ImportOptionsDialog(
            exportTime = pendingExportTime,
            appLanguage = currentLanguage,
            onDismiss = {
                showImportOptionsDialog = false
                pendingRawImportBytes = null
            },
            onConfirmImport = { adjustTimestamps ->
                val rawBytes = pendingRawImportBytes!!
                showImportOptionsDialog = false
                pendingRawImportBytes = null
                viewModel.importBackupDb(rawBytes, adjustTimestamps, pendingExportTime) { success ->
                    if (success) {
                        Toast.makeText(context, when (currentLanguage) {
                            "bn" -> "এনক্রিপ্টেড ব্যাকআপ ডাটাবেস রিস্টোর সফল হয়েছে"
                            "ar" -> "تمت استعادة قاعدة البيانات المشفرة بنجاح"
                            "hi" -> "एनक्रिप्टेड डेटाबेस रिस्टोर सफल हुआ"
                            "ur" -> "انکرپٹڈ ڈیٹا بیس ریسٹور کامیاب رہا"
                            "es" -> "Base de datos encriptada restaurada con éxito"
                            "ru" -> "Зашифрованная база данных успешно восстановлена"
                            "zh" -> "加密数据库恢复成功"
                            "ja" -> "暗号化データベースが復元されました"
                            else -> "Encrypted database backup restored successfully"
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
fun BackupDecryptPinDialog(
    appLanguage: String = "en",
    onDismiss: () -> Unit,
    onPinSubmitted: (enteredPin: String, onResult: (Boolean) -> Unit) -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val displayTitle = when (appLanguage) {
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

    val displaySubtitle = when (appLanguage) {
        "bn" -> "এই ব্যাকআপ ফাইলটি খুলতে ৪-ডিজিট পিন দিন:"
        "ar" -> "أدخل الرمز المكون من 4 أرقام لفتح هذا الملف:"
        "hi" -> "इस बैकअप फ़ाइल को खोलने के लिए 4-अंकीय पिन दर्ज करें:"
        "ur" -> "یہ بیک اپ کھولنے کے لیے 4 ہندسوں کا پن درج کریں:"
        "es" -> "Introduce el PIN de 4 dígitos para abrir la copia:"
        "ru" -> "Введите 4-значный PIN для открытия этой резервной копии:"
        "zh" -> "解密该备份文件所需的 4位数 PIN 码:"
        "ja" -> "このバックアップを開くための4桁のPINを入力してください:"
        else -> "Enter 4-digit PIN to decrypt this backup file:"
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    val verifyAndSubmit = { targetPin: String ->
        if (targetPin.length == 4 && !isLoading) {
            isLoading = true
            isError = false
            onPinSubmitted(targetPin) { success ->
                isLoading = false
                if (!success) {
                    isError = true
                    pinInput = ""
                }
            }
        }
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
                            verifyAndSubmit(it)
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
                onClick = { verifyAndSubmit(pinInput) },
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                enabled = !isLoading
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
                        "hi" -> "রদ্দ करें"
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

@Composable
fun ImportOptionsDialog(
    exportTime: Long,
    appLanguage: String,
    onDismiss: () -> Unit,
    onConfirmImport: (adjustTimestamps: Boolean) -> Unit
) {
    var adjustTimestampsChoice by remember { mutableStateOf(true) }

    val formattedExportDate = remember(exportTime) {
        if (exportTime > 0) {
            try {
                val sdf = java.text.SimpleDateFormat("MMM dd, yyyy - hh:mm a", java.util.Locale.getDefault())
                sdf.format(java.util.Date(exportTime))
            } catch (e: Exception) {
                null
            }
        } else null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when (appLanguage) {
                    "bn" -> "ব্যাকআপ রিস্টোর অপশন"
                    "ar" -> "خيارات استعادة النسخة الاحتياطية"
                    "hi" -> "बैकअप रिस्टोर विकल्प"
                    "ur" -> "بیک اپ ریسٹور کی آپشنز"
                    "es" -> "Opciones de Restauración"
                    "ru" -> "Параметры восстановления"
                    "zh" -> "恢复备份选项"
                    "ja" -> "バックアップ復元オプション"
                    else -> "Restore Backup Options"
                },
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "সতর্কতা: ব্যাকআপ রিস্টোর করলে বর্তমান সব কাউন্টার ও সেটিংস মুছে গিয়ে ব্যাকআপ ফাইল দ্বারা পরিবর্তিত হবে।"
                        "ar" -> "تحذير: ستؤدي استعادة النسخة الاحتياطية إلى استبدال كافة عداداتك وإعداداتك الحالية."
                        "hi" -> "चेतावनी: बैकअप रिस्टोर करने से आपके वर्तमान सभी काउंटर और सेटिंग्स बदल जाएंगी।"
                        "ur" -> "انتباہ: بیک اپ ریسٹور کرنے سے آپ کے موجودہ تمام کاؤنٹرز اور سیٹنگز تبدیل ہو جائیں گی۔"
                        "es" -> "Advertencia: Restaurar una copia de seguridad sobrescribirá todos tus contadores y ajustes actuales."
                        "ru" -> "Предупреждение: восстановление резервной копии перезапишет все ваши текущие счетчики и настройки."
                        "zh" -> "警告：恢复备份将覆盖并替换您当前所有的计时器和设置。"
                        "ja" -> "警告：バックアップを復元すると、現在のすべてのタイマーと設定が上書きされます。"
                        else -> "Warning: Restoring a backup will overwrite and replace all your current counters, history, and settings."
                    },
                    color = AccentRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                if (formattedExportDate != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "ব্যাকআপ তৈরির সময়: $formattedExportDate"
                            "ar" -> "تاريخ إنشاء النسخة: $formattedExportDate"
                            "hi" -> "बैकअप बनाने का समय: $formattedExportDate"
                            "ur" -> "بیک اپ بنانے کا وقت: $formattedExportDate"
                            "es" -> "Copia creada el: $formattedExportDate"
                            "ru" -> "Создано: $formattedExportDate"
                            "zh" -> "备份创建时间: $formattedExportDate"
                            "ja" -> "バックアップ作成日時: $formattedExportDate"
                            else -> "Backup created on: $formattedExportDate"
                        },
                        color = PrimaryCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = when (appLanguage) {
                        "bn" -> "চলমান স্ট্রিকের সময় কীভাবে রিস্টোর করা হবে?"
                        "ar" -> "كيف ينبغي استعادة العدادات النشطة؟"
                        "hi" -> "सक्रिय स्ट्रीक टाइमर कैसे रिस्टोर किए जाएं?"
                        "ur" -> "ایکٹو اسٹریک ٹائمر کیسے ریسٹور کیے جائیں؟"
                        "es" -> "¿Cómo se deben restaurar los contadores activos?"
                        "ru" -> "Как восстановить активные таймеры?"
                        "zh" -> "如何恢复正在运行的计时器？"
                        "ja" -> "アクティブなタイマーをどのように復元しますか？"
                        else -> "How should active streak timers be restored?"
                    },
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { adjustTimestampsChoice = true },
                    colors = CardDefaults.cardColors(
                        containerColor = if (adjustTimestampsChoice) PrimaryCyan.copy(alpha = 0.15f) else DarkSurfaceVariant
                    ),
                    border = BorderStroke(
                        width = if (adjustTimestampsChoice) 1.5.dp else 0.5.dp,
                        color = if (adjustTimestampsChoice) PrimaryCyan else TextMuted.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        RadioButton(
                            selected = adjustTimestampsChoice,
                            onClick = { adjustTimestampsChoice = true },
                            colors = RadioButtonDefaults.colors(selectedColor = PrimaryCyan)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = when (appLanguage) {
                                    "bn" -> "মধ্যবর্তী সময় বাদ দিন (টাইম অ্যাডজাস্ট)"
                                    "ar" -> "خصم الوقت المنقضي (تعديل الوقت)"
                                    "hi" -> "बीच का समय घटाएं (समय समायोजित करें)"
                                    "ur" -> "درمیانی وقت منہا کریں (وقت ایڈجسٹ)"
                                    "es" -> "Restar tiempo transcurrido (Ajustar)"
                                    "ru" -> "Вычесть прошедшее время (Настройка)"
                                    "zh" -> "扣除中间间隔时间（调整时间）"
                                    "ja" -> "経過時間を差し引く（時間を調整）"
                                    else -> "Subtract elapsed gap time (Adjust)"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = when (appLanguage) {
                                    "bn" -> "ব্যাকআপ নেওয়ার পর থেকে বর্তমান সময় পর্যন্ত গ্যাপ বাদ দিবে। ব্যাকআপের সময়ের স্ট্রিক দিন অপরিবর্তিত থাকবে।"
                                    "ar" -> "يستثني الفجوة بين التصدير والآن. يحافظ على مدة العداد عند التصدير."
                                    "hi" -> "बैकअप और अब के बीच के समय को हटाता है। बैकअप के समय की स्ट्रीक बनी रहेगी।"
                                    "ur" -> "بیک اپ اور اب کے درمیان کا وقت منہا کرے گا۔ بیک اپ کے وقت والی اسٹریک قائم رہے گی۔"
                                    "es" -> "Excluye el tiempo transcurrido. Mantiene la duración exacta del momento de la copia."
                                    "ru" -> "Исключает время с момента экспорта. Сохраняет длительность на момент экспорта."
                                    "zh" -> "排除从导出至今的时间。保持导出时的连续天数不变。"
                                    "ja" -> "バックアップ作成からの経過時間を除外します。作成時の継続時間を維持します。"
                                    else -> "Excludes time elapsed since backup. Preserves exact streak duration from when backup was taken."
                                },
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { adjustTimestampsChoice = false },
                    colors = CardDefaults.cardColors(
                        containerColor = if (!adjustTimestampsChoice) PrimaryCyan.copy(alpha = 0.15f) else DarkSurfaceVariant
                    ),
                    border = BorderStroke(
                        width = if (!adjustTimestampsChoice) 1.5.dp else 0.5.dp,
                        color = if (!adjustTimestampsChoice) PrimaryCyan else TextMuted.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        RadioButton(
                            selected = !adjustTimestampsChoice,
                            onClick = { adjustTimestampsChoice = false },
                            colors = RadioButtonDefaults.colors(selectedColor = PrimaryCyan)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = when (appLanguage) {
                                    "bn" -> "মূল শুরুর সময় ঠিক রাখুন (মাঝের সময়সহ)"
                                    "ar" -> "الحفاظ على وقت البدء الأصلي (تضمين)"
                                    "hi" -> "मूल प्रारंभ समय बनाए रखें (समय शामिल)"
                                    "ur" -> "اصل شروع کا وقت برقرار رکھیں (وقت شامل)"
                                    "es" -> "Mantener hora de inicio original (Incluir)"
                                    "ru" -> "Сохранить исходное время начала (Включая)"
                                    "zh" -> "保留原始开始时间（包含间隔）"
                                    "ja" -> "元の開始日時を維持（経過時間を含む）"
                                    else -> "Keep original start time (Include gap)"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = when (appLanguage) {
                                    "bn" -> "মাঝের সময় বাদ দিবে না। মূল শুরুর তারিখ থেকেই অবিরত স্ট্রিক টাইম গণনা হতে থাকবে।"
                                    "ar" -> "لا يخصم الوقت المنقضي. يستمر العداد بدون انقطاع من تاريخ البدء الأصلي."
                                    "hi" -> "बीच का समय नहीं घटाता है। मूल प्रारंभ तिथि से स्ट्रीक जारी रहेगी।"
                                    "ur" -> "درمیانی وقت منہا نہیں کرے گا۔ اصل تاریخ سے اسٹریک جاری رہے گی۔"
                                    "es" -> "No resta el tiempo transcurrido. El contador continúa desde la fecha original."
                                    "ru" -> "Не вычитает прошедшее время. Таймер продолжается с исходной даты."
                                    "zh" -> "不扣除间隔时间。计时器将从最初开始日期连续计算。"
                                    "ja" -> "経過時間を差し引きません。元の開始日時から継続してカウントします。"
                                    else -> "Does not subtract elapsed time. Streak timer continues uninterrupted from original start date."
                                },
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmImport(adjustTimestampsChoice) },
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
            ) {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "রিস্টোর করুন"
                        "ar" -> "استعادة"
                        "hi" -> "रिस्टोर करें"
                        "ur" -> "ریسٹور"
                        "es" -> "Restaurar"
                        "ru" -> "Восстановить"
                        "zh" -> "确认恢复"
                        "ja" -> "復元する"
                        else -> "Restore Data"
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
                        "hi" -> "রদ্দ करें"
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
