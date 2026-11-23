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

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class StreakHistory(
    val id: Long = System.currentTimeMillis(),
    val startTimestamp: Long,
    val endTimestamp: Long,
    val note: String = ""
) {
    val durationMillis: Long
        get() = (endTimestamp - startTimestamp).coerceAtLeast(0)

    val durationDays: Int
        get() = (durationMillis / (1000 * 60 * 60 * 24)).toInt()

    val durationHours: Int
        get() = ((durationMillis / (1000 * 60 * 60)) % 24).toInt()

    val durationMinutes: Int
        get() = ((durationMillis / (1000 * 60)) % 60).toInt()

    fun getFormattedStartDate(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        return sdf.format(Date(startTimestamp))
    }

    fun getFormattedEndDate(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        return sdf.format(Date(endTimestamp))
    }

    fun getFormattedDuration(): String {
        return when {
            durationDays > 0 -> "$durationDays d $durationHours h $durationMinutes m"
            durationHours > 0 -> "$durationHours h $durationMinutes m"
            else -> "$durationMinutes mins"
        }
    }
}
