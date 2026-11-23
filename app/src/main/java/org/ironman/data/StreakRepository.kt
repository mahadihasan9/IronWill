package org.ironman.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "iron_will_settings")

data class BackupInfo(
    val exportTime: Long,
    val timersCount: Int,
    val rawDbBytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BackupInfo
        if (exportTime != other.exportTime) return false
        if (timersCount != other.timersCount) return false
        if (!rawDbBytes.contentEquals(other.rawDbBytes)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = exportTime.hashCode()
        result = 31 * result + timersCount
        result = 31 * result + rawDbBytes.contentHashCode()
        return result
    }
}

class StreakRepository(private val context: Context) {

    private val dbHelper = StreakDatabaseHelper(context)
    private val dbChangeTrigger = MutableStateFlow(System.currentTimeMillis())

    init {
        CoroutineScope(Dispatchers.IO).launch {
            migrateLegacyDataStoreIfNeeded()
        }
    }

    private fun notifyChanged() {
        dbChangeTrigger.value = System.currentTimeMillis()
    }

    val selectedTimerIdFlow: Flow<String> = dbChangeTrigger.map {
        dbHelper.getSetting("selected_timer_id", "main")
    }

    val timersListFlow: Flow<List<StreakTimer>> = dbChangeTrigger.map {
        dbHelper.getAllTimers()
    }

    val pinCodeFlow: Flow<String> = dbChangeTrigger.map {
        dbHelper.getSetting("pin_code", "")
    }

    val pinEnabledFlow: Flow<Boolean> = dbChangeTrigger.map {
        dbHelper.getSetting("pin_enabled", "true").toBoolean()
    }

    val appLockEnabledFlow: Flow<Boolean> = dbChangeTrigger.map {
        dbHelper.getSetting("app_lock_enabled", "false").toBoolean()
    }

    val languageFlow: Flow<String> = dbChangeTrigger.map {
        dbHelper.getSetting("app_language", "en")
    }

    val selectedIconIdFlow: Flow<Int> = dbChangeTrigger.map {
        dbHelper.getSetting("selected_icon_id", "1").toIntOrNull() ?: 1
    }

    val selectedNameIndexFlow: Flow<Int> = dbChangeTrigger.map {
        dbHelper.getSetting("selected_name_index", "0").toIntOrNull() ?: 0
    }

    suspend fun selectTimer(id: String) {
        dbHelper.setSetting("selected_timer_id", id)
        notifyChanged()
    }

    suspend fun addNewTimer(timerName: String): Boolean {
        val trimmedName = timerName.trim().take(50)
        if (trimmedName.isBlank()) return false

        val currentTimers = dbHelper.getAllTimers()
        if (currentTimers.size >= 30) return false

        val newId = "timer_${System.currentTimeMillis()}"
        val newTimer = StreakTimer(
            id = newId,
            name = trimmedName,
            startTimestamp = System.currentTimeMillis(),
            isTracking = false,
            historyList = emptyList()
        )
        dbHelper.saveTimer(newTimer)
        dbHelper.setSetting("selected_timer_id", newId)
        notifyChanged()
        return true
    }

    suspend fun deleteTimer(timerId: String) {
        if (timerId == "main") return
        dbHelper.deleteTimer(timerId)
        val activeId = dbHelper.getSetting("selected_timer_id", "main")
        if (activeId == timerId) {
            dbHelper.setSetting("selected_timer_id", "main")
        }
        notifyChanged()
    }

    suspend fun startStreak(timerId: String, timestamp: Long) {
        val timers = dbHelper.getAllTimers()
        val timer = timers.find { it.id == timerId } ?: return
        val updated = timer.copy(startTimestamp = timestamp, isTracking = true)
        dbHelper.saveTimer(updated)
        notifyChanged()
    }

    suspend fun resetStreak(timerId: String, resetTimestamp: Long = System.currentTimeMillis(), note: String = "") {
        val timers = dbHelper.getAllTimers()
        val timer = timers.find { it.id == timerId } ?: return
        val now = resetTimestamp

        if (timer.isTracking) {
            val endTs = if (now >= timer.startTimestamp) now else System.currentTimeMillis()
            val newHistoryItem = StreakHistory(
                id = System.currentTimeMillis(),
                startTimestamp = timer.startTimestamp,
                endTimestamp = endTs,
                note = note.replace("\n", " ").replace(";", ",")
            )
            dbHelper.addHistoryItem(timerId, newHistoryItem)
            val updatedTimer = timer.copy(isTracking = false)
            dbHelper.saveTimer(updatedTimer)
        } else {
            val updatedTimer = timer.copy(isTracking = false)
            dbHelper.saveTimer(updatedTimer)
        }
        notifyChanged()
    }

