package org.communityday.navigation.events

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.communityday.navigation.events.ui.screens.EventListScreen
import org.communityday.navigation.events.ui.screens.WelcomeScreen
import org.communityday.navigation.events.ui.screens.EventDetailScreen
import org.communityday.navigation.events.data.Event
import org.communityday.navigation.events.data.EventCategory

@Preview
@Composable
fun AppPreview() {
    App()
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(
        onGetStarted = {},
        NavyBlue = androidx.compose.ui.graphics.Color(0xFF000033),
        Silver = androidx.compose.ui.graphics.Color(0xFFC0C0C0),
        ActionOrange = androidx.compose.ui.graphics.Color(0xFFFF8C00),
        Turquoise = androidx.compose.ui.graphics.Color(0xFF40E0D0)
    )
}

@Preview
@Composable
fun EventListScreenPreview() {
    EventListScreen(
        onEventClick = {}
    )
}

@Preview
@Composable
fun EventDetailScreenPreview() {
    val sampleEvent = Event(
        id = "1",
        title = "Sample Event",
        description = "This is a sample event for preview",
        date = "2024-01-15",
        time = "10:00 AM",
        location = "Sample Location",
        category = EventCategory.TECHNOLOGY,
        tags = listOf("android", "kotlin", "compose"),
        speaker = "John Doe",
        maxAttendees = 100,
        currentAttendees = 45,
        imageUrl = null,
        isRegistered = false
    )
    
    EventDetailScreen(
        event = sampleEvent,
        onBackClick = {}
    )
}
