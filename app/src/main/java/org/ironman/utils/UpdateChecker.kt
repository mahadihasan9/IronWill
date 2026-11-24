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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

sealed class UpdateStatus {
    object Idle : UpdateStatus()
    object Checking : UpdateStatus()
    data class LatestVersion(val version: String) : UpdateStatus()
    data class NewVersionAvailable(val version: String, val releaseUrl: String) : UpdateStatus()
    data class Error(val message: String) : UpdateStatus()
}

object UpdateChecker {
    private const val GITHUB_API_URL = "https://api.github.com/repos/mahadihasan9/IronWill/releases/latest"
    const val GITHUB_RELEASES_PAGE = "https://github.com/mahadihasan9/IronWill/releases"

    suspend fun checkForUpdates(context: Context): UpdateStatus = withContext(Dispatchers.IO) {
        val currentVersion = try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }

        try {
            val url = URL("$GITHUB_API_URL?t=${System.currentTimeMillis()}")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                useCaches = false
                setRequestProperty("User-Agent", "IronWillApp/$currentVersion")
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate")
                setRequestProperty("Pragma", "no-cache")
                connectTimeout = 8000
                readTimeout = 8000
            }

            val statusCode = connection.responseCode
            if (statusCode == 200) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                connection.disconnect()

                val json = JSONObject(responseText)
                val rawTag = json.optString("tag_name", "").trim()
                val htmlUrl = json.optString("html_url", GITHUB_RELEASES_PAGE)
                val cleanTag = rawTag.removePrefix("v").removePrefix("V").trim()

                if (cleanTag.isNotBlank() && isNewerVersion(cleanTag, currentVersion)) {
                    UpdateStatus.NewVersionAvailable(version = cleanTag, releaseUrl = htmlUrl)
                } else {
                    UpdateStatus.LatestVersion(version = currentVersion)
                }
            } else {
                connection.disconnect()
                UpdateStatus.LatestVersion(version = currentVersion)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            UpdateStatus.Error(e.localizedMessage ?: "Unable to connect to server")
        }
    }

    private fun isNewerVersion(remoteVersion: String, currentVersion: String): Boolean {
        try {
            val remoteParts = remoteVersion.split(".").mapNotNull { it.toIntOrNull() }
            val currentParts = currentVersion.split(".").mapNotNull { it.toIntOrNull() }

            val maxParts = maxOf(remoteParts.size, currentParts.size)
            for (i in 0 until maxParts) {
                val r = remoteParts.getOrElse(i) { 0 }
                val c = currentParts.getOrElse(i) { 0 }
                if (r > c) return true
                if (r < c) return false
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }
}
