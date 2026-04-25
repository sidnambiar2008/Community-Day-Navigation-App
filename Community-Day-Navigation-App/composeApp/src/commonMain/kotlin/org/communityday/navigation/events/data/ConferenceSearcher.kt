package org.communityday.navigation.events.data

expect class ConferenceSearcher()
{
    suspend fun search(query: String):List<Conference>
}