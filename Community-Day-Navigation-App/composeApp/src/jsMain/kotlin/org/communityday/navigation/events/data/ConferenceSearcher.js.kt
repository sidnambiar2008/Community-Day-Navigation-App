package org.communityday.navigation.events.data
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

actual class ConferenceSearcher {
    // You should ideally pass the engine in, but this works if Gradle is set up
    private val httpClient = HttpClient()

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true // Helpful for raw REST responses
    }

    actual suspend fun search(query: String): List<Conference> {
        return try {
            // Use the DSN (Distributed Search Network) URL you provided
            val response = httpClient.post("https://QZPCMXM01D-dsn.algolia.net/1/indexes/conferences/query") {
                header("X-Algolia-Application-Id", "QZPCMXM01D")
                header("X-Algolia-API-Key", "6cc7d0551bd8fb59c75313135385a396")
                contentType(ContentType.Application.Json)

                // Better way to format the body for Algolia
                setBody(buildJsonObject {
                    put("params", "query=${query}")
                }.toString())
            }

            if (response.status != HttpStatusCode.OK) {
                println("Algolia Error: ${response.status}")
                return emptyList()
            }

            val responseBody = response.bodyAsText()
            val jsonElement = jsonParser.parseToJsonElement(responseBody)
            val hitsArray = jsonElement.jsonObject["hits"]?.jsonArray

            hitsArray?.map { hitElement ->
                // This will map the 'objectID' from Algolia to your Conference objectID
                jsonParser.decodeFromJsonElement<Conference>(hitElement)
            } ?: emptyList()

        } catch (e: Exception) {
            // In KMP, use println or a logger to see this in Logcat/Xcode console
            println("Search Exception: ${e.message}")
            emptyList()
        }
    }
}