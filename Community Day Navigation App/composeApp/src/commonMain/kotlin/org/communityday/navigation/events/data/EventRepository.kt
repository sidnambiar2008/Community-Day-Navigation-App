package org.communityday.navigation.events.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch

class EventRepository {
    private val firestore = Firebase.firestore
    private val eventsCollection = firestore
        .collection("conferences")
        .document("communityday2026")
        .collection("events")

    suspend fun getAllEvents(): Result<List<Event>> {
        return try {
            val snapshot = eventsCollection.get()

            val events = snapshot.documents.map { doc ->
                doc.data<Event>().copy(id = doc.id)
            }

            // --- DEBUG LOGS START ---
            println("Firestore Request: conferences/communityday2026/events")
            println("Firestore Result: Found ${events.size} documents")
            // --- DEBUG LOGS END ---

            Result.success(events)
        } catch (e: Exception) {
            // This will tell us if your Rules are blocking you or if the path is wrong
            println("Firestore ERROR: ${e.message}")
            Result.failure(e)
        }
    }
    /**
     * The "Real-time" Stream: Updates the UI automatically when you change
     * something in the Firebase Console.
     */
    fun getEventsStream(): Flow<List<Event>> {
        return eventsCollection.snapshots.map { snapshot ->
            snapshot.documents.map { doc ->
                doc.data<Event>().copy(id = doc.id)
            }
        }.catch { e ->
            println("Firestore Stream Error: ${e.message}")
            e.printStackTrace()
            emit(emptyList())
        }
    }

    /**
     * Your safety net: If Firebase is empty or offline, we show these.
     */
    fun getMockEvents(): List<Event> {
        return listOf(
            Event(
                id = "mock_1",
                title = "Offline: Check Connection",
                description = "We couldn't reach the cloud. Showing local data.",
                category = EventCategory.OTHER,
                location = "Unknown",
                startTime = "--:--",
                endTime = "--:--",
                latitude = 0.0,
                longitude = 0.0

            )
        )
    }
}