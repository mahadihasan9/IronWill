package org.ironman.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "iron_will_settings")

class StreakRepository(private val context: Context) {

    companion object {
        private val KEY_START_TIMESTAMP = longPreferencesKey("start_timestamp")
        private val KEY_HISTORY_DATA = stringPreferencesKey("history_data")
        private val KEY_TARGET_GOAL = longPreferencesKey("target_goal_days")
        private val KEY_PIN_CODE = stringPreferencesKey("pin_code")
        private val KEY_PIN_ENABLED = booleanPreferencesKey("pin_enabled")
        private val KEY_APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        private val KEY_LANGUAGE = stringPreferencesKey("app_language")
        private val KEY_TRACKING_STATUS = booleanPreferencesKey("is_tracking")
        private val KEY_SELECTED_ICON_ID = intPreferencesKey("selected_icon_id")
        private val KEY_SELECTED_NAME_INDEX = intPreferencesKey("selected_name_index")

        private val KEY_TIMERS_JSON = stringPreferencesKey("multi_timers_json")
        private val KEY_SELECTED_TIMER_ID = stringPreferencesKey("selected_timer_id")
    }

    val selectedTimerIdFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_SELECTED_TIMER_ID] ?: "main"
    }

    val timersListFlow: Flow<List<StreakTimer>> = context.dataStore.data.map { preferences ->
        val rawJson = preferences[KEY_TIMERS_JSON] ?: ""
        if (rawJson.isBlank()) {
            // Migrate existing single-timer data or initialize default Main Timer
            val mainStart = preferences[KEY_START_TIMESTAMP] ?: System.currentTimeMillis()
            val mainTracking = preferences[KEY_TRACKING_STATUS] ?: false
            val mainHistoryRaw = preferences[KEY_HISTORY_DATA] ?: ""
            val mainHistory = parseHistoryData(mainHistoryRaw)

            val mainTimer = StreakTimer(
                id = "main",
                name = "Main Counter",
                startTimestamp = mainStart,
                isTracking = mainTracking,
                historyList = mainHistory
            )
            listOf(mainTimer)
        } else {
            parseTimersJson(rawJson)
        }
    }

    val pinCodeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_PIN_CODE] ?: ""
    }

    val pinEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_PIN_ENABLED] ?: true
    }

    val appLockEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_APP_LOCK_ENABLED] ?: false
    }

    val languageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_LANGUAGE] ?: "en"
    }

    val selectedIconIdFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_SELECTED_ICON_ID] ?: 1
    }

    val selectedNameIndexFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_SELECTED_NAME_INDEX] ?: 0
    }

    suspend fun selectTimer(id: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SELECTED_TIMER_ID] = id
        }
    }

    suspend fun addNewTimer(timerName: String): Boolean {
        val trimmedName = timerName.trim().take(50)
        if (trimmedName.isBlank()) return false

        var success = false
        context.dataStore.edit { preferences ->
            val currentTimers = getOrCreateTimersList(preferences).toMutableList()
            if (currentTimers.size < 30) {
                val newId = "timer_${System.currentTimeMillis()}"
                val newTimer = StreakTimer(
                    id = newId,
                    name = trimmedName,
                    startTimestamp = System.currentTimeMillis(),
                    isTracking = false,
                    historyList = emptyList()
                )
                currentTimers.add(newTimer)
                preferences[KEY_TIMERS_JSON] = serializeTimersList(currentTimers)
                preferences[KEY_SELECTED_TIMER_ID] = newId
                success = true
            }
        }
        return success
    }

    suspend fun deleteTimer(timerId: String) {
        if (timerId == "main") return // Main timer cannot be deleted
        context.dataStore.edit { preferences ->
            val currentTimers = getOrCreateTimersList(preferences).filterNot { it.id == timerId }
            preferences[KEY_TIMERS_JSON] = serializeTimersList(currentTimers)
            val activeId = preferences[KEY_SELECTED_TIMER_ID] ?: "main"
            if (activeId == timerId) {
                preferences[KEY_SELECTED_TIMER_ID] = "main"
            }
        }
    }

    suspend fun startStreak(timerId: String, timestamp: Long) {
        context.dataStore.edit { preferences ->
            val currentTimers = getOrCreateTimersList(preferences).map { timer ->
                if (timer.id == timerId) {
                    timer.copy(startTimestamp = timestamp, isTracking = true)
                } else timer
            }
            preferences[KEY_TIMERS_JSON] = serializeTimersList(currentTimers)
            if (timerId == "main") {
                preferences[KEY_START_TIMESTAMP] = timestamp
                preferences[KEY_TRACKING_STATUS] = true
            }
        }
    }

    suspend fun saveStartTimestamp(timerId: String, timestamp: Long) {
        context.dataStore.edit { preferences ->
            val currentTimers = getOrCreateTimersList(preferences).map { timer ->
                if (timer.id == timerId) {
                    timer.copy(startTimestamp = timestamp)
                } else timer
            }
            preferences[KEY_TIMERS_JSON] = serializeTimersList(currentTimers)
            if (timerId == "main") {
                preferences[KEY_START_TIMESTAMP] = timestamp
            }
        }
    }

    suspend fun resetStreak(timerId: String, note: String = "") {
        val now = System.currentTimeMillis()
        context.dataStore.edit { preferences ->
            val currentTimers = getOrCreateTimersList(preferences).map { timer ->
                if (timer.id == timerId) {
                    if (timer.isTracking) {
                        val newHistoryItem = StreakHistory(
                            id = now,
                            startTimestamp = timer.startTimestamp,
                            endTimestamp = now,
                            note = note.replace("\n", " ").replace(";", ",")
                        )
                        val updatedHistory = listOf(newHistoryItem) + timer.historyList
                        timer.copy(isTracking = false, historyList = updatedHistory)
                    } else {
                        timer.copy(isTracking = false)
                    }
                } else timer
            }
            preferences[KEY_TIMERS_JSON] = serializeTimersList(currentTimers)
            if (timerId == "main") {
                preferences[KEY_TRACKING_STATUS] = false
            }
        }
    }

    suspend fun deleteHistoryItem(timerId: String, historyId: Long) {
        context.dataStore.edit { preferences ->
            val currentTimers = getOrCreateTimersList(preferences).map { timer ->
                if (timer.id == timerId) {
                    val filteredHistory = timer.historyList.filterNot { it.id == historyId }
                    timer.copy(historyList = filteredHistory)
                } else timer
            }
            preferences[KEY_TIMERS_JSON] = serializeTimersList(currentTimers)
        }
    }

    suspend fun clearAllHistory(timerId: String) {
        context.dataStore.edit { preferences ->
            val currentTimers = getOrCreateTimersList(preferences).map { timer ->
                if (timer.id == timerId) {
                    timer.copy(historyList = emptyList())
                } else timer
            }
            preferences[KEY_TIMERS_JSON] = serializeTimersList(currentTimers)
        }
    }

    suspend fun setPinCode(pin: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PIN_CODE] = pin
            preferences[KEY_PIN_ENABLED] = true
        }
    }

    suspend fun setPinEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PIN_ENABLED] = true
        }
    }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_APP_LOCK_ENABLED] = enabled
        }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE] = lang
        }
    }

    suspend fun saveDisguiseSelection(iconId: Int, nameIndex: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SELECTED_ICON_ID] = iconId
            preferences[KEY_SELECTED_NAME_INDEX] = nameIndex
        }
    }

    private fun getOrCreateTimersList(preferences: Preferences): List<StreakTimer> {
        val rawJson = preferences[KEY_TIMERS_JSON] ?: ""
        if (rawJson.isBlank()) {
            val mainStart = preferences[KEY_START_TIMESTAMP] ?: System.currentTimeMillis()
            val mainTracking = preferences[KEY_TRACKING_STATUS] ?: false
            val mainHistoryRaw = preferences[KEY_HISTORY_DATA] ?: ""
            val mainHistory = parseHistoryData(mainHistoryRaw)

            val mainTimer = StreakTimer(
                id = "main",
                name = "Main Counter",
                startTimestamp = mainStart,
                isTracking = mainTracking,
                historyList = mainHistory
            )
            return listOf(mainTimer)
        }
        return parseTimersJson(rawJson)
    }

    private fun parseTimersJson(rawJson: String): List<StreakTimer> {
        val list = mutableListOf<StreakTimer>()
        try {
            val jsonArray = JSONArray(rawJson)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.optJSONObject(i)
                if (item != null) {
                    list.add(StreakTimer.fromJson(item))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (list.none { it.id == "main" }) {
            list.add(0, StreakTimer("main", "Main Counter"))
        }
        return list
    }

    private fun serializeTimersList(list: List<StreakTimer>): String {
        val jsonArray = JSONArray()
        list.forEach { timer ->
            jsonArray.put(timer.toJson())
        }
        return jsonArray.toString()
    }

    private fun parseHistoryData(raw: String): List<StreakHistory> {
        if (raw.isBlank()) return emptyList()
        return raw.split("\n").mapNotNull { line ->
            val parts = line.split(";")
            if (parts.size >= 3) {
                val id = parts[0].toLongOrNull() ?: return@mapNotNull null
                val start = parts[1].toLongOrNull() ?: return@mapNotNull null
                val end = parts[2].toLongOrNull() ?: return@mapNotNull null
                val note = if (parts.size >= 4) parts[3] else ""
                StreakHistory(id, start, end, note)
            } else null
        }
    }
}
