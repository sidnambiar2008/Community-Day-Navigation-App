package org.communityday.navigation.events.data

import kotlinx.serialization.Serializable

@Serializable
data class Booth(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val organization: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val location: String,
    val ownerId: String = "",
    val category: String = "Exhibitor",
    val imageUrl: String? = null)
{

}

