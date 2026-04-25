package org.communityday.navigation.events.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Conference(
    @SerialName("objectID") val objectID: String = "",
    val joinCode: String = "",
    val name: String = "",
    val isPublic: Boolean = true,
    val organization: String = "",
    val description: String = "",
    val isPublished: Boolean = false,
    val ownerId: String = ""
)



