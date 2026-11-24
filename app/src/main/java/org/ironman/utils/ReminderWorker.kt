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

package org.ironman.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.ironman.data.RankSystem
import org.ironman.data.StreakDatabaseHelper

class ReminderWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val helper = StreakDatabaseHelper(applicationContext)
        return try {
            val selectedTimerId = helper.getSetting("selected_timer_id", "main")
            val timers = helper.getAllTimers()
            val timer = timers.find { it.id == selectedTimerId } ?: timers.firstOrNull()
            if (timer != null && timer.isTracking) {
                val elapsed = (System.currentTimeMillis() - timer.startTimestamp).coerceAtLeast(0L)
                val progress = RankSystem.getRankProgress(elapsed)
                val language = helper.getSetting("app_language", "en")
                val nameIndex = helper.getSetting("selected_name_index", "0").toIntOrNull() ?: 0
                val displayName = LauncherAliasManager.AVAILABLE_NAMES.getOrElse(nameIndex) { "Iron Will" }
                val days = progress.remainingMillis / DAY_MILLIS
                val hours = (progress.remainingMillis / HOUR_MILLIS) % 24L
                val message = buildMessage(language, progress.nextRankTitle, days, hours)
                ReminderScheduler.showNotification(applicationContext, displayName, message)
                ReminderScheduler.scheduleNext(applicationContext)
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        } finally {
            helper.close()
        }
    }

    private fun buildMessage(language: String, rank: String, days: Long, hours: Long): String {
        val hasDays = days > 0L
        return when (language) {
            "bn" -> if (hasDays) {
                "পরবর্তী Rank ($rank) অর্জন করতে আর মাত্র $days দিন $hours ঘণ্টা বাকি! হাল ছাড়বেন না।"
            } else {
                "পরবর্তী Rank ($rank) অর্জন করতে আর মাত্র $hours ঘণ্টা বাকি! হাল ছাড়বেন না।"
            }
            "ar" -> if (hasDays) {
                "باقي $days يوم و$hours ساعة فقط للوصول إلى الرتبة التالية ($rank)! لا تستسلم."
            } else {
                "باقي $hours ساعة فقط للوصول إلى الرتبة التالية ($rank)! لا تستسلم."
            }
            "hi" -> if (hasDays) {
                "अगली रैंक ($rank) तक पहुंचने में केवल $days दिन $hours घंटे बाकी! हार मत मानो।"
            } else {
                "अगली रैंक ($rank) तक पहुंचने में केवल $hours घंटे बाकी! हार मत मानो।"
            }
            "ur" -> if (hasDays) {
                "اگلی رینک ($rank) تک پہنچنے میں صرف $days دن $hours گھنٹے باقی! ہمت نہ ہاریں۔"
            } else {
                "اگلی رینک ($rank) تک پہنچنے میں صرف $hours گھنٹے باقی! ہمت نہ ہاریں۔"
            }
            "es" -> if (hasDays) {
                "¡Solo quedan ${days}d ${hours}h para alcanzar el siguiente Rango ($rank)! No te rindas."
            } else {
                "¡Solo quedan ${hours}h para alcanzar el siguiente Rango ($rank)! No te rindas."
            }
            "ru" -> if (hasDays) {
                "До следующего ранга ($rank) осталось всего $days д. $hours ч.! Не сдавайтесь."
            } else {
                "До следующего ранга ($rank) осталось всего $hours ч.! Не сдавайтесь."
            }
            "zh" -> if (hasDays) {
                "距离下一等级（$rank）仅剩 $days 天 $hours 小时！不要放弃。"
            } else {
                "距离下一等级（$rank）仅剩 $hours 小时！不要放弃。"
            }
            "ja" -> if (hasDays) {
                "次のランク（$rank）まであと $days 日 $hours 時間！諦めないで。"
            } else {
                "次のランク（$rank）まであと $hours 時間！諦めないで。"
            }
            else -> if (hasDays) {
                "Only ${days}d ${hours}h remaining to reach next Rank ($rank)! Don't give up."
            } else {
                "Only ${hours}h remaining to reach next Rank ($rank)! Don't give up."
            }
        }
    }

    private companion object {
        const val HOUR_MILLIS = 60L * 60L * 1000L
        const val DAY_MILLIS = 24L * HOUR_MILLIS
    }
}
