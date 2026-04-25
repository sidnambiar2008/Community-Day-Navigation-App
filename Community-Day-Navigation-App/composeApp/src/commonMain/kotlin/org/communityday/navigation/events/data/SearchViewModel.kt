package org.communityday.navigation.events.data

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Job

class SearchViewModel(private val searcher: ConferenceSearcher) {
    var query by mutableStateOf("")
    var results by mutableStateOf<List<Conference>>(emptyList())
    var isSearching by mutableStateOf(false)

    private var searchJob: Job? = null // Track the current search

    fun onQueryChange(newQuery: String, scope: CoroutineScope) {
        query = newQuery

        // Cancel the previous search job if it's still running
        searchJob?.cancel()

        if (newQuery.length < 3) {
            results = emptyList()
            isSearching = false
            return
        }

        searchJob = scope.launch {
            isSearching = true
            try {
                results = searcher.search(newQuery)
            } catch (e: Exception) {
                // Handle error (e.g., no internet)
                results = emptyList()
            } finally {
                isSearching = false
            }
        }
    }
}