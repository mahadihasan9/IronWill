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
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ironman.data.StreakDatabaseHelper
import java.util.concurrent.TimeUnit

object AutoBackupManager {

    const val KEY_ENABLED = "auto_backup_enabled"
    const val KEY_URI = "auto_backup_uri"
    private const val UNIQUE_WORK_NAME = "iron_will_auto_backup"
    private const val BACKUP_FILE_NAME = "iron_will_auto_backup.db"

    fun requestBackup(context: Context, enabled: Boolean, uri: String, immediate: Boolean = false) {
        val workManager = WorkManager.getInstance(context)
        if (!enabled || uri.isBlank()) {
            workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
            return
        }
        val request = OneTimeWorkRequestBuilder<AutoBackupWorker>()
            .setInitialDelay(if (immediate) 0L else 2L, TimeUnit.SECONDS)
            .build()
        workManager.enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    suspend fun requestBackupFromSettings(context: Context) = withContext(Dispatchers.IO) {
        val helper = StreakDatabaseHelper(context)
        try {
            val enabled = helper.getSetting(KEY_ENABLED, "false") == "true"
            val uri = helper.getSetting(KEY_URI, "")
            requestBackup(context, enabled, uri, immediate = true)
        } finally {
            helper.close()
        }
    }

    fun persistFolderPermission(context: Context, uri: Uri) {
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun writeBackup(context: Context, treeUri: Uri, bytes: ByteArray): Boolean {
        val resolver = context.contentResolver
        return try {
            val rootId = DocumentsContract.getTreeDocumentId(treeUri)
            val parentUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, rootId)
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, rootId)
            var targetUri: Uri? = null
            resolver.query(
                childrenUri,
                arrayOf(
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME
                ),
                null,
                null,
                null
            )?.use { cursor ->
                val idIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                val nameIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                while (cursor.moveToNext()) {
                    if (nameIndex >= 0 && cursor.getString(nameIndex) == BACKUP_FILE_NAME) {
                        targetUri = DocumentsContract.buildDocumentUriUsingTree(
                            treeUri,
                            cursor.getString(idIndex)
                        )
                        break
                    }
                }
            }
            val documentUri = targetUri ?: DocumentsContract.createDocument(
                resolver,
                parentUri,
                "application/octet-stream",
                BACKUP_FILE_NAME
            ) ?: return false
            resolver.openOutputStream(documentUri, "wt")?.use { it.write(bytes) } != null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
