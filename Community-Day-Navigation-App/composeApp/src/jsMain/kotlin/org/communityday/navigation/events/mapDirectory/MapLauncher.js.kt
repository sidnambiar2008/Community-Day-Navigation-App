package org.communityday.navigation.events.mapDirectory

import kotlinx.browser.window


actual fun openMap(lat: Double, lon: Double, label: String, context: Any?) {
    // Universal Google Maps URL
    val url = "https://www.google.com/maps/search/?api=1&query=$lat,$lon"
    window.open(url, "_blank")
}

actual class LocationProvider { // Remove 'actual val'
    actual fun getCurrentLocation(onLocationReceived: (Double, Double) -> Unit) {
        // Use asDynamic() to bypass the missing type definitions
        val navigator = window.navigator.asDynamic()

        if (navigator.geolocation != null) {
            navigator.geolocation.getCurrentPosition(
                { pos: dynamic ->
                    val lat = pos.coords.latitude as Double
                    val lon = pos.coords.longitude as Double
                    onLocationReceived(lat, lon)
                },
                { error: dynamic ->
                    println("Browser Geolocation Error: ${error.message}")
                }
            )
        } else {
            println("Geolocation is not supported by this browser.")
        }
    }
}
