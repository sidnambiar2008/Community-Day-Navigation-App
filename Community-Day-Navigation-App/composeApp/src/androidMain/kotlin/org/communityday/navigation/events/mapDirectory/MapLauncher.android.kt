package org.communityday.navigation.events.mapDirectory
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.content.Context
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.lang.ref.WeakReference


object AndroidMapConfig {
    private var _context: WeakReference<Context>? = null
    var context: Context?
        get() = _context?.get()
        set(value) { _context = value?.let { WeakReference(it) } }
}
@Suppress("WrongConstant") // This specifically targets the flag error
actual fun openMap(lat: Double, lon: Double, label: String, context: Any?) {
    // Check the passed context first, then our global backup
    val actualContext = (context as? android.content.Context) ?: AndroidMapConfig.context

    if (actualContext != null) {
        val uri = Uri.parse("geo:$lat,$lon?q=$lat,$lon($label)")
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        actualContext.startActivity(intent)
    } else {
        android.util.Log.e("MAP_ERROR", "No context found to launch map!")
    }
}
actual class LocationProvider actual constructor() {
    @SuppressLint("MissingPermission")
    actual fun getCurrentLocation(onLocationReceived: (Double, Double) -> Unit) {
        try {
            // Use your singleton backup!
            val actualContext = AndroidMapConfig.context

            if (actualContext == null) {
                Log.e("GPS_DEBUG", "ERROR: Context is null. Did you set AndroidMapConfig.context in MainActivity?")
                onLocationReceived(0.0, 0.0)
                return
            }

            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(actualContext)

            fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                if (lastLoc != null) {
                    onLocationReceived(lastLoc.latitude, lastLoc.longitude)
                } else {
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                        .addOnSuccessListener { freshLoc ->
                            onLocationReceived(freshLoc?.latitude ?: 0.0, freshLoc?.longitude ?: 0.0)
                        }
                }
            }
        } catch (e: Exception) {
            Log.e("GPS_DEBUG", "CRITICAL CRASH: ${e.message}")
            onLocationReceived(0.0, 0.0)
        }
    }
}



