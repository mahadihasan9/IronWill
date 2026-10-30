package org.ironman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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

enum class NavigationTab(val titleEn: String, val titleBn: String, val titleAr: String, val icon: ImageVector) {
    COUNTER("Counter", "কাউন্টার", "العداد", Icons.Default.Timer),
    HISTORY("History", "হিস্ট্রি", "السجل", Icons.Default.History),
    STATS("Stats", "পরিসংখ্যান", "الإحصائيات", Icons.Default.BarChart),
    SETTINGS("Settings", "সেটিংস", "الإعدادات", Icons.Default.Settings);

    fun getTitle(lang: String): String {
        return when (lang) {
            "bn" -> titleBn
            "ar" -> titleAr
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

    var isUnlockedBySession by remember { mutableStateOf(false) }

    if (!isUnlockedBySession) {
        AppLockOverlay(
            correctPin = pinCode,
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
