package org.ironman.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ironman.data.StreakHistory
import org.ironman.ui.components.PinVerificationDialog
import org.ironman.ui.theme.AccentGold
import org.ironman.ui.theme.AccentRed
import org.ironman.ui.theme.DarkSurface
import org.ironman.ui.theme.PrimaryCyan
import org.ironman.ui.theme.TextMuted
import org.ironman.ui.theme.TextPrimary
import org.ironman.ui.theme.TextSecondary
import org.ironman.ui.viewmodel.MainViewModel

@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val historyList by viewModel.historyList.collectAsState()
    val pinEnabled by viewModel.pinEnabled.collectAsState()
    val pinCode by viewModel.pinCode.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var showPinDialog by remember { mutableStateOf(false) }

    val requirePinThenExecute = { action: () -> Unit ->
        if (pinEnabled) {
            pendingAction = action
            showPinDialog = true
        } else {
            action()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (appLanguage) {
                    "bn" -> "স্ট্রিক হিস্ট্রি"
                    "ar" -> "سجل العداد"
                    else -> "Streak History"
                },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            if (historyList.isNotEmpty()) {
                OutlinedButton(
                    onClick = {
                        requirePinThenExecute { showClearDialog = true }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "সব মুছুন"
                            "ar" -> "مسح الكل"
                            else -> "Clear All"
                        },
                        color = AccentRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "No History",
                        tint = TextMuted,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "কোনো হিস্ট্রি পাওয়া যায়নি"
                            "ar" -> "لا يوجد سجل حتى الآن"
                            else -> "No history recorded yet"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (appLanguage) {
                            "bn" -> "রিসেট করলে আপনার পূর্বের রেকর্ডগুলো এখানে দেখা যাবে।"
                            "ar" -> "ستظهر سجلاتك السابقة هنا عند إعادة الضبط"
                            else -> "Your previous streak records will appear here when you reset."
                        },
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historyList, key = { it.id }) { item ->
                    HistoryCard(
                        item = item,
                        appLanguage = appLanguage,
                        onDelete = {
                            requirePinThenExecute { viewModel.deleteHistoryItem(item.id) }
                        }
                    )
                }
            }
        }
    }

    if (showPinDialog) {
        PinVerificationDialog(
            correctPin = pinCode,
            title = "Security PIN Required",
            subtitle = "Enter PIN to perform this deletion:",
            onDismiss = {
                showPinDialog = false
                pendingAction = null
            },
            onPinSuccess = {
                showPinDialog = false
                pendingAction?.invoke()
                pendingAction = null
            }
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All History?") },
            text = { Text("This action cannot be undone. Are you sure?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearHistory()
                    showClearDialog = false
                }) {
                    Text("Clear", color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HistoryCard(
    item: StreakHistory,
    appLanguage: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (appLanguage) {
                        "bn" -> "মেয়াদ: ${item.getFormattedDuration()}"
                        "ar" -> "المدة: ${item.getFormattedDuration()}"
                        else -> "Duration: ${item.getFormattedDuration()}"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryCyan
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = TextMuted
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Started: ${item.getFormattedStartDate()}",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Text(
                text = "Ended:   ${item.getFormattedEndDate()}",
                fontSize = 12.sp,
                color = TextSecondary
            )
            if (item.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Notes,
                        contentDescription = "Note",
                        tint = AccentGold,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = item.note,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
