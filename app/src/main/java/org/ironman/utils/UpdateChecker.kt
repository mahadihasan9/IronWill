package org.ironman.utils

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
    const val CURRENT_VERSION_NAME = "3.1"
    const val CURRENT_VERSION_CODE = 4
    private const val GITHUB_API_URL = "https://api.github.com/repos/mahadihasan9/IronWill/releases/latest"
    const val GITHUB_RELEASES_PAGE = "https://github.com/mahadihasan9/IronWill/releases"

    suspend fun checkForUpdates(): UpdateStatus = withContext(Dispatchers.IO) {
        try {
            val url = URL(GITHUB_API_URL)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", "IronWillApp/$CURRENT_VERSION_NAME")
                setRequestProperty("Accept", "application/vnd.github.v3+json")
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

                if (cleanTag.isNotBlank() && isNewerVersion(cleanTag, CURRENT_VERSION_NAME)) {
                    UpdateStatus.NewVersionAvailable(version = cleanTag, releaseUrl = htmlUrl)
                } else {
                    UpdateStatus.LatestVersion(version = CURRENT_VERSION_NAME)
                }
            } else {
                connection.disconnect()
                UpdateStatus.LatestVersion(version = CURRENT_VERSION_NAME)
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
