package org.communityday.navigation.events.notifications

import org.communityday.navigation.events.utils.convertTimeToMinutes
import platform.UserNotifications.*
import platform.Foundation.* //

actual class NotificationScheduler actual constructor() {
    actual fun scheduleEventNotification(id: String, title: String, startTime: String) {
        val center = UNUserNotificationCenter.currentNotificationCenter()

        center.getNotificationSettingsWithCompletionHandler { settings ->
            if (settings?.authorizationStatus == UNAuthorizationStatusAuthorized) {

                // 1. Calculate the delay using your utility
                val eventMinutes = convertTimeToMinutes(startTime)

                // Get current time (You'll need a small helper or use NSDate)
                val now = NSDate()
                val calendar = NSCalendar.currentCalendar
                val components = calendar.components(
                    NSCalendarUnitHour or NSCalendarUnitMinute,
                    fromDate = now
                )
                val currentMinutes = (components.hour * 60L) + components.minute

                // Calculate delay: (Event Time - Current Time - 10 minutes buffer)
                val delayInMinutes = eventMinutes - currentMinutes - 10
                val delayInSeconds = delayInMinutes * 60.0

                // 2. SAFETY CHECK: Only schedule if the event is in the future
                // If the delay is less than 5 seconds, just don't schedule it.
                if (delayInSeconds < 5.0) {
                    println("Event is too soon or in the past. Skipping notification.")
                    return@getNotificationSettingsWithCompletionHandler
                }

                val content = UNMutableNotificationContent().apply {
                    setTitle("Event Starting Soon!")
                    setBody("$title is about to start at $startTime")
                    setSound(UNNotificationSound.defaultSound())
                }

                // 3. Use the dynamic delay
                val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(delayInSeconds, false)

                val request = UNNotificationRequest.requestWithIdentifier(id, content, trigger)

                center.addNotificationRequest(request) { error ->
                    if (error != null) println("Error: ${error.localizedDescription}")
                }
            }
        }
    }

    actual fun cancelNotification(id: String) {
        // removePending... handles notifications that haven't fired yet
        // removeDelivered... handles notifications already sitting in the tray
        UNUserNotificationCenter.currentNotificationCenter().let { center ->
            center.removePendingNotificationRequestsWithIdentifiers(listOf(id))
            center.removeDeliveredNotificationsWithIdentifiers(listOf(id))
        }
    }

    actual fun requestPermissions() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { granted, error ->
            if (granted) {
                println("Notifications allowed!")
            } else if (error != null) {
                println("Error requesting permissions: ${error.localizedDescription}")
            }
        }
    }
}