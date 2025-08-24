package com.nexters.boolti.data.network.request

import kotlinx.serialization.Serializable

@Serializable
data class SetVisibleRequest(
    val visible: Boolean,
)
