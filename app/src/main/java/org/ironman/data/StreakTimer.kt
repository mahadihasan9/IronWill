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

import org.json.JSONArray
import org.json.JSONObject

data class StreakTimer(
    val id: String,
    val name: String,
    val startTimestamp: Long = System.currentTimeMillis(),
    val isTracking: Boolean = false,
    val historyList: List<StreakHistory> = emptyList()
) {
    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("name", name)
        json.put("startTimestamp", startTimestamp)
        json.put("isTracking", isTracking)

        val historyArray = JSONArray()
        historyList.forEach { history ->
            val hJson = JSONObject()
            hJson.put("id", history.id)
            hJson.put("startTimestamp", history.startTimestamp)
            hJson.put("endTimestamp", history.endTimestamp)
            hJson.put("note", history.note)
            historyArray.put(hJson)
        }
        json.put("historyList", historyArray)
        return json
    }

    companion object {
        fun fromJson(json: JSONObject): StreakTimer {
            val id = json.optString("id", "main")
            val name = json.optString("name", "Main Counter")
            val startTimestamp = json.optLong("startTimestamp", System.currentTimeMillis())
            val isTracking = json.optBoolean("isTracking", false)

            val historyList = mutableListOf<StreakHistory>()
            val historyArray = json.optJSONArray("historyList")
            if (historyArray != null) {
                for (i in 0 until historyArray.length()) {
                    val hJson = historyArray.optJSONObject(i)
                    if (hJson != null) {
                        val hId = hJson.optLong("id", 0L)
                        val hStart = hJson.optLong("startTimestamp", 0L)
                        val hEnd = hJson.optLong("endTimestamp", 0L)
                        val hNote = hJson.optString("note", "")
                        if (hId != 0L) {
                            historyList.add(StreakHistory(hId, hStart, hEnd, hNote))
                        }
                    }
                }
            }
            return StreakTimer(id, name, startTimestamp, isTracking, historyList)
        }
    }
}
