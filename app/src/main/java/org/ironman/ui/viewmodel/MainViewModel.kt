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


package org.ironman.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.ironman.data.Quote
import org.ironman.data.QuotesProvider
import org.ironman.data.RankInfo
import org.ironman.data.RankSystem
import org.ironman.data.StreakHistory
import org.ironman.data.StreakRepository
import org.ironman.data.StreakTimer
import org.ironman.utils.LauncherAliasManager
import org.ironman.utils.UpdateChecker
import org.ironman.utils.UpdateStatus

data class StreakTime(
    val days: Long = 0,
    val hours: Long = 0,
    val minutes: Long = 0,
    val seconds: Long = 0,
    val totalMillis: Long = 0
)

data class StreakStats(
    val currentStreakMillis: Long = 0,
    val longestStreakMillis: Long = 0,
    val totalResets: Int = 0,
    val averageStreakMillis: Long = 0
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StreakRepository(application)

    val timersList: StateFlow<List<StreakTimer>> = repository.timersListFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf(StreakTimer("main", "Main Counter"))
    )

    val selectedTimerId: StateFlow<String> = repository.selectedTimerIdFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "main"
    )

    val activeTimer: StateFlow<StreakTimer> = combine(
        timersList,
        selectedTimerId
    ) { timers, selectedId ->
        timers.find { it.id == selectedId } ?: timers.firstOrNull() ?: StreakTimer("main", "Main Counter")
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StreakTimer("main", "Main Counter")
    )

    val startTimestamp: StateFlow<Long> = activeTimer.map { it.startTimestamp }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = System.currentTimeMillis()
    )

    val isTracking: StateFlow<Boolean> = activeTimer.map { it.isTracking }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val historyList: StateFlow<List<StreakHistory>> = activeTimer.map { it.historyList }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val pinCode: StateFlow<String> = repository.pinCodeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    val pinEnabled: StateFlow<Boolean> = repository.pinEnabledFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val appLockEnabled: StateFlow<Boolean> = repository.appLockEnabledFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val appLanguage: StateFlow<String> = repository.languageFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "en"
    )

    val selectedIconId: StateFlow<Int> = repository.selectedIconIdFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 1
    )

    val selectedNameIndex: StateFlow<Int> = repository.selectedNameIndexFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val autoBackupEnabled: StateFlow<Boolean> = repository.autoBackupEnabledFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val autoBackupUri: StateFlow<String> = repository.autoBackupUriFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    private val _currentTimeMillis = MutableStateFlow(System.currentTimeMillis())
    val currentTimeMillis: StateFlow<Long> = _currentTimeMillis.asStateFlow()

    private val _currentQuote = MutableStateFlow(QuotesProvider.getRandomQuote())
    val currentQuote: StateFlow<Quote> = _currentQuote.asStateFlow()

    private val _updateStatus = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)
    val updateStatus: StateFlow<UpdateStatus> = _updateStatus.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                _currentTimeMillis.value = System.currentTimeMillis()
                delay(1000)
            }
        }
    }

    val currentStreakTime: StateFlow<StreakTime> = combine(
        startTimestamp,
        currentTimeMillis,
        isTracking
    ) { start, now, tracking ->
        if (!tracking) {
            StreakTime(0, 0, 0, 0, 0)
        } else {
            val diff = (now - start).coerceAtLeast(0)
            val seconds = (diff / 1000) % 60
            val minutes = (diff / (1000 * 60)) % 60
            val hours = (diff / (1000 * 60 * 60)) % 24
            val days = diff / (1000 * 60 * 60 * 24)
            StreakTime(days, hours, minutes, seconds, diff)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StreakTime()
    )

    val targetGoalDays: StateFlow<Int> = currentStreakTime.map { streakTime ->
        val days = streakTime.days
        when {
            days < 3 -> 3
            days < 4 -> 4
            days < 5 -> 5
            days < 7 -> 7
            days < 14 -> 14
            days < 30 -> 30
            days < 60 -> 60
            days < 90 -> 90
            days < 180 -> 180
            else -> 365
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 3
    )

    val stats: StateFlow<StreakStats> = combine(
        currentStreakTime,
        historyList
    ) { streakTime, history ->
        val currentMillis = streakTime.totalMillis
        val historyMaxMillis = history.maxOfOrNull { it.durationMillis } ?: 0L
        val longest = maxOf(currentMillis, historyMaxMillis)
        val totalResets = history.size
        val avg = if (history.isNotEmpty()) {
            history.map { it.durationMillis }.average().toLong()
        } else {
            currentMillis
        }
        StreakStats(
            currentStreakMillis = currentMillis,
            longestStreakMillis = longest,
            totalResets = totalResets,
            averageStreakMillis = avg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StreakStats()
    )

    fun checkForUpdates(context: Context) {
        viewModelScope.launch {
            _updateStatus.value = UpdateStatus.Checking
            _updateStatus.value = UpdateChecker.checkForUpdates(context)
        }
    }

    fun selectTimer(timerId: String) {
        viewModelScope.launch {
            repository.selectTimer(timerId)
        }
    }

    fun addNewTimer(name: String) {
        viewModelScope.launch {
            repository.addNewTimer(name)
        }
    }

    fun deleteTimer(timerId: String) {
        viewModelScope.launch {
            repository.deleteTimer(timerId)
        }
    }

    fun applyLauncherDisguise(context: Context, iconId: Int, nameIndex: Int) {
        viewModelScope.launch {
            repository.saveDisguiseSelection(iconId, nameIndex)
            LauncherAliasManager.applyDisguise(context, iconId, nameIndex)
        }
    }

    fun setAutoBackup(enabled: Boolean, uri: String) {
        viewModelScope.launch {
            repository.setAutoBackup(enabled, uri)
        }
    }

    fun refreshQuote() {
        _currentQuote.value = QuotesProvider.getRandomQuoteDifferentFrom(_currentQuote.value)
    }

    fun startStreak(startTimestamp: Long) {
        viewModelScope.launch {
            val currentTimerId = activeTimer.value.id
            repository.startStreak(currentTimerId, startTimestamp)
            refreshQuote()
        }
    }

    fun resetStreak(resetTimestamp: Long = System.currentTimeMillis(), note: String = "") {
        viewModelScope.launch {
            val currentTimerId = activeTimer.value.id
            repository.resetStreak(currentTimerId, resetTimestamp, note)
            refreshQuote()
        }
    }

    fun updateStartTimestamp(newTimestamp: Long) {
        viewModelScope.launch {
            val currentTimerId = activeTimer.value.id
            repository.saveStartTimestamp(currentTimerId, newTimestamp)
        }
    }

    fun updateTargetGoal(days: Int) {
        viewModelScope.launch {
            repository.setTargetGoalDays(days)
        }
    }

    fun setPinCode(pin: String) {
        viewModelScope.launch {
            repository.setPinCode(pin)
        }
    }

    fun setPinEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setPinEnabled(enabled)
        }
    }

    fun setAppLockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setAppLockEnabled(enabled)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            repository.setLanguage(lang)
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            val currentTimerId = activeTimer.value.id
            repository.deleteHistoryItem(currentTimerId, id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            val currentTimerId = activeTimer.value.id
            repository.clearAllHistory(currentTimerId)
        }
    }

    fun exportEncryptedBackup(pin: String, onResult: (ByteArray) -> Unit) {
        viewModelScope.launch {
            val bytes = repository.exportEncryptedDbBackup(pin)
            onResult(bytes)
        }
    }

    fun readBackupInfo(bytes: ByteArray, pin: String, onResult: (org.ironman.data.BackupInfo?) -> Unit) {
        viewModelScope.launch {
            val info = repository.readBackupInfo(bytes, pin)
            onResult(info)
        }
    }

    fun importBackupDb(rawDbBytes: ByteArray, adjustTimestamps: Boolean, exportTime: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.importBackupDb(rawDbBytes, adjustTimestamps, exportTime)
            onResult(success)
        }
    }

    fun getRankInfo(currentDays: Long): RankInfo = RankSystem.getRankInfo(currentDays)
}
