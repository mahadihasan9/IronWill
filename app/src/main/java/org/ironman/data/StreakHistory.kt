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
