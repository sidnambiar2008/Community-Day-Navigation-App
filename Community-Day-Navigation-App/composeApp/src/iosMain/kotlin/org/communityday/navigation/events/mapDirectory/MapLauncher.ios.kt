package org.communityday.navigation.events.mapDirectory
import platform.CoreLocation.CLLocationManager
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.darwin.NSObject
import kotlinx.cinterop.useContents // Add this import

actual fun openMap(lat: Double, lon: Double, label: String, context: Any?) {
    val encodedLabel = label.replace(" ", "+")
    val googleMapsAppUrl = NSURL.URLWithString("comgooglemaps://?q=$lat,$lon($encodedLabel)&center=$lat,$lon&zoom=14")
    val appleMapsUrl = NSURL.URLWithString("http://maps.apple.com/?ll=$lat,$lon&q=$encodedLabel")

    val application = UIApplication.sharedApplication

    if (googleMapsAppUrl != null && application.canOpenURL(googleMapsAppUrl)) {
        application.openURL(googleMapsAppUrl, options = emptyMap<Any?, Any?>(), completionHandler = null)
    } else if (appleMapsUrl != null) {
        application.openURL(appleMapsUrl, options = emptyMap<Any?, Any?>(), completionHandler = null)
    }
}

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual class LocationProvider actual constructor() {
    private val locationManager = CLLocationManager()

    actual fun getCurrentLocation(onLocationReceived: (Double, Double) -> Unit) {
        locationManager.requestWhenInUseAuthorization()

        val location = locationManager.location
        if (location != null) {
            location.coordinate.useContents {
                onLocationReceived(latitude, longitude)
            }
        }
        else {
            // Optional: You could log something here or
            // trigger a one-time update request if you wanted to get fancy.
            println("iOS Location is currently null - GPS warming up or permission pending")
            onLocationReceived(0.0, 0.0)
        }
    }
}


