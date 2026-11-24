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

package org.ironman.utils

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.ironman.data.StreakDatabaseHelper
import org.ironman.data.StreakRepository

class AutoBackupWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val helper = StreakDatabaseHelper(applicationContext)
        return try {
            val enabled = helper.getSetting(AutoBackupManager.KEY_ENABLED, "false") == "true"
            val uriString = helper.getSetting(AutoBackupManager.KEY_URI, "")
            if (!enabled || uriString.isBlank()) {
                Result.success()
            } else {
                val pin = helper.getSetting("pin_code", "")
                val repository = StreakRepository(applicationContext, autoMigrate = false)
                val bytes = repository.exportEncryptedDbBackup(pin)
                if (bytes.isEmpty()) {
                    if (runAttemptCount < 3) Result.retry() else Result.success()
                } else {
                    val written = AutoBackupManager.writeBackup(
                        applicationContext,
                        Uri.parse(uriString),
                        bytes
                    )
                    if (written || runAttemptCount >= 3) Result.success() else Result.retry()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (runAttemptCount < 3) Result.retry() else Result.success()
        } finally {
            helper.close()
        }
    }
}