    suspend fun deleteHistoryItem(timerId: String, historyId: Long) {
        dbHelper.deleteHistoryItem(historyId)
        notifyChanged()
    }

    suspend fun clearAllHistory(timerId: String) {
        dbHelper.clearAllHistory(timerId)
        notifyChanged()
    }

    suspend fun setPinCode(pin: String) {
        dbHelper.setSetting("pin_code", pin)
        dbHelper.setSetting("pin_enabled", "true")
        notifyChanged()
    }

    suspend fun setPinEnabled(enabled: Boolean) {
        dbHelper.setSetting("pin_enabled", enabled.toString())
        notifyChanged()
    }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        dbHelper.setSetting("app_lock_enabled", enabled.toString())
        notifyChanged()
    }

    suspend fun setLanguage(lang: String) {
        dbHelper.setSetting("app_language", lang)
        notifyChanged()
    }

    suspend fun setTargetGoalDays(days: Int) {
        dbHelper.setSetting("target_goal_days", days.toString())
        notifyChanged()
    }

    suspend fun saveStartTimestamp(timerId: String, timestamp: Long) {
        val timers = dbHelper.getAllTimers()
        val timer = timers.find { it.id == timerId } ?: return
        val updated = timer.copy(startTimestamp = timestamp)
        dbHelper.saveTimer(updated)
        notifyChanged()
    }

    suspend fun saveDisguiseSelection(iconId: Int, nameIndex: Int) {
        dbHelper.setSetting("selected_icon_id", iconId.toString())
        dbHelper.setSetting("selected_name_index", nameIndex.toString())
        notifyChanged()
    }

    // --- Encrypted SQLite Database Backup & Restore ---

    suspend fun exportEncryptedDbBackup(pin: String): ByteArray {
        val settingsMap = mutableMapOf<String, String>()
        val keys = listOf(
            "pin_code", "pin_enabled", "app_lock_enabled", "app_language",
            "selected_icon_id", "selected_name_index", "selected_timer_id", "target_goal_days"
        )
        keys.forEach { key ->
            settingsMap[key] = dbHelper.getSetting(key, "")
        }
        val exportTime = System.currentTimeMillis()
        settingsMap["export_time"] = exportTime.toString()

        val timersList = dbHelper.getAllTimers()

        val tempDbFile = java.io.File(context.cacheDir, "temp_export_${System.currentTimeMillis()}.db")
        if (tempDbFile.exists()) tempDbFile.delete()

        try {
            val tempDbHelper = StreakDatabaseHelperTest(context, tempDbFile.absolutePath)
            tempDbHelper.replaceAllData(settingsMap, timersList)
            tempDbHelper.close()

            val rawDbBytes = tempDbFile.readBytes()
            tempDbFile.delete()

            return BackupCryptoManager.encryptDatabase(rawDbBytes, pin)
        } catch (e: Exception) {
            e.printStackTrace()
            if (tempDbFile.exists()) tempDbFile.delete()
            return ByteArray(0)
        }
    }

    suspend fun readBackupInfo(encryptedOrRawBytes: ByteArray, pin: String): BackupInfo? {
        if (encryptedOrRawBytes.isEmpty()) return null

        val rawDbBytes: ByteArray? = if (BackupCryptoManager.isEncryptedBackup(encryptedOrRawBytes)) {
            BackupCryptoManager.decryptDatabase(encryptedOrRawBytes, pin)
        } else {
            encryptedOrRawBytes
        }

        if (rawDbBytes == null || rawDbBytes.isEmpty()) return null

        val tempDbFile = java.io.File(context.cacheDir, "temp_read_${System.currentTimeMillis()}.db")
        if (tempDbFile.exists()) tempDbFile.delete()

        return try {
            tempDbFile.writeBytes(rawDbBytes)
            val tempDbHelper = StreakDatabaseHelperTest(context, tempDbFile.absolutePath)
            val exportTime = tempDbHelper.getSetting("export_time", "0").toLongOrNull() ?: System.currentTimeMillis()
            val timers = tempDbHelper.getAllTimers()
            tempDbHelper.close()
            tempDbFile.delete()

            BackupInfo(exportTime, timers.size, rawDbBytes)
        } catch (e: Exception) {
            e.printStackTrace()
            if (tempDbFile.exists()) tempDbFile.delete()
            null
        }
    }

    suspend fun importBackupDb(rawDbBytes: ByteArray, adjustTimestamps: Boolean, exportTime: Long): Boolean {
        if (rawDbBytes.isEmpty()) return false

        val tempDbFile = java.io.File(context.cacheDir, "temp_import_${System.currentTimeMillis()}.db")
        if (tempDbFile.exists()) tempDbFile.delete()

        return try {
            tempDbFile.writeBytes(rawDbBytes)
            val tempDbHelper = StreakDatabaseHelperTest(context, tempDbFile.absolutePath)

            val settingsMap = mutableMapOf<String, String>()
            val keys = listOf(
                "pin_code", "pin_enabled", "app_lock_enabled", "app_language",
                "selected_icon_id", "selected_name_index", "selected_timer_id", "target_goal_days"
            )
            keys.forEach { key ->
                val v = tempDbHelper.getSetting(key, "")
                if (v.isNotBlank()) {
                    settingsMap[key] = v
                }
            }

            val now = System.currentTimeMillis()
            val importedTimers = tempDbHelper.getAllTimers()

            val adjustedTimers = importedTimers.map { timer ->
                if (timer.isTracking && adjustTimestamps) {
                    val originalDuration = (exportTime - timer.startTimestamp).coerceAtLeast(0L)
                    val adjustedStartTimestamp = now - originalDuration
                    timer.copy(startTimestamp = adjustedStartTimestamp)
                } else {
                    timer
                }
            }

            tempDbHelper.close()
            tempDbFile.delete()

            dbHelper.replaceAllData(settingsMap, adjustedTimers)
            notifyChanged()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            if (tempDbFile.exists()) tempDbFile.delete()
            false
        }
    }

    // Migration helper from old DataStore to SQLite
    private suspend fun migrateLegacyDataStoreIfNeeded() {
        try {
            val pref = context.dataStore.data.first()
            val hasDataStoreData = pref[longPreferencesKey("start_timestamp")] != null ||
                    pref[stringPreferencesKey("pin_code")] != null ||
                    pref[stringPreferencesKey("multi_timers_json")] != null

            val hasSqliteData = dbHelper.getSetting("migrated_from_datastore", "false") == "true"

            if (hasDataStoreData && !hasSqliteData) {
                pref[stringPreferencesKey("pin_code")]?.let { dbHelper.setSetting("pin_code", it) }
                pref[booleanPreferencesKey("pin_enabled")]?.let { dbHelper.setSetting("pin_enabled", it.toString()) }
                pref[booleanPreferencesKey("app_lock_enabled")]?.let { dbHelper.setSetting("app_lock_enabled", it.toString()) }
                pref[stringPreferencesKey("app_language")]?.let { dbHelper.setSetting("app_language", it) }
                pref[intPreferencesKey("selected_icon_id")]?.let { dbHelper.setSetting("selected_icon_id", it.toString()) }
                pref[intPreferencesKey("selected_name_index")]?.let { dbHelper.setSetting("selected_name_index", it.toString()) }
                pref[stringPreferencesKey("selected_timer_id")]?.let { dbHelper.setSetting("selected_timer_id", it) }

                val rawTimersJson = pref[stringPreferencesKey("multi_timers_json")] ?: ""
                if (rawTimersJson.isNotBlank()) {
                    val parsedTimers = parseTimersJson(rawTimersJson)
                    parsedTimers.forEach { timer ->
                        dbHelper.saveTimer(timer)
                        timer.historyList.forEach { history ->
                            dbHelper.addHistoryItem(timer.id, history)
                        }
                    }
                }
                dbHelper.setSetting("migrated_from_datastore", "true")
                notifyChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseTimersJson(rawJson: String): List<StreakTimer> {
        val list = mutableListOf<StreakTimer>()
        try {
            val jsonArray = org.json.JSONArray(rawJson)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.optJSONObject(i)
                if (item != null) {
                    list.add(StreakTimer.fromJson(item))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}

// Inner Helper class for reading/writing temporary database files
private class StreakDatabaseHelperTest(context: Context, private val dbPath: String) :
    android.database.sqlite.SQLiteOpenHelper(context, dbPath, null, 1) {

    override fun onCreate(db: android.database.sqlite.SQLiteDatabase) {
        db.execSQL("CREATE TABLE settings (\"key\" TEXT PRIMARY KEY, \"value\" TEXT)")
        db.execSQL("CREATE TABLE timers (\"id\" TEXT PRIMARY KEY, \"name\" TEXT NOT NULL, \"start_timestamp\" INTEGER NOT NULL, \"is_tracking\" INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE history (\"id\" INTEGER PRIMARY KEY, \"timer_id\" TEXT NOT NULL, \"start_timestamp\" INTEGER NOT NULL, \"end_timestamp\" INTEGER NOT NULL, \"note\" TEXT NOT NULL DEFAULT '')")
    }

    override fun onUpgrade(db: android.database.sqlite.SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun getSetting(key: String, defaultValue: String): String {
        val db = readableDatabase
        val cursor = db.query("settings", arrayOf("value"), "\"key\" = ?", arrayOf(key), null, null, null)
        cursor.use {
            if (it.moveToFirst()) {
                val valueIndex = it.getColumnIndex("value")
                if (valueIndex != -1) {
                    return it.getString(valueIndex) ?: defaultValue
                }
            }
        }
        return defaultValue
    }

    fun getAllTimers(): List<StreakTimer> {
        val db = readableDatabase
        val timers = mutableListOf<StreakTimer>()
        val cursor = db.query("timers", arrayOf("id", "name", "start_timestamp", "is_tracking"), null, null, null, null, null)
        cursor.use { c ->
            val idIdx = c.getColumnIndex("id")
            val nameIdx = c.getColumnIndex("name")
            val startIdx = c.getColumnIndex("start_timestamp")
            val trackIdx = c.getColumnIndex("is_tracking")
            while (c.moveToNext()) {
                val id = c.getString(idIdx)
                val name = c.getString(nameIdx)
                val startTs = c.getLong(startIdx)
                val isTracking = c.getInt(trackIdx) == 1
                val history = getHistoryForTimer(id)
                timers.add(StreakTimer(id, name, startTs, isTracking, history))
            }
        }
        return timers
    }

    private fun getHistoryForTimer(timerId: String): List<StreakHistory> {
        val db = readableDatabase
        val list = mutableListOf<StreakHistory>()
        val cursor = db.query("history", arrayOf("id", "start_timestamp", "end_timestamp", "note"), "\"timer_id\" = ?", arrayOf(timerId), null, null, "end_timestamp DESC")
        cursor.use { c ->
            val idIdx = c.getColumnIndex("id")
            val startIdx = c.getColumnIndex("start_timestamp")
            val endIdx = c.getColumnIndex("end_timestamp")
            val noteIdx = c.getColumnIndex("note")
            while (c.moveToNext()) {
                list.add(StreakHistory(c.getLong(idIdx), c.getLong(startIdx), c.getLong(endIdx), c.getString(noteIdx) ?: ""))
            }
        }
        return list
    }

    fun replaceAllData(settingsMap: Map<String, String>, timersList: List<StreakTimer>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete("settings", null, null)
            db.delete("history", null, null)
            db.delete("timers", null, null)

            settingsMap.forEach { (key, value) ->
                val cv = android.content.ContentValues().apply {
                    put("key", key)
                    put("value", value)
                }
                db.insert("settings", null, cv)
            }

            timersList.forEach { timer ->
                val timerCv = android.content.ContentValues().apply {
                    put("id", timer.id)
                    put("name", timer.name)
                    put("start_timestamp", timer.startTimestamp)
                    put("is_tracking", if (timer.isTracking) 1 else 0)
                }
                db.insert("timers", null, timerCv)

                timer.historyList.forEach { history ->
                    val hCv = android.content.ContentValues().apply {
                        put("id", history.id)
                        put("timer_id", timer.id)
                        put("start_timestamp", history.startTimestamp)
                        put("end_timestamp", history.endTimestamp)
                        put("note", history.note)
                    }
                    db.insert("history", null, hCv)
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}
