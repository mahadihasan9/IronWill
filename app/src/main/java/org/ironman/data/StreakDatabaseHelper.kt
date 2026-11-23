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

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StreakDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "iron_will.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_SETTINGS = "settings"
        private const val KEY_SETTING_KEY = "key"
        private const val KEY_SETTING_VALUE = "value"

        private const val TABLE_TIMERS = "timers"
        private const val KEY_TIMER_ID = "id"
        private const val KEY_TIMER_NAME = "name"
        private const val KEY_TIMER_START_TIMESTAMP = "start_timestamp"
        private const val KEY_TIMER_IS_TRACKING = "is_tracking"

        private const val TABLE_HISTORY = "history"
        private const val KEY_HISTORY_ID = "id"
        private const val KEY_HISTORY_TIMER_ID = "timer_id"
        private const val KEY_HISTORY_START_TIMESTAMP = "start_timestamp"
        private const val KEY_HISTORY_END_TIMESTAMP = "end_timestamp"
        private const val KEY_HISTORY_NOTE = "note"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createSettingsTable = """
            CREATE TABLE $TABLE_SETTINGS (
                "$KEY_SETTING_KEY" TEXT PRIMARY KEY,
                "$KEY_SETTING_VALUE" TEXT
            )
        """.trimIndent()

        val createTimersTable = """
            CREATE TABLE $TABLE_TIMERS (
                "$KEY_TIMER_ID" TEXT PRIMARY KEY,
                "$KEY_TIMER_NAME" TEXT NOT NULL,
                "$KEY_TIMER_START_TIMESTAMP" INTEGER NOT NULL,
                "$KEY_TIMER_IS_TRACKING" INTEGER NOT NULL
            )
        """.trimIndent()

        val createHistoryTable = """
            CREATE TABLE $TABLE_HISTORY (
                "$KEY_HISTORY_ID" INTEGER PRIMARY KEY,
                "$KEY_HISTORY_TIMER_ID" TEXT NOT NULL,
                "$KEY_HISTORY_START_TIMESTAMP" INTEGER NOT NULL,
                "$KEY_HISTORY_END_TIMESTAMP" INTEGER NOT NULL,
                "$KEY_HISTORY_NOTE" TEXT NOT NULL DEFAULT '',
                FOREIGN KEY ("$KEY_HISTORY_TIMER_ID") REFERENCES $TABLE_TIMERS ("$KEY_TIMER_ID") ON DELETE CASCADE
            )
        """.trimIndent()

        db.execSQL(createSettingsTable)
        db.execSQL(createTimersTable)
        db.execSQL(createHistoryTable)

        val initialValues = ContentValues().apply {
            put(KEY_TIMER_ID, "main")
            put(KEY_TIMER_NAME, "Main Counter")
            put(KEY_TIMER_START_TIMESTAMP, System.currentTimeMillis())
            put(KEY_TIMER_IS_TRACKING, 0)
        }
        db.insert(TABLE_TIMERS, null, initialValues)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    fun getSetting(key: String, defaultValue: String): String {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_SETTINGS,
            arrayOf(KEY_SETTING_VALUE),
            "$KEY_SETTING_KEY = ?",
            arrayOf(key),
            null, null, null
        )
        cursor.use {
            if (it.moveToFirst()) {
                val valueIndex = it.getColumnIndex(KEY_SETTING_VALUE)
                if (valueIndex != -1) {
                    return it.getString(valueIndex) ?: defaultValue
                }
            }
        }
        return defaultValue
    }

    fun setSetting(key: String, value: String) {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_SETTING_KEY, key)
            put(KEY_SETTING_VALUE, value)
        }
        db.insertWithOnConflict(
            TABLE_SETTINGS,
            null,
            contentValues,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun getAllTimers(): List<StreakTimer> {
        val db = readableDatabase
        val timers = mutableListOf<StreakTimer>()

        val cursor = db.query(
            TABLE_TIMERS,
            arrayOf(KEY_TIMER_ID, KEY_TIMER_NAME, KEY_TIMER_START_TIMESTAMP, KEY_TIMER_IS_TRACKING),
            null, null, null, null, null
        )

        cursor.use { c ->
            val idIndex = c.getColumnIndex(KEY_TIMER_ID)
            val nameIndex = c.getColumnIndex(KEY_TIMER_NAME)
            val startTsIndex = c.getColumnIndex(KEY_TIMER_START_TIMESTAMP)
            val trackingIndex = c.getColumnIndex(KEY_TIMER_IS_TRACKING)

            while (c.moveToNext()) {
                val id = c.getString(idIndex)
                val name = c.getString(nameIndex)
                val startTs = c.getLong(startTsIndex)
                val isTracking = c.getInt(trackingIndex) == 1
                val history = getHistoryForTimer(id)

                timers.add(StreakTimer(id, name, startTs, isTracking, history))
            }
        }

        if (timers.none { it.id == "main" }) {
            val mainTimer = StreakTimer("main", "Main Counter", System.currentTimeMillis(), false, getHistoryForTimer("main"))
            saveTimer(mainTimer)
            timers.add(0, mainTimer)
        }

        return timers
    }

    fun saveTimer(timer: StreakTimer) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_TIMER_ID, timer.id)
            put(KEY_TIMER_NAME, timer.name)
            put(KEY_TIMER_START_TIMESTAMP, timer.startTimestamp)
            put(KEY_TIMER_IS_TRACKING, if (timer.isTracking) 1 else 0)
        }
        db.insertWithOnConflict(
            TABLE_TIMERS,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun deleteTimer(timerId: String) {
        if (timerId == "main") return
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_HISTORY, "$KEY_HISTORY_TIMER_ID = ?", arrayOf(timerId))
            db.delete(TABLE_TIMERS, "$KEY_TIMER_ID = ?", arrayOf(timerId))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getHistoryForTimer(timerId: String): List<StreakHistory> {
        val db = readableDatabase
        val historyList = mutableListOf<StreakHistory>()

        val cursor = db.query(
            TABLE_HISTORY,
            arrayOf(KEY_HISTORY_ID, KEY_HISTORY_START_TIMESTAMP, KEY_HISTORY_END_TIMESTAMP, KEY_HISTORY_NOTE),
            "$KEY_HISTORY_TIMER_ID = ?",
            arrayOf(timerId),
            null, null,
            "$KEY_HISTORY_END_TIMESTAMP DESC"
        )

        cursor.use { c ->
            val idIdx = c.getColumnIndex(KEY_HISTORY_ID)
            val startIdx = c.getColumnIndex(KEY_HISTORY_START_TIMESTAMP)
            val endIdx = c.getColumnIndex(KEY_HISTORY_END_TIMESTAMP)
            val noteIdx = c.getColumnIndex(KEY_HISTORY_NOTE)

            while (c.moveToNext()) {
                val id = c.getLong(idIdx)
                val start = c.getLong(startIdx)
                val end = c.getLong(endIdx)
                val note = c.getString(noteIdx) ?: ""
                historyList.add(StreakHistory(id, start, end, note))
            }
        }
        return historyList
    }

    fun addHistoryItem(timerId: String, history: StreakHistory) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_HISTORY_ID, history.id)
            put(KEY_HISTORY_TIMER_ID, timerId)
            put(KEY_HISTORY_START_TIMESTAMP, history.startTimestamp)
            put(KEY_HISTORY_END_TIMESTAMP, history.endTimestamp)
            put(KEY_HISTORY_NOTE, history.note)
        }
        db.insertWithOnConflict(
            TABLE_HISTORY,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun deleteHistoryItem(historyId: Long) {
        val db = writableDatabase
        db.delete(TABLE_HISTORY, "$KEY_HISTORY_ID = ?", arrayOf(historyId.toString()))
    }

    fun clearAllHistory(timerId: String) {
        val db = writableDatabase
        db.delete(TABLE_HISTORY, "$KEY_HISTORY_TIMER_ID = ?", arrayOf(timerId))
    }

    fun replaceAllData(
        settingsMap: Map<String, String>,
        timersList: List<StreakTimer>
    ) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_SETTINGS, null, null)
            db.delete(TABLE_HISTORY, null, null)
            db.delete(TABLE_TIMERS, null, null)

            settingsMap.forEach { (key, value) ->
                val cv = ContentValues().apply {
                    put(KEY_SETTING_KEY, key)
                    put(KEY_SETTING_VALUE, value)
                }
                db.insert(TABLE_SETTINGS, null, cv)
            }

            timersList.forEach { timer ->
                val timerCv = ContentValues().apply {
                    put(KEY_TIMER_ID, timer.id)
                    put(KEY_TIMER_NAME, timer.name)
                    put(KEY_TIMER_START_TIMESTAMP, timer.startTimestamp)
                    put(KEY_TIMER_IS_TRACKING, if (timer.isTracking) 1 else 0)
                }
                db.insert(TABLE_TIMERS, null, timerCv)

                timer.historyList.forEach { history ->
                    val hCv = ContentValues().apply {
                        put(KEY_HISTORY_ID, history.id)
                        put(KEY_HISTORY_TIMER_ID, timer.id)
                        put(KEY_HISTORY_START_TIMESTAMP, history.startTimestamp)
                        put(KEY_HISTORY_END_TIMESTAMP, history.endTimestamp)
                        put(KEY_HISTORY_NOTE, history.note)
                    }
                    db.insert(TABLE_HISTORY, null, hCv)
                }
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}
