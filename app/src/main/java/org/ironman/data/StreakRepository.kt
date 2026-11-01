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
    }

    val startTimestampFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[KEY_START_TIMESTAMP] ?: System.currentTimeMillis()
    }

    val isTrackingFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_TRACKING_STATUS] ?: false
    }

    val historyListFlow: Flow<List<StreakHistory>> = context.dataStore.data.map { preferences ->
        val rawData = preferences[KEY_HISTORY_DATA] ?: ""
        parseHistoryData(rawData)
    }

    val targetGoalDaysFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_TARGET_GOAL]?.toInt() ?: 30
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

    suspend fun startStreak(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_START_TIMESTAMP] = timestamp
            preferences[KEY_TRACKING_STATUS] = true
        }
    }

    suspend fun saveStartTimestamp(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_START_TIMESTAMP] = timestamp
        }
    }

    suspend fun setTargetGoalDays(days: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TARGET_GOAL] = days.toLong()
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

    suspend fun resetStreak(note: String = "") {
        val now = System.currentTimeMillis()
        context.dataStore.edit { preferences ->
            val isTracking = preferences[KEY_TRACKING_STATUS] ?: false
            val currentStart = preferences[KEY_START_TIMESTAMP] ?: now
            val currentRawHistory = preferences[KEY_HISTORY_DATA] ?: ""

            // Save to history if streak was actively tracking
            if (isTracking) {
                val newHistoryItem = StreakHistory(
                    id = now,
                    startTimestamp = currentStart,
                    endTimestamp = now,
                    note = note.replace("\n", " ").replace(";", ",")
                )
                val currentList = parseHistoryData(currentRawHistory).toMutableList()
                currentList.add(0, newHistoryItem) // newest first
                preferences[KEY_HISTORY_DATA] = serializeHistoryData(currentList)
            }

            preferences[KEY_TRACKING_STATUS] = false
        }
    }

    suspend fun deleteHistoryItem(id: Long) {
        context.dataStore.edit { preferences ->
            val currentRawHistory = preferences[KEY_HISTORY_DATA] ?: ""
            val list = parseHistoryData(currentRawHistory).filterNot { it.id == id }
            preferences[KEY_HISTORY_DATA] = serializeHistoryData(list)
        }
    }

    suspend fun clearAllHistory() {
        context.dataStore.edit { preferences ->
            preferences[KEY_HISTORY_DATA] = ""
        }
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

    private fun serializeHistoryData(list: List<StreakHistory>): String {
        return list.joinToString("\n") { "${it.id};${it.startTimestamp};${it.endTimestamp};${it.note}" }
    }
}
