package org.ironman.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import org.ironman.R

data class AppIconPreset(
    val id: Int, // 1 to 9
    val title: String,
    val iconResId: Int
)

object LauncherAliasManager {

    val AVAILABLE_ICONS = listOf(
        AppIconPreset(1, "Icon 1", R.drawable.app_icon_1),
        AppIconPreset(2, "Icon 2", R.drawable.app_icon_2),
        AppIconPreset(3, "Icon 3", R.drawable.app_icon_3),
        AppIconPreset(4, "Icon 4", R.drawable.app_icon_4),
        AppIconPreset(5, "Icon 5", R.drawable.app_icon_5),
        AppIconPreset(6, "Icon 6", R.drawable.app_icon_6),
        AppIconPreset(7, "Icon 7", R.drawable.app_icon_7),
        AppIconPreset(8, "Icon 8", R.drawable.app_icon_8),
        AppIconPreset(9, "Icon 9", R.drawable.app_icon_9)
    )

    val AVAILABLE_NAMES = listOf(
        "Iron Will",
        "Calculator",
        "Diary",
        "Clock",
        "Timer",
        "My App",
        "Notes",
        "Fitness",
        "Calendar"
    )

    fun applyDisguise(context: Context, iconId: Int, nameIndex: Int) {
        val safeIcon = iconId.coerceIn(1, 9)
        val safeName = (nameIndex + 1).coerceIn(1, 9)
        val targetAlias = "org.ironman.MainActivityAlias_Icon${safeIcon}_Name${safeName}"

        val pm = context.packageManager
        val packageName = context.packageName

        // Enable target alias first using DONT_KILL_APP to prevent process termination
        try {
            pm.setComponentEnabledSetting(
                ComponentName(packageName, targetAlias),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Disable other aliases using DONT_KILL_APP
        for (i in 1..9) {
            for (j in 1..9) {
                val alias = "org.ironman.MainActivityAlias_Icon${i}_Name${j}"
                if (alias != targetAlias) {
                    try {
                        val currentState = pm.getComponentEnabledSetting(ComponentName(packageName, alias))
                        if (currentState != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                            pm.setComponentEnabledSetting(
                                ComponentName(packageName, alias),
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
