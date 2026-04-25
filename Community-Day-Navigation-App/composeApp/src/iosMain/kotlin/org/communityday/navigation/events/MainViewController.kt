package org.communityday.navigation.events

import androidx.compose.ui.window.ComposeUIViewController
import org.communityday.navigation.events.mapDirectory.LocationProvider

fun MainViewController() = ComposeUIViewController {
    val locationProvider = LocationProvider()
    App(locationProvider = locationProvider) }