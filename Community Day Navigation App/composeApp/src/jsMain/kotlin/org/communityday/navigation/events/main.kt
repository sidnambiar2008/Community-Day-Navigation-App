package org.communityday.navigation.events

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import org.jetbrains.skiko.wasm.onWasmReady
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import dev.gitlive.firebase.FirebaseOptions

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        // 1. Initialize Firebase for the JS target
        Firebase.initialize(
            context = null, // Always null for JS
            options = FirebaseOptions(
                apiKey = "AIzaSyAXAHUpJpvpxTgZ9xpM1wxKHR2bidG0GdA",
                applicationId = "1:512193332305:web:890e7d8a84c3886d4ea470",
                projectId = "community-day-navigation-15748",
                authDomain = "community-day-navigation-15748.firebaseapp.com",
                storageBucket = "community-day-navigation-15748.firebasestorage.app",

            )
        )

        // 2. Start the UI
        CanvasBasedWindow(canvasElementId = "ComposeTarget") {
            App()
        }
    }
}