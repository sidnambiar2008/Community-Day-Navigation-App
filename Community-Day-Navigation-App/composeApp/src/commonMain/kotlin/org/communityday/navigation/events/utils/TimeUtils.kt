package org.communityday.navigation.events.utils

fun convertTimeToMinutes(timeString: String): Int {
    return try {
        val cleanTime = timeString.uppercase().trim()
        val isPM = cleanTime.contains("PM")
        val isAM = cleanTime.contains("AM")

        // Strip AM/PM and split "9:30" into [9, 30]
        val timeOnly = cleanTime.replace("AM", "").replace("PM", "").trim()
        val parts = timeOnly.split(":")
        var hours = parts[0].toInt()
        val minutes = if (parts.size > 1) parts[1].toInt() else 0

        // Convert to 24-hour logic for sorting
        if (isPM && hours < 12) hours += 12
        if (isAM && hours == 12) hours = 0

        (hours * 60) + minutes
    } catch (e: Exception) {
        // If it's an old "broken" string, put it at the top so you can see it and fix it
        0
    }
}