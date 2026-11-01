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
import org.ironman.data.StreakHistory
import org.ironman.data.StreakRepository
import org.ironman.utils.LauncherAliasManager

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

data class RankInfo(
    val title: String,
    val minDays: Int,
    val nextRankTitle: String,
    val daysToNextRank: Int
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StreakRepository(application)

    val startTimestamp: StateFlow<Long> = repository.startTimestampFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = System.currentTimeMillis()
    )

    val historyList: StateFlow<List<StreakHistory>> = repository.historyListFlow.stateIn(
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

    val isTracking: StateFlow<Boolean> = repository.isTrackingFlow.stateIn(
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

    private val _currentTimeMillis = MutableStateFlow(System.currentTimeMillis())
    val currentTimeMillis: StateFlow<Long> = _currentTimeMillis.asStateFlow()

    private val _currentQuote = MutableStateFlow(QuotesProvider.getRandomQuote())
    val currentQuote: StateFlow<Quote> = _currentQuote.asStateFlow()

    init {
        // Ticking timer loop
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

    // Dynamic Progressive Target Goal (3 -> 4 -> 5 -> 7 -> 14 -> 30 -> 60 -> 90 -> 180 -> 365)
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

    fun refreshQuote() {
        _currentQuote.value = QuotesProvider.getRandomQuoteDifferentFrom(_currentQuote.value)
    }

    fun applyLauncherDisguise(context: Context, iconId: Int, nameIndex: Int) {
        viewModelScope.launch {
            repository.saveDisguiseSelection(iconId, nameIndex)
            LauncherAliasManager.applyDisguise(context, iconId, nameIndex)
        }
    }

    fun startStreak(startTimestamp: Long) {
        viewModelScope.launch {
            repository.startStreak(startTimestamp)
            refreshQuote()
        }
    }

    fun resetStreak(note: String = "") {
        viewModelScope.launch {
            repository.resetStreak(note)
            refreshQuote()
        }
    }

    fun updateStartTimestamp(newTimestamp: Long) {
        viewModelScope.launch {
            repository.saveStartTimestamp(newTimestamp)
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
            repository.deleteHistoryItem(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }

    fun getRankInfo(currentDays: Long): RankInfo {
        return when {
            currentDays < 1 -> RankInfo("Novice", 0, "Iron Mind", 1 - currentDays.toInt())
            currentDays < 3 -> RankInfo("Bronze Will", 1, "Iron Will", 3 - currentDays.toInt())
            currentDays < 7 -> RankInfo("Iron Will", 3, "Steel Mind", 7 - currentDays.toInt())
            currentDays < 14 -> RankInfo("Steel Mind", 7, "Unstoppable", 14 - currentDays.toInt())
            currentDays < 30 -> RankInfo("Unstoppable", 14, "Titan", 30 - currentDays.toInt())
            currentDays < 90 -> RankInfo("Titan", 30, "Iron Legend", 90 - currentDays.toInt())
            else -> RankInfo("Iron Legend", 90, "Master of Will", 365 - currentDays.toInt())
        }
    }
}
