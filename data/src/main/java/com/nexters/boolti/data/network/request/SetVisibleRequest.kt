package com.nexters.boolti.data.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SetVisibleRequest(
    @SerialName("visible")
    val visible: Boolean,
)
