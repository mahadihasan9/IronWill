/*
 * Iron Will - Streak & Habit Tracker
 * Copyright (C) 2026 Mahadi Hasan <islammdmahadi943@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.ironman

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import org.ironman.ui.components.AppLockOverlay
import org.ironman.ui.screens.HistoryScreen
import org.ironman.ui.screens.HomeScreen
import org.ironman.ui.screens.SettingsScreen
import org.ironman.ui.screens.StatsScreen
import org.ironman.ui.theme.DarkSurface
import org.ironman.ui.theme.IronWillTheme
import org.ironman.ui.theme.PrimaryCyan
import org.ironman.ui.theme.TextMuted
import org.ironman.ui.viewmodel.MainViewModel
import org.ironman.utils.AutoBackupManager
import org.ironman.utils.LauncherAliasManager
import org.ironman.utils.ReminderScheduler

enum class NavigationTab(
    val titleEn: String,
    val titleBn: String,
    val titleAr: String,
    val titleHi: String,
    val titleUr: String,
    val titleEs: String,
    val titleRu: String,
    val titleZh: String,
    val titleJa: String,
    val icon: ImageVector
) {
    COUNTER("Counter", "কাউন্টার", "العداد", "काउंटर", "کاؤنٹر", "Contador", "Счетчик", "计时器", "カウンター", Icons.Default.Timer),
    HISTORY("History", "হিস্ট্রি", "السجل", "إعادة", "تاریخ", "Historial", "История", "历史", "履歴", Icons.Default.History),
    STATS("Stats", "পরিসংখ্যান", "الإحصائيات", "आंकड़े", "شماریات", "Estadísticas", "Статистика", "统计", "統計", Icons.Default.BarChart),
    SETTINGS("Settings", "সেটিংস", "الإعدادات", "सेटिंग्स", "سیٹنگز", "Ajustes", "Настройки", "设置", "設定", Icons.Default.Settings);

    fun getTitle(lang: String): String {
        return when (lang) {
            "bn" -> titleBn
            "ar" -> titleAr
            "hi" -> titleHi
            "ur" -> titleUr
            "es" -> titleEs
            "ru" -> titleRu
            "zh" -> titleZh
            "ja" -> titleJa
            else -> titleEn
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IronWillTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(mainViewModel: MainViewModel = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val appLanguage by mainViewModel.appLanguage.collectAsState()
    val pinCode by mainViewModel.pinCode.collectAsState()
    val appLockEnabled by mainViewModel.appLockEnabled.collectAsState()
    val selectedNameIndex by mainViewModel.selectedNameIndex.collectAsState()
    val isTracking by mainViewModel.isTracking.collectAsState()
    val context = LocalContext.current

    var isUnlockedBySession by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {}

    val disguiseName = LauncherAliasManager.AVAILABLE_NAMES
        .getOrElse(selectedNameIndex) { LauncherAliasManager.AVAILABLE_NAMES.first() }

    LaunchedEffect(Unit) {
        AutoBackupManager.requestBackupFromSettings(context)
    }

    LaunchedEffect(isTracking) {
        ReminderScheduler.sync(context, isTracking)
        if (isTracking && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (appLockEnabled && pinCode.isNotBlank() && !isUnlockedBySession) {
        AppLockOverlay(
            correctPin = pinCode,
            appLanguage = appLanguage,
            disguiseName = disguiseName,
            onSaveInitialPin = { newPin ->
                mainViewModel.setPinCode(newPin)
            },
            onUnlockSuccess = {
                isUnlockedBySession = true
            }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = DarkSurface
                ) {
                    NavigationTab.entries.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = { Icon(imageVector = tab.icon, contentDescription = tab.getTitle(appLanguage)) },
                            label = { Text(text = tab.getTitle(appLanguage)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryCyan,
                                selectedTextColor = PrimaryCyan,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = DarkSurface
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            when (selectedTab) {
                0 -> HomeScreen(viewModel = mainViewModel, modifier = modifier)
                1 -> HistoryScreen(viewModel = mainViewModel, modifier = modifier)
                2 -> StatsScreen(viewModel = mainViewModel, modifier = modifier)
                3 -> SettingsScreen(viewModel = mainViewModel, modifier = modifier)
            }
        }
    }
}
