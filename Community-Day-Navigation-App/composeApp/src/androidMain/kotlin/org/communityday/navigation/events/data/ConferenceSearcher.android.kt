package org.communityday.navigation.events.data

import com.algolia.client.api.SearchClient
import com.algolia.client.model.search.SearchParamsObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.encodeToJsonElement

actual class ConferenceSearcher {
    // 1. Plain Strings (No AppID or APIKey wrappers!)
    private val client = SearchClient(
        appId = "QZPCMXM01D",
        apiKey = "6cc7d0551bd8fb59c75313135385a396"
    )

    private val jsonParser = Json {
        ignoreUnknownKeys = true
    }

    actual suspend fun search(query: String): List<Conference> {
        return try {
            val response = client.searchSingleIndex(
                indexName = "conferences",
                searchParams = SearchParamsObject(query = query)
            )

            response.hits.map { hit ->
                // 1. Convert the 'additionalProperties' map to a mutable map
                val jsonMap = hit.additionalProperties?.toMutableMap() ?: mutableMapOf()

                // 2. MANUALLY add the objectID into the map
                // This ensures your @SerialName("objectID") in Conference.kt actually gets a value
                jsonMap["objectID"] = jsonParser.encodeToJsonElement(hit.objectID)

                // 3. Convert back to JsonObject and decode
                val finalJson = jsonParser.encodeToJsonElement(jsonMap).jsonObject
                jsonParser.decodeFromJsonElement<Conference>(finalJson)
            }
        } catch (e: Exception) {
            println("Search Error: ${e.message}")
            emptyList()
        }
    }
}
