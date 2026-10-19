package com.yesmin.ai.service.automation

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.Settings
import android.widget.Toast

class AutomationManager(private val context: Context) {

    sealed class AutomationResult {
        data class Success(val message: String) : AutomationResult()
        data class Failure(val error: String) : AutomationResult()
        object NotAnAction : AutomationResult()
    }

    fun tryParseAndExecute(command: String): AutomationResult {
        val lower = command.lowercase().trim()

        return when {
            lower.startsWith("open youtube") || lower.contains("launch youtube") -> {
                launchAppByPackage("com.google.android.youtube", "YouTube")
            }
            lower.startsWith("play ") && (lower.contains("on youtube") || lower.contains("song") || lower.contains("gaana")) -> {
                val query = lower.removePrefix("play ").replace("on youtube", "").replace("song", "").trim()
                playYouTube(query)
            }
            lower.startsWith("open whatsapp") -> {
                launchAppByPackage("com.whatsapp", "WhatsApp")
            }
            lower.startsWith("open settings") -> {
                openSettings()
            }
            lower.startsWith("open camera") -> {
                openCamera()
            }
            lower.startsWith("open chrome") || lower.startsWith("open browser") -> {
                launchAppByPackage("com.android.chrome", "Browser")
            }
            lower.startsWith("set alarm for ") -> {
                val timeStr = lower.removePrefix("set alarm for ").trim()
                setAlarm(timeStr)
            }
            lower.startsWith("search ") || lower.startsWith("google ") -> {
                val query = lower.removePrefix("search ").removePrefix("google ").trim()
                webSearch(query)
            }
            lower.startsWith("call ") -> {
                val target = lower.removePrefix("call ").trim()
                dialNumber(target)
            }
            else -> AutomationResult.NotAnAction
        }
    }

    fun launchAppByPackage(packageName: String, friendlyName: String): AutomationResult {
        val pm = context.packageManager
        val intent = pm.getLaunchIntentForPackage(packageName)
        return if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            AutomationResult.Success("Opening $friendlyName for you.")
        } else {
            AutomationResult.Failure("$friendlyName is not installed on this device.")
        }
    }

    fun playYouTube(query: String): AutomationResult {
        val appIntent = Intent(Intent.ACTION_SEARCH).apply {
            setPackage("com.google.android.youtube")
            putExtra("query", query)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(appIntent)
            AutomationResult.Success("Playing '$query' on YouTube.")
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(query))).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(webIntent)
            AutomationResult.Success("Searching '$query' on YouTube.")
        }
    }

    fun setAlarm(timeStr: String): AutomationResult {
        // Extract hour and minutes
        val regex = Regex("(\\d{1,2})(:(\\d{2}))?\\s*(am|pm)?")
        val match = regex.find(timeStr)

        val hour: Int
        val minutes: Int
        if (match != null) {
            var h = match.groupValues[1].toIntOrNull() ?: 7
            val m = match.groupValues[3].toIntOrNull() ?: 0
            val amPm = match.groupValues[4]
            if (amPm.equals("pm", ignoreCase = true) && h < 12) h += 12
            if (amPm.equals("am", ignoreCase = true) && h == 12) h = 0
            hour = h
            minutes = m
        } else {
            hour = 7
            minutes = 0
        }

        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minutes)
            putExtra(AlarmClock.EXTRA_MESSAGE, "Yesmin AI Alarm")
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            AutomationResult.Success("Alarm set for %02d:%02d.".format(hour, minutes))
        } catch (e: Exception) {
            AutomationResult.Failure("Unable to open clock alarm application.")
        }
    }

    fun openSettings(): AutomationResult {
        val intent = Intent(Settings.ACTION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        return AutomationResult.Success("Opening system settings.")
    }

    fun openCamera(): AutomationResult {
        val intent = Intent("android.media.action.IMAGE_CAPTURE").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            AutomationResult.Success("Opening camera.")
        } catch (e: Exception) {
            AutomationResult.Failure("No camera application found.")
        }
    }

    fun webSearch(query: String): AutomationResult {
        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, query)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        return AutomationResult.Success("Searching the web for '$query'.")
    }

    fun dialNumber(numberOrName: String): AutomationResult {
        val clean = numberOrName.filter { it.isDigit() || it == '+' }
        val uri = if (clean.isNotBlank()) Uri.parse("tel:$clean") else Uri.parse("tel:")
        val intent = Intent(Intent.ACTION_DIAL, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        return AutomationResult.Success("Opening dialer for $numberOrName.")
    }
}
