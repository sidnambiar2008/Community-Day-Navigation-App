package org.communityday.navigation.events.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import communitydaynavigationapp.composeapp.generated.resources.Res
import communitydaynavigationapp.composeapp.generated.resources.ic_flag
import org.communityday.navigation.events.data.Booth
import org.communityday.navigation.events.mapDirectory.openMap
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import org.communityday.navigation.events.data.EventRepository

@Composable
fun BoothDetailScreen(
    booth: Booth,
    conferenceAddress: String, // Add this parameter
    onBackClick: () -> Unit,
    confId: String,
    repository: EventRepository
) {
    val context: Any? = null
    val NavyBlue = Color(0xFF000033)
    val Silver = Color(0xFFC0C0C0)
    val Turquoise = Color(0xFF40E0D0)
    val CardNavy = Color(0xFF1A1A4D)
    val focusManager = LocalFocusManager.current // 1. Add this
    var showSafetyDialog by remember { mutableStateOf(false) }
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    val scope = rememberCoroutineScope()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBlue)
            // 2. Add this modifier to the main container
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .padding(16.dp)
    ) {
        // --- Navigation ---
        TextButton(
            onClick = onBackClick,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("< Back to Exhibitors", color = Turquoise, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // --- Header Section ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = booth.name,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 38.sp
                )
            }
        }

        Text(
            text = booth.organization,
            color = Turquoise,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Category Badge ---
        Surface(
            color = CardNavy,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = booth.category.uppercase(),
                color = Silver,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- About Section ---
        Text(
            text = "About the Exhibitor",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = booth.description,
            color = Silver,
            fontSize = 16.sp,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // --- New Smart Map Section ---
        // Show the button if we have coordinates OR a specific booth location string
        if ((booth.latitude != null && booth.longitude != null) || booth.location.isNotBlank()) {
            Button(
                onClick = {
                    openMap(
                        lat = booth.latitude ?: 0.0,
                        lon = booth.longitude ?: 0.0,
                        label = booth.location.ifBlank { booth.name },
                        conferenceAddress = conferenceAddress, // The "Neighborhood" Anchor
                        context = context
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Turquoise),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Find Booth on Map", color = NavyBlue, fontWeight = FontWeight.Bold)
            }

            // Show the text below the button so they know which booth to look for
            if (booth.location.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Location: ${booth.location}",
                    color = Silver,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(24.dp)) // Give it some space from the map info
            IconButton(
                onClick = { showSafetyDialog = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_flag),
                    contentDescription = "Safety Options",
                    // Using a muted alpha makes the icon look high-end rather than like an error
                    tint = Color(0xFFCF6679),
                    modifier = Modifier.size(28.dp) // Slightly smaller for a "footer" feel
                )
            }
            Text(
                text = "Report Content",
                color = Color(0xFFCF6679),
                fontSize = 10.sp, // Small and clean
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (showSafetyDialog) {
                AlertDialog(
                    onDismissRequest = { showSafetyDialog = false },
                    containerColor = Color(0xFF1A1A4D),
                    title = {
                        Text("Safety Options", color = Color.White, fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Text(
                            "Would you like to report this content for review, or hide this entire conference from your app?",
                            color = Silver
                        )
                    },
                    confirmButton = {
                        // ACTION 1: REPORT
                        TextButton(onClick = {
                            showSafetyDialog = false
                            uriHandler.openUri("https://docs.google.com/forms/...")
                        }) {
                            Text("Report", color = Turquoise)
                        }
                    },
                    dismissButton = {
                        Row {
                            // ACTION 2: CANCEL (The "Oops" button)
                            TextButton(onClick = { showSafetyDialog = false }) {
                                Text("Cancel", color = Silver)
                            }

                            // ACTION 3: HIDE (The "Nuclear" button)
                            TextButton(onClick = {
                                showSafetyDialog = false
                                scope.launch {
                                    repository.hideConference(confId)
                                    onBackClick()
                                }
                            }) {
                                Text("Hide Conference", color = Color(0xFFCF6679))
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}