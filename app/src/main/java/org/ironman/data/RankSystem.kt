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

package org.ironman.data

data class RankInfo(
    val title: String,
    val minDays: Int,
    val nextRankTitle: String,
    val daysToNextRank: Int
)

data class RankProgress(
    val nextRankTitle: String,
    val remainingMillis: Long
)

object RankSystem {

    private const val DAY_MILLIS = 24L * 60L * 60L * 1000L

    fun getRankInfo(currentDays: Long): RankInfo = when {
        currentDays < 1 -> RankInfo("Novice", 0, "Iron Mind", (1 - currentDays).toInt())
        currentDays < 3 -> RankInfo("Bronze Will", 1, "Iron Will", (3 - currentDays).toInt())
        currentDays < 7 -> RankInfo("Iron Will", 3, "Steel Mind", (7 - currentDays).toInt())
        currentDays < 14 -> RankInfo("Steel Mind", 7, "Unstoppable", (14 - currentDays).toInt())
        currentDays < 30 -> RankInfo("Unstoppable", 14, "Titan", (30 - currentDays).toInt())
        currentDays < 90 -> RankInfo("Titan", 30, "Iron Legend", (90 - currentDays).toInt())
        else -> RankInfo("Iron Legend", 90, "Master of Will", (365 - currentDays).toInt())
    }

    fun getRankProgress(elapsedMillis: Long): RankProgress {
        val safeElapsed = elapsedMillis.coerceAtLeast(0L)
        val currentDays = safeElapsed / DAY_MILLIS
        val info = getRankInfo(currentDays)
        val nextThresholdDays = when {
            currentDays < 1 -> 1
            currentDays < 3 -> 3
            currentDays < 7 -> 7
            currentDays < 14 -> 14
            currentDays < 30 -> 30
            currentDays < 90 -> 90
            else -> 365
        }
        val remainingMillis = (nextThresholdDays * DAY_MILLIS - safeElapsed).coerceAtLeast(0L)
        return RankProgress(info.nextRankTitle, remainingMillis)
    }
}
