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
