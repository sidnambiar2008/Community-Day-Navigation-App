package org.communityday.navigation.events.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import communitydaynavigationapp.composeapp.generated.resources.Res
import communitydaynavigationapp.composeapp.generated.resources.ic_daterange
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.communityday.navigation.events.data.Conference // Import your Conference model
import org.communityday.navigation.events.data.EventRepository // Import your Repository
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun AddConferenceScreen(
    repository: EventRepository,
    onConferenceCreated: (String) -> Unit,
    onBack: () -> Unit
) {
    // State variables
    var name by remember { mutableStateOf("") }
    var confId by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(true) } // Privacy Toggle
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) } // Date State

    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Date Formatter Helper (Simple for demo)
    val dateText = selectedDateMillis?.let { millis ->
        val instant = Instant.fromEpochMilliseconds(millis)
        val date = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${date.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${date.dayOfMonth}, ${date.year}"
    } ?: "Select Conference Date"

    // Colors
    val NavyBlue = Color(0xFF000033)
    val Turquoise = Color(0xFF40E0D0)
    val Silver = Color(0xFFC0C0C0)

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK", color = Turquoise) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(NavyBlue).padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Conference", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Spacer(Modifier.height(24.dp))

        // Basic Info Fields (Name, ID, Location)
        StandardTextField(name, { name = it }, "Conference Name", Turquoise, Silver)
        Spacer(Modifier.height(12.dp))

        // Conditional ID Label: If private, call it "Access Code", if public, call it "ID"
        StandardTextField(confId, { confId = it }, if (isPublic) "Conference ID" else "Access Code", Turquoise, Silver)
        Spacer(Modifier.height(12.dp))

        StandardTextField(location, { location = it }, "Location", Turquoise, Silver)

        Spacer(Modifier.height(24.dp))

        // --- Privacy Toggle Section ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Privacy", color = Color.White, fontWeight = FontWeight.Bold)
                Text(
                    text = if (isPublic) "Public (Anyone can join)" else "Private (Code required)",
                    color = Silver,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(
                checked = isPublic,
                onCheckedChange = { isPublic = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Turquoise)
            )
        }

        Spacer(Modifier.height(24.dp))

        // --- Date Picker Button ---
        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Silver.copy(alpha = 0.5f))
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_daterange),
                contentDescription = "Select Date",
                tint = Silver
            )
            Spacer(Modifier.width(8.dp))
            Text(dateText, color = Color.White)
        }

        Spacer(Modifier.height(40.dp))

        if (isLoading) {
            CircularProgressIndicator(color = Turquoise)
        } else {
            Button(
                onClick = {
                    if (name.isNotBlank() && confId.isNotBlank() && selectedDateMillis != null) {
                        scope.launch {
                            isLoading = true

                            val isoDateString = selectedDateMillis?.let { millis ->
                                val instant = Instant.fromEpochMilliseconds(millis)
                                val localDateTime = instant.toLocalDateTime(TimeZone.UTC)
                                localDateTime.date.toString() // "YYYY-MM-DD"
                            } ?: ""

                            // Pass the new fields to your model
                            val newConf = Conference(
                                joinCode = confId,
                                name = name,
                                isPublic = isPublic,
                                dateString = isoDateString
                            )
                            val result = repository.createConference(newConf)
                            isLoading = false
                            if (result.isSuccess) onConferenceCreated(confId)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Turquoise)
            ) {
                Text("Save and Manage Events", color = NavyBlue, fontWeight = FontWeight.Bold)
            }
        }
        TextButton(onClick = onBack) { Text("Cancel", color = Color.White) }
    }
}

// Helper to keep the main code clean
@Composable
fun StandardTextField(value: String, onValueChange: (String) -> Unit, label: String, accent: Color, silver: Color) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = silver,
            focusedBorderColor = accent,
            unfocusedBorderColor = silver.copy(alpha = 0.7f),
            focusedLabelColor = accent,
            unfocusedLabelColor = silver.copy(alpha = 0.8f)
        )
    )
}