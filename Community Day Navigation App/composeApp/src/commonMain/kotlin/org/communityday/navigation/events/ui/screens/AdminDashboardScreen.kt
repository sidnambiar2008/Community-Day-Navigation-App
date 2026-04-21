package org.communityday.navigation.events.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.communityday.navigation.events.data.EventRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items //
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import communitydaynavigationapp.composeapp.generated.resources.Res
import communitydaynavigationapp.composeapp.generated.resources.ic_schedule
import org.jetbrains.compose.resources.vectorResource
import communitydaynavigationapp.composeapp.generated.resources.ic_delete
import org.communityday.navigation.events.utils.convertTimeToMinutes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun AdminDashboardScreen(
    confId: String,
    repository: EventRepository,
    onBack: () -> Unit,
    Turquoise: Color
) {
    // State for tracking which item we are editing
    var editingEvent by remember { mutableStateOf<org.communityday.navigation.events.data.Event?>(null) }
    var editingBooth by remember { mutableStateOf<org.communityday.navigation.events.data.Booth?>(null) }

    var showEventDialog by remember { mutableStateOf(false) }
    var showBoothDialog by remember { mutableStateOf(false) }

    // Fetch the data
    val events by repository.getEventsStream(confId).collectAsState(initial = emptyList())
    val booths by repository.getBoothsStream(confId).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()


    Scaffold { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. HEADER & BUTTONS
                item {
                    Text("Edit Conference: $confId", style = MaterialTheme.typography.headlineSmall)
                    // The Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // BUTTON TO ADD EVENT
                        Button(
                            onClick = {
                                editingEvent = null // Ensure it's fresh, not an edit
                                showEventDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Turquoise)
                        ) {
                            Text("Add Event", color = Color.Black)
                        }

                        // BUTTON TO ADD BOOTH
                        Button(
                            onClick = {
                                editingBooth = null
                                showBoothDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Turquoise)
                        ) {
                            Text("Add Booth", color = Color.Black)
                        }
                    }
                }

                // 2. EVENTS SECTION
                item { Text("Events", style = MaterialTheme.typography.titleLarge) }

                if (events.isEmpty()) {
                    item { Text("No events added yet.", color = Color.Gray) }
                } else {
                    items(events) { event ->
                        AdminCard(
                            title = event.title,
                            onClick = { editingEvent = event; showEventDialog = true },
                            onDelete = { scope.launch { repository.deleteEvent(confId, event.id) } }
                        )
                    }
                }

                // 3. BOOTHS SECTION
                item {
                    Spacer(Modifier.height(16.dp))
                    Text("Booths", style = MaterialTheme.typography.titleLarge)
                }

                if (booths.isEmpty()) {
                    item { Text("No booths added yet.", color = Color.Gray) }
                } else {
                    items(booths) { booth ->
                        AdminCard(
                            title = booth.name,
                            onClick = { editingBooth = booth; showBoothDialog = true },
                            onDelete = { scope.launch { repository.deleteBooth(confId, booth.id) } }
                        )
                    }
                }
                item {
                    Spacer(Modifier.height(24.dp))
                    TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                        Text("Exit Dashboard")
                    }
                }
            }

        // Dialogs
        if (showEventDialog) {
            AddEventDialog(
                confId = confId,
                repository = repository,
                initialEvent = editingEvent, // 👈 Pass the item we clicked
                onDismiss = { showEventDialog = false },
                onSuccess = { showEventDialog = false }
            )
        }

        if (showBoothDialog) {
            AddBoothDialog(
                confId = confId,
                repository = repository,
                initialBooth = editingBooth, // 👈 Add similar logic to Booth Dialog
                onDismiss = { showBoothDialog = false },
                onSuccess = { showBoothDialog = false }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddEventDialog(
    confId: String,
    repository: EventRepository,
    initialEvent: org.communityday.navigation.events.data.Event? = null, // A Null Event
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {

    var title by remember(initialEvent) { mutableStateOf(initialEvent?.title ?: "") }
    var description by remember(initialEvent) { mutableStateOf(initialEvent?.description ?: "") }
    var startTime by remember(initialEvent) { mutableStateOf(initialEvent?.startTime ?: "") }
    var endTime by remember(initialEvent) { mutableStateOf(initialEvent?.endTime ?: "") }
    var latText by remember(initialEvent) { mutableStateOf(initialEvent?.latitude?.toString() ?: "") }
    var lonText by remember(initialEvent) { mutableStateOf(initialEvent?.longitude?.toString() ?: "") }

    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    // ... title and description states ...

    // States for controlling the pickers
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val startTimeState = rememberTimePickerState(is24Hour = false)
    val endTimeState = rememberTimePickerState(is24Hour = false)
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialEvent == null) "Add New Event" else "Edit Event") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // START TIME FIELD
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = {}, // Read-only
                        label = { Text("Start Time") },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = { showStartPicker = true }) {
                                Icon(vectorResource(Res.drawable.ic_schedule), contentDescription = null)
                            }
                        }
                    )
                    // END TIME FIELD
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = {}, // Read-only
                        label = { Text("End Time") },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = { showEndPicker = true }) {
                                Icon(vectorResource(Res.drawable.ic_schedule), contentDescription = null)
                            }
                        }
                    )
                }

                // --- PICKER DIALOGS ---
                if (showStartPicker) {
                    TimeSelectionDialog(
                        state = startTimeState,
                        onDismiss = { showStartPicker = false },
                        onConfirm = {
                            startTime = formatTime(startTimeState.hour, startTimeState.minute)
                            showStartPicker = false
                        }
                    )
                }

                if (showEndPicker) {
                    TimeSelectionDialog(
                        state = endTimeState,
                        onDismiss = { showEndPicker = false },
                        onConfirm = {
                            endTime = formatTime(endTimeState.hour, endTimeState.minute)
                            showEndPicker = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = latText,
                        onValueChange = { latText = it },
                        label = { Text("Lat") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = lonText,
                        onValueChange = { lonText = it },
                        label = { Text("Long") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = title.isNotBlank() && latText.isNotBlank() && lonText.isNotBlank() && !isSaving,
                onClick = {
                    val startMin = convertTimeToMinutes(startTime)
                    val endMin = convertTimeToMinutes(endTime)

                    if (endMin <= startMin) {
                        errorMessage = "End time must be after start time."
                    } else {
                        errorMessage = null
                    scope.launch {
                        isSaving = true
                        val eventData = org.communityday.navigation.events.data.Event(
                            id = initialEvent?.id ?: "", // Keep ID if it exists
                            title = title,
                            description = description,
                            startTime = startTime,
                            endTime = endTime,
                            latitude = latText.toDoubleOrNull() ?: 0.0,
                            longitude = lonText.toDoubleOrNull() ?: 0.0,
                            sortOrder = 1
                        )

                        // Switch between add and update
                        val result = if (initialEvent == null) {
                            repository.addEvent(confId, eventData)
                        } else {
                            repository.updateEvent(
                                confId,
                                eventData
                            ) // 👈 Use the update function we wrote
                        }

                        isSaving = false
                             if (result.isSuccess) onSuccess()
                        }
                    }
                }
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddBoothDialog(
    confId: String,
    repository: EventRepository,
    initialBooth: org.communityday.navigation.events.data.Booth? = null, // 👈 Added this
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var name by remember(initialBooth) { mutableStateOf(initialBooth?.name ?: "") }
    var latText by remember(initialBooth) { mutableStateOf(initialBooth?.latitude?.toString() ?: "") }
    var lonText by remember(initialBooth) { mutableStateOf(initialBooth?.longitude?.toString() ?: "") }
    var description by remember(initialBooth) { mutableStateOf(initialBooth?.description?: "")}
    var isSaving by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialBooth == null) "Add New Booth" else "Edit Booth") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Company / Organization Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = latText,
                        onValueChange = { latText = it },
                        label = { Text("Lat") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = lonText,
                        onValueChange = { lonText = it },
                        label = { Text("Long") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Brief Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                enabled = name.isNotBlank() && latText.isNotBlank() && lonText.isNotBlank() && !isSaving,
                onClick = {
                    scope.launch {
                        isSaving = true
                        // Ensure this matches your Booth data class exactly!
                        val newBooth = org.communityday.navigation.events.data.Booth(
                            name = name,
                            id = initialBooth?.id ?: "",
                            latitude = latText.toDoubleOrNull()?:0.0,
                            longitude = lonText.toDoubleOrNull()?:0.0,
                            description = description
                        )
                        // Make sure your repository has an addBooth function!
                        val result = if (initialBooth == null) {
                            repository.addBooth(confId, newBooth)
                        }
                        else
                        {
                            repository.updateBooth(confId, newBooth)
                        }

                        isSaving = false
                        if (result.isSuccess) {
                            onSuccess()
                        }
                        else
                        {
                            println("Error: ${result.exceptionOrNull()?.message}")
                        }
                    }
                }
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Save Booth")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AdminCard(
    title: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick // Tapping the card = EDIT
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, modifier = Modifier.weight(1f))

            IconButton(onClick = onDelete) { // Tapping the trash = DELETE
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_delete),
                    contentDescription = "Time",
                    tint = Color.Red,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    val amPm = if (hour < 12) "AM" else "PM"
    val h = if (hour % 12 == 0) 12 else hour % 12
    val m = minute.toString().padStart(2, '0')
    return "$h:$m $amPm"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelectionDialog(
    state: TimePickerState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        text = {
            TimePicker(state = state)
        }
    )
}
